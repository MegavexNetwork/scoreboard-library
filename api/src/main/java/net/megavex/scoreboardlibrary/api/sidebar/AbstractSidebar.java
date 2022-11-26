package net.megavex.scoreboardlibrary.api.sidebar;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;


import static net.kyori.adventure.text.Component.empty;

public class AbstractSidebar implements HasScoreboardLibrary, Closeable {
  protected final Sidebar sidebar;
  private final SidebarLine[] lines;
  private boolean closed;

  public AbstractSidebar(@NotNull Sidebar sidebar) {
    this.sidebar = sidebar;
    this.lines = new SidebarLine[sidebar.maxLines()];
  }

  public final @NotNull Sidebar sidebar() {
    return sidebar;
  }

  @ApiStatus.OverrideOnly
  protected void onClosed() {
  }

  /**
   * Registers a static line for this AbstractSidebar
   *
   * @param line  Line to register
   * @param value Static value
   */
  protected final void registerStaticLine(@Range(from = 0, to = Sidebar.MAX_LINES - 1) int line, @NotNull Component value) {
    SidebarUtilities.checkLineBounds(line);
    checkLineAvailable(line);
    Preconditions.checkNotNull(value);
    checkClosed();

    lines[line] = new SidebarLine(line);
    sidebar.line(line, value);
  }

  /**
   * Registers a line that can change its value
   *
   * @param line         Line to register
   * @param lineSupplier Line value supplier
   * @return The created LineSupplier
   */
  protected final @NotNull DynamicSidebarLine registerDynamicLine(@Range(from = 0, to = Sidebar.MAX_LINES - 1) int line, @NotNull Supplier<@Nullable Component> lineSupplier) {
    SidebarUtilities.checkLineBounds(line);
    checkLineAvailable(line);
    Preconditions.checkNotNull(lineSupplier);
    checkClosed();

    var sidebarLine = new DynamicSidebarLine(line, lineSupplier);
    lines[line] = sidebarLine;
    sidebar.line(line, lineSupplier.get());
    return sidebarLine;
  }

  /**
   * Registers an empty line
   *
   * @param line Line to register
   */
  protected final void registerEmptyLine(@Range(from = 0, to = Sidebar.MAX_LINES - 1) int line) {
    registerStaticLine(line, empty());
  }

  /**
   * Unregisters a line
   *
   * @param line Line to unregister
   */
  protected final void unregisterLine(@NotNull SidebarLine line) {
    Preconditions.checkNotNull(line);
    checkClosed();

    lines[line.line] = null;
    sidebar.line(line.line, null);
  }

  /**
   * Gets the {@link SidebarLine} of an index
   *
   * @param line Line
   * @return Supplier of line
   */
  protected @Nullable SidebarLine getLine(@Range(from = 0, to = Sidebar.MAX_LINES - 1) int line) {
    SidebarUtilities.checkLineBounds(line);
    checkClosed();
    return lines[line];
  }

  @Override
  public final @NotNull ScoreboardLibrary scoreboardLibrary() {
    return sidebar.scoreboardLibrary();
  }

  @Override
  public final void close() {
    close(true);
  }

  /**
   * @param closeWrappedSidebar Whether to close the wrapped {@link Sidebar} or just clear all lines from it
   */
  public final void close(boolean closeWrappedSidebar) {
    if (closed) return;
    closed = true;

    if (closeWrappedSidebar) {
      sidebar.close();
    } else {
      for (var line : lines) {
        if (line != null) {
          sidebar.line(line.line, null);
        }
      }
    }

    onClosed();
  }

  @Override
  public final boolean closed() {
    return closed;
  }

  @Override
  public int hashCode() {
    return sidebar.hashCode();
  }

  protected final void checkClosed() {
    Preconditions.checkState(!closed, "AbstractSidebar is closed");
  }

  private void checkLineAvailable(int line) {
    if (lines[line] != null) {
      throw new IllegalArgumentException("line " + line + " is already registered");
    }
  }

  public sealed class SidebarLine permits DynamicSidebarLine {
    protected final int line;

    private SidebarLine(int line) {
      this.line = line;
    }
  }

  public final class DynamicSidebarLine extends SidebarLine {
    private final Supplier<Component> function;

    private DynamicSidebarLine(int line, @NotNull Supplier<Component> function) {
      super(line);
      this.function = function;
    }

    public void update() {
      sidebar.line(line, function.get());
    }
  }
}
