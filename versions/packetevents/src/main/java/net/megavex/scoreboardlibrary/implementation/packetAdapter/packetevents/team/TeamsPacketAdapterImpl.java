package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamsPacketAdapterImpl implements TeamsPacketAdapter {
  private final PacketSender<PacketWrapper<?>> sender;
  private final String teamName;
  private WrapperPlayServerTeams removePacket;

  public TeamsPacketAdapterImpl(PacketSender<PacketWrapper<?>> sender, String teamName) {
    this.sender = sender;
    this.teamName = teamName;
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = new WrapperPlayServerTeams(
        teamName,
        WrapperPlayServerTeams.TeamMode.REMOVE,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
      );
    }

    sender.sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new AdventureTeamDisplayPacketAdapter(sender, teamName, properties);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyTeamDisplayPacketAdapter(sender, teamName, properties);
  }
}
