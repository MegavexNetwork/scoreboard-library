package net.megavex.scoreboardlibrary.implementation.nms.packetevents.team;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.ColorUtil;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.nms.base.ImmutableTeamProperties;

public class WrapperPlayServerTeamsLegacy extends PacketWrapper<WrapperPlayServerTeamsLegacy> {
  private final String teamName;
  private final ImmutableTeamProperties<String> properties;
  private final WrapperPlayServerTeams.TeamMode teamMode;

  public WrapperPlayServerTeamsLegacy(String teamName, ImmutableTeamProperties<String> properties, WrapperPlayServerTeams.TeamMode teamMode) {
    super(PacketType.Play.Server.TEAMS);
    this.teamName = teamName;
    this.properties = properties;
    Preconditions.checkArgument(teamMode == WrapperPlayServerTeams.TeamMode.CREATE || teamMode == WrapperPlayServerTeams.TeamMode.UPDATE);
    this.teamMode = teamMode;
  }

  @Override
  public void write() {
    writeString(teamName, 16);
    writeByte(teamMode.ordinal());
    writeString(properties.displayName());
    writeString(properties.prefix());
    writeString(properties.suffix());
    writeByte(properties.packOptions());
    if (serverVersion == ServerVersion.V_1_7_10) {
      writeString(NameTagVisibility.ALWAYS.key());
      writeByte(15);
    } else {
      writeString(properties.nameTagVisibility().key());
      if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
        writeString(properties.collisionRule().key());
      }
      writeByte(ColorUtil.getId(properties.playerColor()));
    }

    if (teamMode == WrapperPlayServerTeams.TeamMode.CREATE) {
      if (serverVersion == ServerVersion.V_1_7_10) {
        writeShort(properties.entries().size());
      } else {
        writeVarInt(properties.entries().size());
      }

      for (String entry : properties.entries()) {
        writeString(entry);
      }
    }
  }
}
