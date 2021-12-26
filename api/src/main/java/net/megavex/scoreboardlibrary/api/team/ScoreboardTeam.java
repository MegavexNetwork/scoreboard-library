package net.megavex.scoreboardlibrary.api.team;

import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import org.bukkit.entity.Player;

/**
 * Represents a Scoreboard Team
 */
public interface ScoreboardTeam extends Closeable {

    /**
     * Gets the TeamManager of this Team
     *
     * @return TeamManager
     */
    TeamManager teamManager();

    TeamInfo globalInfo();

    /**
     * Gets the name of this team
     *
     * @return Name
     */
    String name();

    /**
     * Gets the {@link TeamInfo} of a Player
     *
     * @param player Player
     * @return {@link TeamInfo} of Player
     */
    TeamInfo teamInfo(Player player);

    /**
     * Changes a Player's visible {@link TeamInfo}
     *
     * @param player   Player
     * @param teamInfo New {@link TeamInfo} of Player
     * @return new team info of player.
     */
    TeamInfo teamInfo(Player player, TeamInfo teamInfo);
}
