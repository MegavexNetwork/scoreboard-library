package net.megavex.scoreboardlibrary.internal.nms.v1_19_R1.team;

import java.util.Collection;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.codec.StringCodec;
import protocolsupport.protocol.codec.VarNumberCodec;
import protocolsupport.protocol.packet.ClientBoundPacketData;
import protocolsupport.protocol.packet.ClientBoundPacketType;

public class LegacyTeamInfoNMS extends TeamNMS.TeamInfoNMS<String> {
  private final String teamName;

  public LegacyTeamInfoNMS(ImmutableTeamProperties<String> properties, String teamName) {
    super(properties);
    this.teamName = teamName;
  }

  @Override
  public void addEntries(Collection<Player> players, Collection<String> entries) {
    sendPacket(players, createEntriesPacket(true, entries));
  }

  @Override
  public void removeEntries(Collection<Player> players, Collection<String> entries) {
    sendPacket(players, createEntriesPacket(false, entries));
  }

  @Override
  public void createTeam(Collection<Player> players) {
    sendPacket(players, createInfoPacket(true));
  }

  @Override
  public void updateTeam(Collection<Player> players) {
    sendPacket(players, createInfoPacket(false));
  }

  private void sendPacket(Collection<Player> players, ClientBoundPacketData data) {
    for (var player : players) {
      var connection = ProtocolSupportAPI.getConnection(player);
      if (connection == null) continue;
      ((ConnectionImpl) connection).getPacketDataIO().writeClientbound(data);
    }
  }

  private ClientBoundPacketData createPacket(int mode) {
    var data = ClientBoundPacketData.create(ClientBoundPacketType.PLAY_SCOREBOARD_TEAM);
    StringCodec.writeVarIntUTF8String(data, teamName);
    data.writeByte(mode);
    return data;
  }

  private ClientBoundPacketData createInfoPacket(boolean create) {
    var data = createPacket(create ? TeamNMS.MODE_CREATE:TeamNMS.MODE_UPDATE);

    StringCodec.writeVarIntUTF8String(data, properties.displayName());
    StringCodec.writeVarIntUTF8String(data, properties.prefix());
    StringCodec.writeVarIntUTF8String(data, properties.suffix());
    data.writeByte(properties.packOptions());
    StringCodec.writeVarIntUTF8String(data, properties.nameTagVisibility().key());
    data.writeByte(0);

    if (create) {
      writeEntries(data, properties.entries());
    }

    return data;
  }

  private ClientBoundPacketData createEntriesPacket(boolean add, Collection<String> entries) {
    var data = createPacket(add ? TeamNMS.MODE_ADD_ENTRIES:TeamNMS.MODE_REMOVE_ENTRIES);
    writeEntries(data, entries);
    return data;
  }

  private void writeEntries(ClientBoundPacketData data, Collection<String> entries) {
    VarNumberCodec.writeVarInt(data, entries.size());
    for (String entry : entries) {
      StringCodec.writeVarIntUTF8String(data, entry);
    }
  }
}
