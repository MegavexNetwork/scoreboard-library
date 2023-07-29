package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import java.util.Collection;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LegacyTeamDisplayPacketAdapter extends TeamsPacketAdapter.TeamDisplayPacketAdapter<String> {
  private final TeamsPacketAdapter<PacketWrapper<?>, PacketAdapterImpl> packetAdapter;

  public LegacyTeamDisplayPacketAdapter(TeamsPacketAdapter<PacketWrapper<?>, PacketAdapterImpl> packetAdapter, ImmutableTeamProperties<String> properties) {
    super(properties);
    this.packetAdapter = packetAdapter;
  }

  @Override
  public void addEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    packetAdapter.packetAdapter().sendPacket(
      players,
      new WrapperPlayServerTeams(
        packetAdapter.teamName(),
        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entries
      )
    );
  }

  @Override
  public void removeEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    packetAdapter.packetAdapter().sendPacket(
      players,
      new WrapperPlayServerTeams(
        packetAdapter.teamName(),
        WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entries
      )
    );
  }

  @Override
  public void createTeam(@NotNull Collection<Player> players) {
    sendTeamPacket(players, false);
  }

  @Override
  public void updateTeam(@NotNull Collection<Player> players) {
    sendTeamPacket(players, true);
  }

  private void sendTeamPacket(Collection<Player> players, boolean update) {
    LocalePacketUtil.sendLocalePackets(packetAdapter.packetAdapter().localeProvider, null,
      packetAdapter.packetAdapter(),
      players,
      locale -> new WrapperPlayServerTeamsLegacy(
        packetAdapter.teamName(),
        properties,
        update ? WrapperPlayServerTeams.TeamMode.UPDATE : WrapperPlayServerTeams.TeamMode.CREATE
      ));
  }
}

