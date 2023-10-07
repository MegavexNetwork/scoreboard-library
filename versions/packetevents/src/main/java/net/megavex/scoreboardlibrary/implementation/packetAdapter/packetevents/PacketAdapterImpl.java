package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team.TeamsPacketAdapterImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<PacketWrapper<?>> {
  public PacketAdapterImpl() {
    if (PacketEvents.getAPI() == null) {
      throw new IllegalStateException("PacketEvents exists in classpath but isn't loaded");
    }
  }

  @Override
  public @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public @NotNull ObjectivePacketAdapter<?, ?> createObjectiveAdapter(@NotNull String objectiveName) {
    return new ObjectivePacketAdapterImpl(this, objectiveName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return clientVersion(player).isOlderThanOrEquals(ClientVersion.V_1_12_2);
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull PacketWrapper<?> packet) {
    PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
  }

  public ClientVersion clientVersion(@NotNull Player player) {
    return PacketEvents.getAPI()
      .getPlayerManager()
      .getClientVersion(player);
  }
}
