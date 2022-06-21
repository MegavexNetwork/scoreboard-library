package net.megavex.scoreboardlibrary.api.sidebar.line;

public abstract class AbstractSidebarLine implements SidebarLine {
  protected final int line;

  public AbstractSidebarLine(int line) {
    this.line = line;
  }

  @Override
  public final boolean lineStatic() {
    return false;
  }

  @Override
  public final int line() {
    return line;
  }
}
