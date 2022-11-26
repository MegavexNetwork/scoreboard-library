package net.megavex.scoreboardlibrary.implementation.sidebar;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static net.kyori.adventure.text.Component.empty;

public abstract class AbstractSidebar implements Sidebar {
  public final GlobalLineInfo[] lines;
  protected final Object lock = new Object();
  protected final Object visibilityLock = new Object();
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final AtomicBoolean updateTitle = new AtomicBoolean(),
    updateLines = new AtomicBoolean();
  protected volatile boolean closed, visible, visibilityChanged;
  private Component title = empty();
  private volatile SidebarPacketAdapter<?, ?> packetAdapter;

  public AbstractSidebar(ScoreboardLibraryImpl scoreboardLibrary, int maxLines) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.lines = new GlobalLineInfo[maxLines];
  }

  protected abstract void forEachSidebar(Consumer<SidebarLineHandler> consumer);

  protected abstract SidebarLineHandler addPlayer0(Player player);

  protected abstract SidebarLineHandler removePlayer0(Player player);

  protected void updateScores() {
    byte size = 0;
    for (var line : lines) {
      if (line != null && line.value != null) {
        size++;
      }
    }

    byte i = 0;
    for (var line : lines) {
      if (line != null && line.value != null) {
        byte oldScore = line.objectiveScore;
        line.objectiveScore = (byte) (size - i - 1);
        if (line.objectiveScore != oldScore) line.updateScore = true;
        i++;
      }
    }
  }

  @Override
  public @NotNull ScoreboardLibraryImpl scoreboardLibrary() {
    return scoreboardLibrary;
  }

  @Override
  public int maxLines() {
    return lines.length;
  }

  public void update() {
    if (closed) return;

    synchronized (lock) {
      if (closed) return;
      Collection<Player> players = players();

      if (visibilityChanged) {
        synchronized (visibilityLock) {
          if (visibilityChanged) {
            if (visible) {
              sidebarBridge().create(players);
              forEachSidebar(SidebarLineHandler::show);
              scoreboardLibrary.packetAdapter.displaySidebar(players);
            } else {
              forEachSidebar(SidebarLineHandler::hide);
              scoreboardLibrary.packetAdapter.removeSidebar(players);
            }

            visibilityChanged = false;
          }
        }
      }

      if (updateTitle.getAndSet(false)) {
        sidebarBridge().updateTitle(title);
        if (visible) {
          packetAdapter.update(players);
        }
      }

      if (updateLines.getAndSet(false)) {
        updateScores();

        boolean updateTeams = false;
        for (var line : lines) {
          if (line == null || !line.update) continue;
          line.updateTeams = true;
          updateTeams = true;

          forEachSidebar(s -> {
            if (line.value != null) {
              Component rendered = GlobalTranslator.render(line.value, s.locale());
              s.setLine(line.line, rendered);
            } else {
              s.setLine(line.line, null);
            }
          });

          line.update = false;
        }

        if (updateTeams) {
          forEachSidebar(SidebarLineHandler::update);

          for (var line : lines) {
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

    synchronized (lock) {
      SidebarLineHandler sidebar = addPlayer0(player);
      if (sidebar == null) return false;

      LineType lineType = LineType.getType(this, player);
      sidebar.playersInit(lineType).add(player);

      scoreboardLibrary.sidebarMap.put(player, this);

      if (visible) {
        var singleton = Set.of(player);
        sidebarBridge().create(singleton);
        sidebar.show(singleton, lineType);
        scoreboardLibrary.packetAdapter.displaySidebar(singleton);
      }
    }

    return true;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    checkClosed();

    synchronized (lock) {
      SidebarLineHandler sidebar = removePlayer0(player);
      if (sidebar == null) return false;

      LineType lineType = LineType.getType(this, player);
      sidebar.players(lineType).remove(player);

      scoreboardLibrary.sidebarMap.remove(player, this);

      if (visible && !visibilityChanged) {
        var singleton = Set.of(player);
        sidebar.hide(singleton, lineType);
        scoreboardLibrary.packetAdapter.removeSidebar(singleton);
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
  public @NotNull Component title() {
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
    return info == null ? null:info.value;
  }

  public GlobalLineInfo getLineInfo(int line) {
    GlobalLineInfo info = lines[line];
    if (info == null) {
      info = new GlobalLineInfo(this, (byte) line);
      lines[line] = info;
    }
    return info;
  }

  @Override
  public void title(@NotNull Component title) {
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
      if (closed) return;
      removePlayers(ImmutableList.copyOf(players()));
      visible = false;
      closed = true;
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public SidebarPacketAdapter<?, ?> sidebarBridge() {
    if (packetAdapter == null)
      synchronized (lock) {
        if (packetAdapter == null) packetAdapter = scoreboardLibrary.packetAdapter.createSidebarPacketAdapter(this);
      }

    return packetAdapter;
  }

  private void checkPlayer(Player player) {
    // TODO: remove this restriction
    Sidebar sidebar = scoreboardLibrary.sidebarMap.get(player);
    if (sidebar != null && sidebar != this) {
      throw new IllegalArgumentException("player " + player.getName() + " already has a Sidebar");
    }
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
