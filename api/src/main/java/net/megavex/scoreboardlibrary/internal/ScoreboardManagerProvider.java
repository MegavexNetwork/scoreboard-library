package net.megavex.scoreboardlibrary.internal;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Bridges the implementation and the API
 */
@ApiStatus.Internal
public abstract class ScoreboardManagerProvider {

    private static ScoreboardManagerProvider instance;
    private static JavaPlugin loaderPlugin;

    public static ScoreboardManagerProvider instance() {
        return instance;
    }

    public static void instance(ScoreboardManagerProvider instance) {
        ScoreboardManagerProvider.instance = instance;
    }

    public static JavaPlugin loaderPlugin() {
        return loaderPlugin;
    }

    public static void loaderPlugin(JavaPlugin loaderPlugin) {
        ScoreboardManagerProvider.loaderPlugin = loaderPlugin;
    }

    @NotNull
    public TeamInfo teamInfo() {
        return teamInfo(null);
    }

    @NotNull
    public abstract TeamInfo teamInfo(@Nullable Collection<String> entries);


    @NotNull
    public abstract ScoreboardManager scoreboardManager(JavaPlugin plugin);

    /**
     * Gets the {@link Sidebar} that a player has
     *
     * @param player Player
     * @return Sidebar
     */
    @Nullable
    public abstract Sidebar sidebar(Player player);

    /**
     * Gets the {@link TeamManager} that a player has
     *
     * @param player Player
     * @return Sidebar
     */
    @Nullable
    public abstract TeamManager teamManager(Player player);
}
