package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;


import static net.kyori.adventure.text.Component.empty;

public abstract class BetterAbstractSidebar implements Sidebar {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final SidebarPacketAdapter<?, ?> packetAdapter;

  private final GlobalLineInfo[] lines;
  private Component title = empty();

  private final Set<Player> players = CollectionProvider.set(8);

  private boolean closed;

  private final Queue<SidebarTask> taskQueue = new ConcurrentLinkedQueue<>();

  public BetterAbstractSidebar(@NotNull ScoreboardLibraryImpl scoreboardLibrary, int maxLines) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.packetAdapter = scoreboardLibrary.packetAdapter.createSidebarPacketAdapter(this);
    this.lines = new GlobalLineInfo[maxLines];
  }

  protected abstract void forEachSidebar(Consumer<LocaleLineHandler> consumer);

  protected abstract LocaleLineHandler addPlayer0(Player player);

  protected abstract LocaleLineHandler removePlayer0(Player player);

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(SidebarTask.Close.INSTANCE);
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public @NotNull ScoreboardLibraryImpl scoreboardLibrary() {
    return scoreboardLibrary;
  }

  @Override
  public @NotNull Collection<Player> players() {
    return closed ? Set.of() : Collections.unmodifiableSet(players);
  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    checkClosed();

    if (players.add(player)) {
      taskQueue.add(new SidebarTask.AddPlayer(player));
      return true;
    }

    return false;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    checkClosed();

    if (players.remove(player)) {
      taskQueue.add(new SidebarTask.RemovePlayer(player));
      return true;
    }

    return false;
  }

  @Override
  public @Range(from = 1, to = MAX_LINES) int maxLines() {
    return lines.length;
  }

  @Override
  public void line(@Range(from = 0, to = MAX_LINES - 1) int line, @Nullable Component value) {
    checkClosed();

    var globalLineInfo = lines[line];
    if (globalLineInfo == null) {
      globalLineInfo = lines[line] = new GlobalLineInfo(this, line);
    }

    globalLineInfo.value = value;
    taskQueue.add(new SidebarTask.UpdateLine(line));
  }

  @Override
  public @Nullable Component line(@Range(from = 0, to = MAX_LINES - 1) int line) {
    checkClosed();

    var globalLineInfo = lines[line];
    return globalLineInfo == null ? null : globalLineInfo.value;
  }

  @Override
  public @NotNull Component title() {
    return title;
  }

  @Override
  public void title(@NotNull Component title) {
    checkClosed();

    if (!Objects.equals(this.title, title)) {
      this.title = title;
      taskQueue.add(SidebarTask.UpdateTitle.INSTANCE);
    }
  }

  public @NotNull SidebarPacketAdapter<?, ?> packetAdapter() {
    return packetAdapter;
  }

  public @Nullable GlobalLineInfo[] lines() {
    return lines;
  }

  public @NotNull GlobalLineInfo getLineInfo(int line) {
    // TODO this probably needs to be thread-safe

    var globalLineInfo = lines[line];
    if (globalLineInfo == null) {
      globalLineInfo = lines[line] = new GlobalLineInfo(this, line);
    }

    return globalLineInfo;
  }

  public void tick() {
    while (true) {
      var task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof SidebarTask.Close) {
        // TODO
        return;
      } else if (task instanceof SidebarTask.AddPlayer addPlayerTask) {


      } else if (task instanceof SidebarTask.RemovePlayer removePlayerTask) {

      }
    }
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("Sidebar is closed");
    }
  }
}
