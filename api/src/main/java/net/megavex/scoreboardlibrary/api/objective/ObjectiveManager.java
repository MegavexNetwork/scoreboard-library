package net.megavex.scoreboardlibrary.api.objective;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectiveManager {
  // Objectives

  @NotNull Collection<ScoreboardObjective> objectives();

  @Nullable ScoreboardObjective objective(@NotNull String name);

  boolean objectiveExists(@NotNull String name);

  @NotNull ScoreboardObjective createIfAbsent(@NotNull String name);

  boolean removeObjective(@NotNull String name);

  void removeObjective(@NotNull ScoreboardObjective team);

  void display(ScoreboardObjective objective, ObjectiveDisplaySlot displaySlot);

  // Players

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

  // Close

  void close();

  boolean closed();
}
