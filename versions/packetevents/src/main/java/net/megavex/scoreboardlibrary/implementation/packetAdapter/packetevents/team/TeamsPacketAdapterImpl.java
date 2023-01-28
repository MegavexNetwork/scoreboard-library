package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.PacketAdapterImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamsPacketAdapterImpl extends TeamsPacketAdapter<PacketWrapper<?>, PacketAdapterImpl> {
  private WrapperPlayServerTeams removePacket;

  public TeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = new WrapperPlayServerTeams(
        teamName(),
        WrapperPlayServerTeams.TeamMode.REMOVE,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
      );
    }

    packetAdapter().sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamInfoPacketAdapter<Component> createTeamInfoAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new AdventureTeamInfoPacketAdapter(this, properties);
  }

  @Override
  public @NotNull TeamInfoPacketAdapter<String> createLegacyTeamInfoAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyTeamInfoPacketAdapter(this, properties);
  }
}
