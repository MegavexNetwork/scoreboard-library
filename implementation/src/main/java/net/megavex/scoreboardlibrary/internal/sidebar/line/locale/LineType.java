package net.megavex.scoreboardlibrary.internal.sidebar.line.locale;

import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public enum LineType {
    NEW(LocaleLineImpl::new),
    LEGACY(LegacyLocaleLine::new);

    private final BiFunction<GlobalLineInfo, SidebarLineHandler, LocaleLine<?>> constructor;

    LineType(BiFunction<GlobalLineInfo, SidebarLineHandler, LocaleLine<?>> constructor) {
        this.constructor = constructor;
    }

    public static LineType getType(Player player) {
        return ScoreboardManagerNMS.INSTANCE.isLegacy(player) ? LEGACY : NEW;
    }

    public LocaleLine<?> create(GlobalLineInfo line, SidebarLineHandler sidebarLineHandler) {
        return constructor.apply(line, sidebarLineHandler);
    }
}
