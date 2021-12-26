package net.megavex.scoreboardlibrary.api.interfaces;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface Players {
    Collection<Player> players();

    boolean addPlayer(Player player);

    boolean removePlayer(Player player);

    default void addPlayers(Collection<Player> players) {
        for (Player player : players) {
            addPlayer(player);
        }
    }

    default void removePlayers(Collection<Player> players) {
        for (Player player : players) {
            removePlayer(player);
        }
    }
}
