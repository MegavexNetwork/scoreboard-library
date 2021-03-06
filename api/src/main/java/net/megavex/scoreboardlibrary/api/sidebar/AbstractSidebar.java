package net.megavex.scoreboardlibrary.api.sidebar;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.line.SidebarLine;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static net.kyori.adventure.text.Component.empty;
import static net.megavex.scoreboardlibrary.api.sidebar.line.SidebarLine.staticLine;

public class AbstractSidebar implements HasScoreboardManager, Closeable {
  private static final SidebarLine emptyLine = staticLine(empty());
  protected final Sidebar sidebar;
  private SidebarLine[] lines;
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
   * Updates a line
   *
   * @param lineSupplier Line to update
   */
  protected final void updateLine(@NotNull SidebarLine lineSupplier) {
    checkClosed();
    Preconditions.checkNotNull(lineSupplier);
    Preconditions.checkArgument(!lineSupplier.lineStatic());
    sidebar.line(lineSupplier.line(), lineSupplier.computeValue());
  }

  /**
   * Unregisters a line
   *
   * @param line Line to unregister
   * @return Unregistered line
   */
  protected final @Nullable SidebarLine unregisterLine(int line) {
    var lineSupplier = lines[line];
    if (lineSupplier == null) {
      return null;
    }

    lines[line] = null;
    sidebar.line(line, null);
    return lineSupplier;
  }

  /**
   * Registers a static line for this AbstractSidebar
   *
   * @param line  Line to register
   * @param value Static value
   */
  protected final @NotNull SidebarLine registerStaticLine(int line, @NotNull Component value) {
    var sidebarLine = staticLine(value);
    registerLine(line, sidebarLine);
    return sidebarLine;
  }

  /**
   * Registers a line for this AbstractSidebar
   *
   * @param line     Line to register
   * @param supplier Supplier
   * @return The created LineSupplier
   */
  protected final @NotNull SidebarLine registerLine(int line, @NotNull Supplier<Component> supplier) {
    checkClosed();
    Preconditions.checkNotNull(supplier);

    return registerLine(line, new SidebarLine() {
      @Override
      public boolean lineStatic() {
        return false;
      }

      @Override
      public int line() {
        return line;
      }

      @Override
      public Component computeValue() {
        return supplier.get();
      }
    });
  }

  /**
   * Registers a line for this AbstractSidebar
   *
   * @param line     Line to register
   * @param supplier Supplier
   * @return The same LineSupplier for convenience
   */
  protected final <S extends SidebarLine> @NotNull S registerLine(int line, @NotNull S supplier) {
    checkClosed();
    Objects.requireNonNull(supplier);

    SidebarUtilities.checkLineBounds(line);
    if (lines[line] != null) {
      throw new IllegalStateException("Line " + line + " is already registered");
    }

    lines[line] = Objects.requireNonNull(supplier, "LineSupplier cannot be null");
    sidebar.line(line, supplier.computeValue());
    return supplier;
  }

  /**
   * Registers an empty line for this AbstractSidebar
   *
   * @param line Line to register
   */
  protected final void registerEmptyLine(int line) {
    registerLine(line, emptyLine);
  }

  /**
   * Gets the supplier for a line
   *
   * @param line Line
   * @return Supplier of line
   */
  protected @Nullable SidebarLine getLine(int line) {
    checkClosed();
    return lines[line];
  }

  @Override
  public final @NotNull ScoreboardManager scoreboardManager() {
    return sidebar.scoreboardManager();
  }

  protected final void checkClosed() {
    Preconditions.checkState(!closed, "AbstractSidebar is closed");
  }

  /**
   * Closes this AbstractSidebar
   */
  @Override
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
      for (int i = 0; i < sidebar.maxLines(); i++) {
        sidebar.line(i, null);
      }
    }

    lines = null;
    onClosed();
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public int hashCode() {
    return sidebar.hashCode();
  }
}
