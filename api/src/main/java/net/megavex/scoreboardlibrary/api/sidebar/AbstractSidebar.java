package net.megavex.scoreboardlibrary.api.sidebar;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;
import javax.annotation.concurrent.NotThreadSafe;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;


import static net.kyori.adventure.text.Component.empty;

/**
 * @deprecated Use {@link net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebar} instead
 */
@NotThreadSafe
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
public class AbstractSidebar {
  protected final Sidebar sidebar;
  private final Line[] lines;
  private boolean closed;

  public AbstractSidebar(@NotNull Sidebar sidebar) {
    this.sidebar = sidebar;
    this.lines = new Line[sidebar.maxLines()];
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
    SidebarUtilities.checkLineBounds(sidebar.maxLines(), line);
    checkLineAvailable(line);
    Preconditions.checkNotNull(value);
    checkClosed();

    lines[line] = new Line(line);
    sidebar.line(line, value);
  }

  /**
   * Registers a line that can change its value
   *
   * @param line         Line to register
   * @param lineSupplier Line value supplier
   * @return The created LineSupplier
   */
  protected final @NotNull AbstractSidebar.DynamicLine registerDynamicLine(@Range(from = 0, to = Sidebar.MAX_LINES - 1) int line, @NotNull Supplier<@Nullable Component> lineSupplier) {
    SidebarUtilities.checkLineBounds(sidebar.maxLines(), line);
    checkLineAvailable(line);
    Preconditions.checkNotNull(lineSupplier);
    checkClosed();

    var sidebarLine = new DynamicLine(line, lineSupplier);
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
  protected final void unregisterLine(@NotNull AbstractSidebar.Line line) {
    Preconditions.checkNotNull(line);
    checkClosed();

    lines[line.line] = null;
    sidebar.line(line.line, null);
  }

  /**
   * Gets the {@link Line} of an index
   *
   * @param line Line
   * @return Supplier of line
   */
  protected @Nullable AbstractSidebar.Line getLine(@Range(from = 0, to = Sidebar.MAX_LINES - 1) int line) {
    SidebarUtilities.checkLineBounds(sidebar.maxLines(), line);
    checkClosed();
    return lines[line];
  }

  /**
   * Closes this AbstractSidebar
   */
  public final void close() {
    close(true);
  }

  /**
   * Closes this AbstractSidebar
   *
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

  /**
   * @return Whether this AbstractSidebar is closed
   */
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

  public sealed class Line permits DynamicLine {
    protected final int line;

    private Line(int line) {
      this.line = line;
    }
  }

  public final class DynamicLine extends Line {
    private final Supplier<Component> function;

    private DynamicLine(int line, @NotNull Supplier<Component> function) {
      super(line);
      this.function = function;
    }

    public void update() {
      sidebar.line(line, function.get());
    }
  }
}
