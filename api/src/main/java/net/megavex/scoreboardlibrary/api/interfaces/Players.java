package net.megavex.scoreboardlibrary.api.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Players {
  @NotNull Collection<Player> players();

  boolean addPlayer(@NotNull Player player);

  boolean removePlayer(@NotNull Player player);

  default void addPlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      addPlayer(player);
    }
  }

  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }
}
