package net.megavex.scoreboardlibrary.implementation.packetAdapter.base;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import org.bukkit.entity.Player;

public abstract class TeamsPacketAdapter<P, T extends ScoreboardLibraryPacketAdapter<P>> {
  public static final int LEGACY_CHARACTER_LIMIT = 16;

  public static final int MODE_CREATE = 0,
    MODE_REMOVE = 1,
    MODE_UPDATE = 2,
    MODE_ADD_ENTRIES = 3,
    MODE_REMOVE_ENTRIES = 4;

  public final T impl;
  public final String teamName;

  public TeamsPacketAdapter(T impl, String teamName) {
    this.impl = impl;
    this.teamName = teamName;
  }

  public abstract void removeTeam(Iterable<Player> players);

  public abstract TeamInfoPacketAdapter<Component> createTeamInfoAdapter(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator);

  public TeamInfoPacketAdapter<String> createLegacyTeamInfoAdapter(ImmutableTeamProperties<String> properties) {
    throw new UnsupportedOperationException();
  }

  public abstract static class TeamInfoPacketAdapter<C> {
    protected final ImmutableTeamProperties<C> properties;

    protected TeamInfoPacketAdapter(ImmutableTeamProperties<C> properties) {
      this.properties = properties;
    }

    public void updateTeamPackets(Collection<String> entries) {
    }

    public abstract void addEntries(Collection<Player> players, Collection<String> entries);

    public abstract void removeEntries(Collection<Player> players, Collection<String> entries);

    public abstract void createTeam(Collection<Player> players);

    public abstract void updateTeam(Collection<Player> players);
  }
}
