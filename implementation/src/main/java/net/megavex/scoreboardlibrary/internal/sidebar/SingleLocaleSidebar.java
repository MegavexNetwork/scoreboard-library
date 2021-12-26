package net.megavex.scoreboardlibrary.internal.sidebar;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public class SingleLocaleSidebar extends AbstractSidebar {

    private final Locale locale;
    private SidebarLineHandler sidebar;

    public SingleLocaleSidebar(ScoreboardManager scoreboardManager, int size, Locale locale) {
        super(scoreboardManager, size);
        this.locale = locale;
    }

    @Override
    public @Nullable Locale locale() {
        return locale;
    }

    @Override
    protected void forEachSidebar(Consumer<SidebarLineHandler> consumer) {
        consumer.accept(getSidebar());
    }

    @Override
    protected SidebarLineHandler lineHandler(Player player) {
        return getSidebar();
    }

    private SidebarLineHandler getSidebar() {
        if (sidebar == null) sidebar = new SidebarLineHandler(this, locale());
        return sidebar;
    }
}
