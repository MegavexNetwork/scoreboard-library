package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class AbstractTeamDisplayPacketAdapter<T> implements TeamDisplayPacketAdapter {
  protected final PacketSender<PacketWrapper<?>> sender;
  protected final String teamName;
  protected final ImmutableTeamProperties<T> properties;

  public AbstractTeamDisplayPacketAdapter(@NotNull PacketSender<PacketWrapper<?>> sender, @NotNull String teamName, @NotNull ImmutableTeamProperties<T> properties) {
    this.sender = sender;
    this.teamName = teamName;
    this.properties = properties;
  }

  @Override
  public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    WrapperPlayServerTeams.TeamMode peMode;
    switch (packetType) {
      case ADD:
        peMode = WrapperPlayServerTeams.TeamMode.ADD_ENTITIES;
        break;
      case REMOVE:
        peMode = WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES;
        break;
      default:
        throw new IllegalStateException();
    }

    sender.sendPacket(
      players,
      new WrapperPlayServerTeams(
        teamName,
        peMode,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entries
      )
    );
  }

  protected @NotNull WrapperPlayServerTeams.TeamMode mode(@NotNull PropertiesPacketType packetType) {
    switch (packetType) {
      case CREATE:
        return WrapperPlayServerTeams.TeamMode.CREATE;
      case UPDATE:
        return WrapperPlayServerTeams.TeamMode.UPDATE;
      default:
        throw new IllegalStateException();
    }
  }
}
