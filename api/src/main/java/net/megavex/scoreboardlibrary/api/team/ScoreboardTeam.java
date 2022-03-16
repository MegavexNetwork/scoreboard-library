package net.megavex.scoreboardlibrary.api.team;

import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Scoreboard Team
 */
public interface ScoreboardTeam extends Closeable {

    /**
     * Gets the TeamManager of this Team
     *
     * @return TeamManager
     */
    @NotNull TeamManager teamManager();

    @NotNull TeamInfo globalInfo();

    /**
     * Gets the name of this team
     *
     * @return Name
     */
    @NotNull String name();

    /**
     * Gets the {@link TeamInfo} of a Player
     *
     * @param player Player
     * @return {@link TeamInfo} of Player
     */
    @NotNull TeamInfo teamInfo(Player player);

    /**
     * Changes a Player's visible {@link TeamInfo}
     *
     * @param player   Player
     * @param teamInfo New {@link TeamInfo} of Player
     * @return new team info of player.
     */
    @NotNull TeamInfo teamInfo(Player player, TeamInfo teamInfo);
}
