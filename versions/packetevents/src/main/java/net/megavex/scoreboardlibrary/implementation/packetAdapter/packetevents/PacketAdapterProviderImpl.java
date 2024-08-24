package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team.TeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider {
  private final PacketEventsAPI<?> packetEvents;
  private final PacketEventsSender packetSender;

  public PacketAdapterProviderImpl() {
    this.packetEvents = PacketEvents.getAPI();
    if (this.packetEvents == null) {
      throw new IllegalStateException("PacketEvents exists in classpath but isn't loaded");
    }
    this.packetSender = new PacketEventsSender(this.packetEvents);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(packetSender, teamName);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return new ObjectivePacketAdapterImpl(packetSender, packetEvents, objectiveName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    ServerVersion serverVer = packetEvents.getServerManager().getVersion();
    if (serverVer.isOlderThan(ServerVersion.V_1_13)) {
      return LineRenderingStrategy.LEGACY;
    }

    if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolSupport")) {
      return LineRenderingStrategy.MODERN;
    }

    ClientVersion clientVer = packetEvents.getPlayerManager().getClientVersion(player);
    return clientVer.isNewerThanOrEquals(ClientVersion.V_1_13) ? LineRenderingStrategy.MODERN : LineRenderingStrategy.LEGACY;
  }
}
