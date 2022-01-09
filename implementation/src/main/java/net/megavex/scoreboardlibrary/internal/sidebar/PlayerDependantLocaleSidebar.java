package net.megavex.scoreboardlibrary.internal.sidebar;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LocaleUtilities;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerDependantLocaleSidebar extends AbstractSidebar {

    private volatile Map<Player, SidebarLineHandler> playerMap;
    private volatile Map<Locale, SidebarLineHandler> localeMap;

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
    public Collection<Player> players() {
        return playerMap == null ? Collections.emptySet() : playerMap.keySet();
    }

    @Override
    protected SidebarLineHandler addPlayer0(Player player) {
        SidebarLineHandler sidebar = playerMap == null ? null : playerMap.get(player);
        if (sidebar != null) {
            return sidebar;
        }

        Locale locale = LocaleUtilities.getLocaleOfPlayer(player);

        if (localeMap == null)
            synchronized (lock) {
                if (localeMap == null) localeMap = new ConcurrentHashMap<>(4, 0.75f, 2);
            }

        sidebar = localeMap.get(locale);
        if (sidebar != null) {
            return sidebar;
        }

        sidebar = new SidebarLineHandler(this, locale);

        localeMap.put(locale, sidebar);

        if (playerMap == null)
            synchronized (lock) {
                if (playerMap == null) playerMap = new ConcurrentHashMap<>(4, 0.75f, 2);
            }

        playerMap.put(player, sidebar);

        return sidebar;
    }

    @Override
    protected SidebarLineHandler removePlayer0(Player player) {
        if (playerMap == null) return null;

        SidebarLineHandler lineHandler = playerMap.remove(player);
        if (lineHandler == null) return null;

        if (!lineHandler.hasPlayers() && localeMap != null) {
            localeMap.remove(lineHandler.locale());
        }

        return lineHandler;
    }

    @Override
    public void close() {
        super.close();

        if (closed) {
            playerMap = null;
            localeMap = null;
        }
    }
}
