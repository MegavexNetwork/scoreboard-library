package net.megavex.scoreboardlibrary.implementation.nms.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import java.util.Collection;
import java.util.Optional;
import net.megavex.scoreboardlibrary.implementation.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.nms.base.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.util.LocalePacketUtilities;
import net.megavex.scoreboardlibrary.implementation.nms.packetevents.NMSImpl;
import org.bukkit.entity.Player;

public class LegacyTeamInfoNMS extends TeamsPacketAdapter.TeamInfoNMS<String> {
  private final TeamsPacketAdapter<PacketWrapper<?>, NMSImpl> packetAdapter;

  public LegacyTeamInfoNMS(TeamsPacketAdapter<PacketWrapper<?>, NMSImpl> packetAdapter, ImmutableTeamProperties<String> properties) {
    super(properties);
    this.packetAdapter = packetAdapter;
  }

  @Override
  public void addEntries(Collection<Player> players, Collection<String> entries) {
    packetAdapter.impl.sendPacket(
      players,
      new WrapperPlayServerTeams(
        packetAdapter.teamName,
        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
        Optional.empty(),
        entries
      )
    );
  }

  @Override
  public void removeEntries(Collection<Player> players, Collection<String> entries) {
    packetAdapter.impl.sendPacket(
      players,
      new WrapperPlayServerTeams(
        packetAdapter.teamName,
        WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES,
        Optional.empty(),
        entries
      )
    );
  }

  @Override
  public void createTeam(Collection<Player> players) {
    sendTeamPacket(players, false);
  }

  @Override
  public void updateTeam(Collection<Player> players) {
    sendTeamPacket(players, true);
  }

  private void sendTeamPacket(Collection<Player> players, boolean update) {
    LocalePacketUtilities.sendLocalePackets(packetAdapter.impl.localeProvider, null,
      packetAdapter.impl,
      players,
      locale -> new WrapperPlayServerTeamsLegacy(
        packetAdapter.teamName,
        properties,
        update ? WrapperPlayServerTeams.TeamMode.UPDATE:WrapperPlayServerTeams.TeamMode.CREATE
      ));
  }
}

