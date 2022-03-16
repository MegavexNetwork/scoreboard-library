package net.megavex.scoreboardlibrary.internal.sidebar;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public class SingleLocaleSidebar extends AbstractSidebar {

    private final Locale locale;
    private volatile SidebarLineHandler sidebar;
    private volatile Set<Player> players;

    public SingleLocaleSidebar(ScoreboardManager scoreboardManager, ComponentTranslator componentTranslator, int size, Locale locale) {
        super(scoreboardManager, componentTranslator, size);
        this.locale = locale;
    }

    @Override
    public Collection<Player> players() {
        return players == null ? Collections.emptySet() : Collections.unmodifiableCollection(players);
    }

    @Override
    protected SidebarLineHandler addPlayer0(Player player) {
        if (!playerSet().add(player)) return null;

        return lineHandler();
    }

    @Override
    protected SidebarLineHandler removePlayer0(Player player) {
        if (players == null || !players.remove(player)) return null;

        return lineHandler();
    }

    @Override
    public @Nullable Locale locale() {
        return locale;
    }

    @Override
    protected void forEachSidebar(Consumer<SidebarLineHandler> consumer) {
        consumer.accept(lineHandler());
    }

    @Override
    public void close() {
        super.close();

        if (closed) {
            players = null;
        }
    }

    private Set<Player> playerSet() {
        if (players == null)
            synchronized (lock) {
                if (players == null) {
                    players = CollectionProvider.set(1);
                }
            }

        return players;
    }

    private SidebarLineHandler lineHandler() {
        if (sidebar == null)
            synchronized (lock) {
                if (sidebar == null) sidebar = new SidebarLineHandler(this, locale());
            }

        return sidebar;
    }
}
