package net.megavex.scoreboardlibrary.api.objective;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ObjectiveManager {
  @NotNull ScoreboardObjective create(@NotNull String name);

  void remove(@NotNull ScoreboardObjective objective);

  void display(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjective objective);

  @NotNull Collection<Player> players();

  boolean addPlayer(@NotNull Player player);

  default void addPlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      addPlayer(player);
    }
  }

  boolean removePlayer(@NotNull Player player);

  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }

  void close();

  boolean closed();
}
