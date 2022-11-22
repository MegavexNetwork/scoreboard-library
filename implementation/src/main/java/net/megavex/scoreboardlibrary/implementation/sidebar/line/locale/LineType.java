package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import java.util.function.BiFunction;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;

public enum LineType {
  NEW(LocaleLineImpl::new),
  LEGACY(LegacyLocaleLine::new);

  private final BiFunction<GlobalLineInfo, SidebarLineHandler, LocaleLine<?>> constructor;

  LineType(BiFunction<GlobalLineInfo, SidebarLineHandler, LocaleLine<?>> constructor) {
    this.constructor = constructor;
  }

  public static LineType getType(AbstractSidebar sidebar, Player player) {
    return sidebar.scoreboardLibrary().packetAdapter.isLegacy(player) ? LEGACY:NEW;
  }

  public LocaleLine<?> create(GlobalLineInfo line, SidebarLineHandler sidebarLineHandler) {
    return constructor.apply(line, sidebarLineHandler);
  }
}
