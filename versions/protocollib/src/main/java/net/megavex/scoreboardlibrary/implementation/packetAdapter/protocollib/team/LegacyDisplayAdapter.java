package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;

// 1.12.2 and below
public class LegacyDisplayAdapter implements TeamDisplayPacketAdapter {
  private final ProtocolManager pm;
  private final String teamName;
  private final ImmutableTeamProperties<String> properties;

  public LegacyDisplayAdapter(@NotNull ProtocolManager pm, @NotNull String teamName, @NotNull ImmutableTeamProperties<String> properties) {
    this.pm = pm;
    this.teamName = teamName;
    this.properties = properties;
  }

  @Override
  public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
    packet.getIntegers().write(1, TeamConstants.mode(packetType));
    packet.getStrings().write(0, teamName);
    packet.getSpecificModifier(Collection.class).write(0, entries);

    for (Player player : players) {
      pm.sendServerPacket(player, packet);
    }
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    String displayName = limit(properties.displayName());
    String prefix = limit(properties.prefix());
    String suffix = limit(properties.suffix());

    PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
    packet.getIntegers()
      .write(1, TeamConstants.mode(packetType))
      .write(2, properties.packOptions());

    packet.getStrings()
      .write(0, teamName)
      .write(1, displayName)
      .write(2, prefix)
      .write(3, suffix)
      .write(4, properties.nameTagVisibility().key());

    if (packetType == PropertiesPacketType.CREATE) {
      packet.getSpecificModifier(Collection.class).write(0, properties.entries());
    }

    for (Player player : players) {
      pm.sendServerPacket(player, packet);
    }
  }

  private @NotNull String limit(@NotNull String msg) {
    return limitLegacyText(msg, TeamConstants.LEGACY_CHAR_LIMIT);
  }
}
