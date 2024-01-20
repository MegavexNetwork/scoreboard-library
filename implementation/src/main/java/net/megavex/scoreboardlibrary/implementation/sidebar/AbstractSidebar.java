package net.megavex.scoreboardlibrary.implementation.sidebar;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.player.PlayerDisplayable;
import net.megavex.scoreboardlibrary.implementation.player.ScoreboardLibraryPlayer;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.PlayerNameProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.empty;

public abstract class AbstractSidebar implements Sidebar, PlayerDisplayable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final ObjectivePacketAdapter packetAdapter;
  private final List<String> linePlayerNames;

  private final GlobalLineInfo[] lines;
  private Component title = empty();

  private final Set<Player> players = CollectionProvider.set(8);

  private boolean closed;

  private final Queue<SidebarTask> taskQueue = new ConcurrentLinkedQueue<>();

  public AbstractSidebar(@NotNull ScoreboardLibraryImpl scoreboardLibrary, int maxLines) {
    this.scoreboardLibrary = scoreboardLibrary;

    String objectiveName = UUID.randomUUID().toString().substring(0, 5);
    this.packetAdapter = scoreboardLibrary.packetAdapter().createObjectiveAdapter(objectiveName);
    this.linePlayerNames = PlayerNameProvider.provideLinePlayerNames(maxLines);
    this.lines = new GlobalLineInfo[maxLines];
  }

  protected abstract @NotNull Set<Player> internalPlayers();

  protected abstract void forEachLineHandler(@NotNull Consumer<LocaleLineHandler> consumer);

  protected abstract @Nullable LocaleLineHandler addPlayer0(@NotNull Player player);

  protected abstract @Nullable LocaleLineHandler removePlayer0(@NotNull Player player);

  @Override
  public final void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(SidebarTask.Close.INSTANCE);
    }
  }

  @Override
  public final boolean closed() {
    return closed;
  }

  @Override
  public final @NotNull Collection<Player> players() {
    return closed ? Collections.emptySet() : Collections.unmodifiableSet(players);
  }

  @Override
  public final boolean addPlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (players.add(player)) {
      taskQueue.add(new SidebarTask.AddPlayer(player));
      return true;
    }

    return false;
  }

  @Override
  public final boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (players.remove(player)) {
      taskQueue.add(new SidebarTask.RemovePlayer(player));
      return true;
    }

    return false;
  }

  @Override
  public final @Range(from = 1, to = MAX_LINES) int maxLines() {
    return lines.length;
  }

  @Override
  public final void line(@Range(from = 0, to = MAX_LINES - 1) int line, @Nullable Component value) {
    checkClosed();

    GlobalLineInfo globalLineInfo = getLineInfo(line);
    if (!Objects.equals(globalLineInfo.value(), value)) {
      globalLineInfo.value(value);
      taskQueue.add(new SidebarTask.UpdateLine(line));
      updateScores();
    }
  }

  @Override
  public final @Nullable Component line(@Range(from = 0, to = MAX_LINES - 1) int line) {
    checkClosed();

    GlobalLineInfo globalLineInfo = lines[line];
    return globalLineInfo == null ? null : globalLineInfo.value();
  }

  @Override
  public final @NotNull Component title() {
    return title;
  }

  @Override
  public final void title(@NotNull Component title) {
    Preconditions.checkNotNull(title);
    checkClosed();

    if (!Objects.equals(this.title, title)) {
      this.title = title;
      taskQueue.add(SidebarTask.UpdateTitle.INSTANCE);
    }
  }

  public final @NotNull ScoreboardLibraryImpl scoreboardLibrary() {
    return scoreboardLibrary;
  }

  public final ObjectivePacketAdapter packetAdapter() {
    return packetAdapter;
  }

  public final @Nullable GlobalLineInfo[] lines() {
    return lines;
  }

  public final @NotNull Queue<SidebarTask> taskQueue() {
    return taskQueue;
  }

  @Override
  public final void display(@NotNull Player player) {
    packetAdapter.sendProperties(players, PropertiesPacketType.CREATE, title, ObjectiveRenderType.INTEGER);

    LocaleLineHandler lineHandler = Objects.requireNonNull(addPlayer0(player));
    lineHandler.addPlayer(player);
    lineHandler.show(player);
    packetAdapter.display(Collections.singleton(player), ObjectiveDisplaySlot.sidebar());
  }

  public final boolean tick() {
    while (true) {
      SidebarTask task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof SidebarTask.Close) {
        forEachLineHandler(LocaleLineHandler::hide);
        packetAdapter.remove(internalPlayers());

        for (Player player : internalPlayers()) {
          Objects.requireNonNull(scoreboardLibrary.getPlayer(player)).sidebarQueue().remove(this);
        }

        return false;
      } else if (task instanceof SidebarTask.AddPlayer) {
        SidebarTask.AddPlayer addPlayerTask = (SidebarTask.AddPlayer) task;
        ScoreboardLibraryPlayer slPlayer = scoreboardLibrary.getOrCreatePlayer(addPlayerTask.player());
        slPlayer.sidebarQueue().add(this);
      } else if (task instanceof SidebarTask.RemovePlayer) {
        SidebarTask.RemovePlayer removePlayerTask = (SidebarTask.RemovePlayer) task;

        LocaleLineHandler lineHandler = removePlayer0(removePlayerTask.player());
        if (lineHandler != null) {
          lineHandler.hide(removePlayerTask.player());
          lineHandler.removePlayer(removePlayerTask.player());
          packetAdapter.remove(Collections.singleton(removePlayerTask.player()));
        }

        Objects.requireNonNull(scoreboardLibrary.getPlayer(removePlayerTask.player())).sidebarQueue().remove(this);
      } else if (task instanceof SidebarTask.ReloadPlayer) {
        SidebarTask.ReloadPlayer reloadPlayerTask = (SidebarTask.ReloadPlayer) task;
        LocaleLineHandler lineHandler = removePlayer0(reloadPlayerTask.player());
        if (lineHandler == null) {
          continue;
        }

        packetAdapter.remove(Collections.singleton(reloadPlayerTask.player()));
        lineHandler.hide(reloadPlayerTask.player());
        lineHandler.removePlayer(reloadPlayerTask.player());

        display(reloadPlayerTask.player());
      } else if (task instanceof SidebarTask.UpdateLine) {
        SidebarTask.UpdateLine updateLineTask = (SidebarTask.UpdateLine) task;
        forEachLineHandler(sidebar -> {
          Component value = lines[updateLineTask.line()].value();
          Component renderedValue = value == null ? null : GlobalTranslator.render(value, sidebar.locale());
          sidebar.updateLine(updateLineTask.line(), renderedValue);
        });
      } else if (task instanceof SidebarTask.UpdateScores) {
        forEachLineHandler(LocaleLineHandler::updateScores);
      } else if (task instanceof SidebarTask.UpdateTitle) {
        packetAdapter.sendProperties(internalPlayers(), PropertiesPacketType.UPDATE, title, ObjectiveRenderType.INTEGER);
      }
    }
    return true;
  }

  private void updateScores() {
    int size = 0;
    for (GlobalLineInfo line : lines) {
      if (line != null && line.value() != null) {
        size++;
      }
    }

    boolean changed = false;
    int i = 0;

    for (GlobalLineInfo line : lines) {
      if (line != null && line.value() != null) {
        int newScore = size - i - 1;
        if (line.objectiveScore() != newScore) {
          changed = true;
          line.updateScore(true);
          line.objectiveScore(newScore);
        }

        i++;
      }
    }

    if (changed) {
      taskQueue.add(SidebarTask.UpdateScores.INSTANCE);
    }
  }

  private @NotNull GlobalLineInfo getLineInfo(int line) {
    GlobalLineInfo globalLineInfo = lines[line];
    if (globalLineInfo == null) {
      globalLineInfo = lines[line] = new GlobalLineInfo(this, linePlayerNames.get(line), line);
      updateScores();
    }

    return globalLineInfo;
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("Sidebar is closed");
    }
  }
}
