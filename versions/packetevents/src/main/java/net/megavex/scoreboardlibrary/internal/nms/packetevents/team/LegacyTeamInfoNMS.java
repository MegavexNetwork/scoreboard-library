package net.megavex.scoreboardlibrary.internal.nms.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import java.util.Collection;
import java.util.Optional;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.packetevents.NMSImpl;
import org.bukkit.entity.Player;

public class LegacyTeamInfoNMS extends TeamNMS.TeamInfoNMS<String> {
  private final TeamNMS<PacketWrapper<?>, NMSImpl> teamNMS;

  public LegacyTeamInfoNMS(TeamNMS<PacketWrapper<?>, NMSImpl> teamNMS, ImmutableTeamProperties<String> properties) {
    super(properties);
    this.teamNMS = teamNMS;
  }

  @Override
  public void addEntries(Collection<Player> players, Collection<String> entries) {
    teamNMS.impl.sendPacket(
      players,
      new WrapperPlayServerTeams(
        teamNMS.teamName,
        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
        Optional.empty(),
        entries
      )
    );
  }

  @Override
  public void removeEntries(Collection<Player> players, Collection<String> entries) {
    teamNMS.impl.sendPacket(
      players,
      new WrapperPlayServerTeams(
        teamNMS.teamName,
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
    ScoreboardManagerNMS.sendLocalePackets(null,
      teamNMS.impl,
      players,
      locale -> new WrapperPlayServerTeamsLegacy(
        teamNMS.teamName,
        properties,
        update ? WrapperPlayServerTeams.TeamMode.UPDATE:WrapperPlayServerTeams.TeamMode.CREATE
      ));
  }
}

