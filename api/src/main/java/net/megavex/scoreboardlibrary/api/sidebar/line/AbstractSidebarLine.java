package net.megavex.scoreboardlibrary.api.sidebar.line;

public abstract class AbstractSidebarLine implements SidebarLine {

  protected final byte line;

  public AbstractSidebarLine(int line) {
    this((byte) line);
  }

  public AbstractSidebarLine(byte line) {
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
