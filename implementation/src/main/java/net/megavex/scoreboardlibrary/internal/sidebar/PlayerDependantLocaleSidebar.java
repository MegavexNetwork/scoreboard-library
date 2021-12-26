package net.megavex.scoreboardlibrary.internal.sidebar;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LocaleUtilities;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerDependantLocaleSidebar extends AbstractSidebar {

    private Map<Locale, SidebarLineHandler> localeMap;

    public PlayerDependantLocaleSidebar(ScoreboardManager scoreboardManager, int size) {
        super(scoreboardManager, size);
    }

    @Override
    public @Nullable Locale locale() {
        return null;
    }

    @Override
    protected void forEachSidebar(Consumer<SidebarLineHandler> consumer) {
        if (localeMap != null) {
            for (SidebarLineHandler value : localeMap.values()) {
                consumer.accept(value);
            }
        }
    }

    @Override
    protected SidebarLineHandler lineHandler(Player player) {
        SidebarLineHandler sidebar = playerMap == null ? null : playerMap.get(player);
        if (sidebar != null) {
            return sidebar;
        }

        Locale locale = LocaleUtilities.getLocaleOfPlayer(player);
        if (localeMap == null) localeMap = CollectionProvider.map(4);
        sidebar = localeMap.get(locale);
        if (sidebar != null) {
            return sidebar;
        }

        sidebar = new SidebarLineHandler(this, locale);

        localeMap.put(locale, sidebar);
        playerMap().put(player, sidebar);

        return sidebar;
    }

    @Override
    public boolean removePlayer(Player player) {
        boolean result = super.removePlayer(player);

        if (playerMap == null) return result;

        SidebarLineHandler sidebar = playerMap.remove(player);
        if (sidebar != null && !sidebar.hasPlayers() && localeMap != null) {
            localeMap.remove(sidebar.locale());
        }

        return result;
    }
}
