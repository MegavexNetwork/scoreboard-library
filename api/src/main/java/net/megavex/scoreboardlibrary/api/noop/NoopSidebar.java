package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

import static net.kyori.adventure.text.Component.empty;

class NoopSidebar implements Sidebar {
  private final Set<Player> players = new HashSet<>();
  private final int maxLines;
  private final String objectiveName;
  private final Locale locale;
  private final Component[] lines;
  private Component title = empty();
  private boolean closed;

  NoopSidebar(int maxLines, String objectiveName, @Nullable Locale locale) {
    this.maxLines = maxLines;
    this.locale = locale;
    this.objectiveName = objectiveName;
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
  public @NotNull Collection<Player> players() {
    return closed ? Collections.emptySet() : Collections.unmodifiableSet(players);
  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();
    return players.add(player);
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();
    return players.remove(player);
  }

  @Override
  public @Range(from = 1, to = MAX_LINES) int maxLines() {
    return maxLines;
  }

  @Override
  public @NotNull String objectiveName() {
    return objectiveName;
  }

  @Override
  public @Nullable Locale locale() {
    return locale;
  }

  @Override
  public void line(int index, @Nullable ComponentLike value, @Nullable ScoreFormat scoreFormat) {
    checkLineBounds(index);
    checkClosed();
    lines[index] = value == null ? null : value.asComponent();
  }

  @Override
  public void refreshLine(@Range(from = 0, to = Integer.MAX_VALUE - 1) final int index) {

  }
  @Override
  public @Nullable Component line(int line) {
    checkLineBounds(line);
    checkClosed();
    return lines[line];
  }

  @Override
  public @NotNull Component title() {
    return title;
  }

  @Override
  public void title(@NotNull ComponentLike title) {
    Preconditions.checkNotNull(title);
    checkClosed();
    this.title = title.asComponent();
  }

  @Override
  public void refreshTitle() {

  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("NoopSidebar is closed");
    }
  }

  private void checkLineBounds(int line) {
    if (line >= maxLines || line < 0) {
      throw new IndexOutOfBoundsException("invalid line " + line);
    }
  }
}
