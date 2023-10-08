package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class ObjectivePacketAdapter<P, T extends ScoreboardLibraryPacketAdapter<P>> {
  private final T packetAdapter;
  private final String objectiveName;

  public ObjectivePacketAdapter(@NotNull T packetAdapter, @NotNull String objectiveName) {
    this.packetAdapter = packetAdapter;
    this.objectiveName = objectiveName;
  }

  public @NotNull T packetAdapter() {
    return packetAdapter;
  }

  public @NotNull String objectiveName() {
    return objectiveName;
  }

  public abstract void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot);

  public abstract void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull ObjectivePacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    boolean renderRequired
  );

  public abstract void remove(@NotNull Collection<Player> players);

  public abstract void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value);

  public abstract void removeScore(@NotNull Collection<Player> players, @NotNull String entry);

  public enum ObjectivePacketType {
    CREATE,
    UPDATE
  }
}
