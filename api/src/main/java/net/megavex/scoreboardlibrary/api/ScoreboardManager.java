package net.megavex.scoreboardlibrary.api;

import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;

public interface ScoreboardManager extends Closeable, HasScoreboardManager {

    /**
     * Gets the ScoreboardManager for a plugin
     *
     * @param plugin Plugin
     * @return ScoreboardManager
     */
    static ScoreboardManager scoreboardManager(JavaPlugin plugin) {
        return ScoreboardManagerProvider.instance().scoreboardManager(plugin);
    }

    @Override
    default ScoreboardManager scoreboardManager() {
        return this;
    }

    /**
     * Gets the Plugin owner of this ScoreboardManager
     *
     * @return Plugin owner
     */
    Plugin plugin();

    /**
     * Creates a {@link Sidebar}
     *
     * @param maxLines Max sidebar lines
     * @param locale   Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
     *                 or null if the locale should depend on the player
     * @return Sidebar
     */
    Sidebar sidebar(int maxLines, @Nullable Locale locale);

    /**
     * Gets the sidebars associated with this ScoreboardManager
     *
     * @return Sidebars
     */
    Collection<Sidebar> sidebars();

    /**
     * Creates a {@link TeamManager}
     *
     * @return TeamManager
     */
    TeamManager teamManager();

    /**
     * Gets the team managers associated with this {@link JavaPlugin}
     *
     * @return Team Managers
     */
    Collection<TeamManager> teamManagers();
}
