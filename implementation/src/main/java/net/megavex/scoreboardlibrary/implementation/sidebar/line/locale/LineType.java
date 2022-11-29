package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import java.util.function.BiFunction;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;

public enum LineType {
  MODERN(LocaleLineImpl::new),
  LEGACY(LegacyLocaleLine::new);

  private final BiFunction<GlobalLineInfo, LocaleLineHandler, LocaleLine<?>> constructor;

  LineType(BiFunction<GlobalLineInfo, LocaleLineHandler, LocaleLine<?>> constructor) {
    this.constructor = constructor;
  }

  public static LineType getType(AbstractSidebar sidebar, Player player) {
    return sidebar.scoreboardLibrary().packetAdapter.isLegacy(player) ? LEGACY : MODERN;
  }

  public LocaleLine<?> create(GlobalLineInfo line, LocaleLineHandler localeLineHandler) {
    return constructor.apply(line, localeLineHandler);
  }
}
