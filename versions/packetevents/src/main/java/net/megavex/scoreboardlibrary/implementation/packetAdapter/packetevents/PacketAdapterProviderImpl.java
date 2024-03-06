package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team.TeamsPacketAdapterImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider, PacketSender<PacketWrapper<?>> {
  private final PacketEventsAPI<?> packetEvents;

  public PacketAdapterProviderImpl() {
    this.packetEvents = PacketEvents.getAPI();
    if (this.packetEvents == null) {
      throw new IllegalStateException("PacketEvents exists in classpath but isn't loaded");
    }
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return new ObjectivePacketAdapterImpl(this, packetEvents, objectiveName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return packetEvents
      .getPlayerManager()
      .getClientVersion(player)
      .isOlderThanOrEquals(ClientVersion.V_1_12_2);
  }

  public void sendPacket(@NotNull Player player, @NotNull PacketWrapper<?> packet) {
    packetEvents.getPlayerManager().sendPacket(player, packet);
  }

  public void sendPacket(@NotNull Iterable<Player> players, @NotNull PacketWrapper<?> packet) {
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }
}
