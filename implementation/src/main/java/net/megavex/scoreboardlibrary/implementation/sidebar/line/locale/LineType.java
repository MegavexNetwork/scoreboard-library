package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;

import java.util.function.BiFunction;

public enum LineType {
  MODERN(ModernLocaleLine::new),
  LEGACY(LegacyLocaleLine::new);

  private final BiFunction<GlobalLineInfo, SidebarLineHandler, LocaleLine<?>> constructor;

  LineType(BiFunction<GlobalLineInfo, SidebarLineHandler, LocaleLine<?>> constructor) {
    this.constructor = constructor;
  }

  public LocaleLine<?> create(GlobalLineInfo line, SidebarLineHandler localeLineHandler) {
    return constructor.apply(line, localeLineHandler);
  }
}
