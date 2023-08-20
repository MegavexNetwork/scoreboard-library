package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class TeamsPacketAdapter<P, T extends ScoreboardLibraryPacketAdapter<P>> {
  protected static final int LEGACY_CHARACTER_LIMIT = 16;

  protected static final int MODE_CREATE = 0,
    MODE_REMOVE = 1,
    MODE_UPDATE = 2,
    MODE_ADD_ENTRIES = 3,
    MODE_REMOVE_ENTRIES = 4;

  private final T packetAdapter;
  private final String teamName;

  public TeamsPacketAdapter(@NotNull T packetAdapter, @NotNull String teamName) {
    this.packetAdapter = packetAdapter;
    this.teamName = teamName;
  }

  public @NotNull T packetAdapter() {
    return packetAdapter;
  }

  public @NotNull String teamName() {
    return teamName;
  }

  public abstract void removeTeam(@NotNull Iterable<Player> players);

  public abstract @NotNull TeamsPacketAdapter.TeamDisplayPacketAdapter<Component> createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties);

  public @NotNull TeamsPacketAdapter.TeamDisplayPacketAdapter<String> createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    throw new UnsupportedOperationException();
  }

  public abstract static class TeamDisplayPacketAdapter<C> {
    protected final ImmutableTeamProperties<C> properties;

    protected TeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<C> properties) {
      this.properties = properties;
    }

    public void updateTeamPackets(@NotNull Collection<String> entries) {
    }

    public abstract void addEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries);

    public abstract void removeEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries);

    public abstract void createTeam(@NotNull Collection<Player> players);

    public abstract void updateTeam(@NotNull Collection<Player> players);
  }
}
