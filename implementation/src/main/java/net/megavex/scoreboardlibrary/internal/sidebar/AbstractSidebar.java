package net.megavex.scoreboardlibrary.internal.sidebar;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.SidebarNMS;
import net.megavex.scoreboardlibrary.internal.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import net.megavex.scoreboardlibrary.internal.sidebar.line.locale.LineType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.empty;

public abstract class AbstractSidebar implements Sidebar {

  public final GlobalLineInfo[] lines;
  protected final Object lock = new Object();
  protected final Object visibilityLock = new Object();
  protected final Object playerLock = new Object();
  private final ScoreboardManager scoreboardManager;
  private final ComponentTranslator componentTranslator;
  private final AtomicBoolean updateTitle = new AtomicBoolean(),
    updateLines = new AtomicBoolean();
  protected volatile boolean closed, visible, visibilityChanged;
  private Component title = empty();
  private volatile SidebarNMS<?, ?> nms;

  public AbstractSidebar(ScoreboardManager scoreboardManager, ComponentTranslator componentTranslator, int size) {
    this.scoreboardManager = scoreboardManager;
    this.componentTranslator = componentTranslator;
    SidebarUtilities.checkLineBounds(size);
    this.lines = new GlobalLineInfo[size];
  }

  protected abstract void forEachSidebar(Consumer<SidebarLineHandler> consumer);

  protected abstract SidebarLineHandler addPlayer0(Player player);

  protected abstract SidebarLineHandler removePlayer0(Player player);

  protected void updateScores() {
    byte size = 0;
    for (GlobalLineInfo line : lines) {
      if (line != null && line.value != null) {
        size++;
      }
    }

    byte i = 0;
    for (GlobalLineInfo line : lines) {
      if (line != null && line.value != null) {
        byte oldScore = line.objectiveScore;
        line.objectiveScore = (byte) (size - i - 1);
        if (line.objectiveScore != oldScore) line.updateScore = true;
        i++;
      }
    }
  }

  @Override
  public @NotNull ScoreboardManager scoreboardManager() {
    return scoreboardManager;
  }

  @Override
  public @NotNull ComponentTranslator componentTranslator() {
    return componentTranslator;
  }

  @Override
  public byte maxLines() {
    return (byte) lines.length;
  }

  public void update() {
    if (closed) return;

    synchronized (lock) {
      if (closed) return;

      synchronized (playerLock) {
        Collection<Player> players = players();

        if (visibilityChanged) {
          synchronized (visibilityLock) {
            if (!visibilityChanged) return;

            if (visible) {
              sidebarBridge().create(players);
              forEachSidebar(SidebarLineHandler::show);
              ScoreboardManagerNMS.INSTANCE.displaySidebar(players);
            } else {
              forEachSidebar(SidebarLineHandler::hide);
              ScoreboardManagerNMS.INSTANCE.removeSidebar(players);
            }

            visibilityChanged = false;
          }
        }

        if (updateTitle.getAndSet(false)) {
          sidebarBridge().updateTitle(title);
          if (visible) {
            nms.update(players);
          }
        }
      }

      if (updateLines.getAndSet(false)) {
        updateScores();

        boolean updateTeams = false;
        for (GlobalLineInfo line : lines) {
          if (line == null || !line.update) continue;
          line.updateTeams = true;
          updateTeams = true;

          forEachSidebar(s -> {
            if (line.value != null) {
              Component rendered = componentTranslator.translate(line.value, s.locale());
              s.setLine(line.line, rendered);
            } else {
              s.setLine(line.line, null);
            }
          });

          line.update = false;
        }

        if (updateTeams) {
          forEachSidebar(SidebarLineHandler::update);

          for (GlobalLineInfo line : lines) {
            if (line != null) line.updateTeams = false;
          }
        }
      }
    }


  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    checkClosed();
    checkPlayer(player);

    synchronized (playerLock) {
      SidebarLineHandler sidebar = addPlayer0(player);
      if (sidebar == null) return false;

      LineType lineType = LineType.getType(player);
      sidebar.playersInit(lineType).add(player);

      ScoreboardManagerProviderImpl.instance().sidebarMap.put(player, this);

      if (visible) {
        var singleton = Collections.singleton(player);
        sidebarBridge().create(singleton);
        sidebar.show(singleton, lineType);
        ScoreboardManagerNMS.INSTANCE.displaySidebar(singleton);
      }
    }

    return true;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    checkClosed();

    synchronized (playerLock) {
      SidebarLineHandler sidebar = removePlayer0(player);
      if (sidebar == null) return false;

      LineType lineType = LineType.getType(player);
      sidebar.players(lineType).remove(player);

      ScoreboardManagerProviderImpl.instance().sidebarMap.remove(player, this);

      if (visible) {
        var singleton = Collections.singleton(player);
        sidebar.hide(singleton, lineType);
        ScoreboardManagerNMS.INSTANCE.removeSidebar(singleton);
      }
    }

    return true;
  }

  @Override
  public boolean visible() {
    return visible;
  }

  @Override
  public void visible(boolean visible) {
    checkClosed();

    if (this.visible == visible) return;

    synchronized (visibilityLock) {
      if (this.visible == visible) return;

      this.visible = visible;
      this.visibilityChanged = !visibilityChanged;
    }
  }

  @Override
  public Component title() {
    return title;
  }

  @Override
  public void line(int line, Component value) {
    checkClosed();
    checkLine(line);

    Component component = line(line);
    if (Objects.equals(value, component)) return;

    GlobalLineInfo lineInfo = getLineInfo(line);
    lineInfo.value = value;

    if (visible) {
      lineInfo.update = true;
      updateLines.set(true);
    }
  }

  @Override
  public @Nullable Component line(int line) {
    GlobalLineInfo info = lines[line];
    return info == null ? null : info.value;
  }

  public GlobalLineInfo getLineInfo(int line) {
    GlobalLineInfo info = lines[line];
    if (info == null) {
      info = new GlobalLineInfo((byte) line);
      lines[line] = info;
    }
    return info;
  }

  @Override
  public void title(Component title) {
    checkClosed();
    Preconditions.checkNotNull(title);

    if (!this.title.equals(title)) {
      this.title = title;
      updateTitle.set(true);
    }
  }

  @Override
  public void close() {
    synchronized (lock) {
      removePlayers(players());
      visible = false;
      closed = true;
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public SidebarNMS<?, ?> sidebarBridge() {
    if (nms == null)
      synchronized (lock) {
        if (nms == null) nms = ScoreboardManagerNMS.INSTANCE.createSidebarNMS(this);
      }

    return nms;
  }

  private void checkPlayer(Player player) {
    // TODO: remove this restriction
    Sidebar sidebar = ScoreboardManagerProviderImpl.instance().sidebar(player);
    if (sidebar != null && sidebar != this)
      throw new IllegalArgumentException("Player " + player.getName() + " already has a sidebar");
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("Sidebar is closed");
    }
  }

  private void checkLine(int line) {
    if (line > lines.length || line < 0) {
      throw new IndexOutOfBoundsException("Invalid line " + line);
    }
  }
}
