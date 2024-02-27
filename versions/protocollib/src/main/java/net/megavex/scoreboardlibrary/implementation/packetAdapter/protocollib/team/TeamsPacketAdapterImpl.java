package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamsPacketAdapterImpl implements TeamsPacketAdapter {
  private final ProtocolManager pm;
  private final String teamName;
  private PacketContainer removePacket;

  public TeamsPacketAdapterImpl(@NotNull ProtocolManager pm, @NotNull String teamName) {
    this.pm = pm;
    this.teamName = teamName;
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
      removePacket.getIntegers().write(1, TeamConstants.MODE_REMOVE);
      removePacket.getStrings().write(0, teamName);
    }

    for (Player player : players) {
      pm.sendServerPacket(player, removePacket);
    }
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new ModernDisplayAdapter(pm, teamName, properties);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyDisplayAdapter(pm, teamName, properties);
  }
}
