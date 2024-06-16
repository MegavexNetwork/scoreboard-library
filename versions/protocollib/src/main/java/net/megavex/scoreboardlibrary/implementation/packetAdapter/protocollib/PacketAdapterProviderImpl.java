package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.utility.MinecraftVersion;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib.team.TeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider {
  private final ProtocolManager pm;
  private final boolean isLegacyVersion;

  public PacketAdapterProviderImpl() {
    this.pm = Objects.requireNonNull(ProtocolLibrary.getProtocolManager());
    this.isLegacyVersion = !MinecraftVersion.AQUATIC_UPDATE.atOrAbove();
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return new ObjectivePacketAdapterImpl(pm, objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(pm, teamName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    return isLegacyVersion ? LineRenderingStrategy.LEGACY : LineRenderingStrategy.MODERN;
  }
}