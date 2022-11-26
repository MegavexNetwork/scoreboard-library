package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;


import static net.kyori.adventure.text.Component.empty;

class NoopSidebar implements Sidebar {
  private final NoopScoreboardLibrary scoreboardLibrary;
  private final Set<Player> players = new HashSet<>();
  private final int maxLines;
  private final Locale locale;
  private final Component[] lines;
  private Component title = empty();
  private boolean visible, closed;

  NoopSidebar(@NotNull NoopScoreboardLibrary scoreboardLibrary, int maxLines, @Nullable Locale locale) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.maxLines = maxLines;
    this.locale = locale;
    this.lines = new Component[maxLines];
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public @NotNull ScoreboardLibrary scoreboardLibrary() {
    return scoreboardLibrary;
  }

  @Override
  public @NotNull Collection<Player> players() {
    return closed ? Set.of():Collections.unmodifiableSet(players);
  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    return players.add(player);
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    return players.remove(player);
  }

  @Override
  public @Range(from = 1, to = MAX_LINES) int maxLines() {
    return maxLines;
  }

  @Override
  public @Nullable Locale locale() {
    return locale;
  }

  @Override
  public boolean visible() {
    return visible;
  }

  @Override
  public void visible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public void line(int line, @Nullable Component value) {
    SidebarUtilities.checkLineBounds(maxLines, line);
    lines[line] = value;
  }

  @Override
  public @Nullable Component line(int line) {
    SidebarUtilities.checkLineBounds(maxLines, line);
    return lines[line];
  }

  @Override
  public @NotNull Component title() {
    return title;
  }

  @Override
  public void title(@NotNull Component title) {
    Preconditions.checkNotNull(title);
    this.title = title;
  }
}
