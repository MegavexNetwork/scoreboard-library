package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// 1.13+
public class ModernDisplayAdapter implements TeamDisplayPacketAdapter {
  private final ProtocolManager pm;
  private final String teamName;
  private final ImmutableTeamProperties<Component> properties;
  private PacketContainer removePacket;

  public ModernDisplayAdapter(@NotNull ProtocolManager pm, @NotNull String teamName, @NotNull ImmutableTeamProperties<Component> properties) {
    this.pm = pm;
    this.teamName = teamName;
    this.properties = properties;
  }

  @Override
  public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
    packet.getIntegers().write(0, TeamConstants.mode(packetType));
    packet.getStrings().write(0, teamName);
    packet.getSpecificModifier(Collection.class).write(0, players);

    for (Player player : players) {
      pm.sendServerPacket(player, packet);
    }
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    // TODO
  }
}
