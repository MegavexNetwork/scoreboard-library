package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider, PacketSender<PacketContainer> {
  private final ProtocolManager pm;
  private final boolean isLegacyVersion;

  public PacketAdapterProviderImpl() {
    this.pm = Objects.requireNonNull(ProtocolLibrary.getProtocolManager());
    this.isLegacyVersion = !MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.AQUATIC_UPDATE);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return isLegacyVersion;
  }

  @Override
  public void sendPacket(Player player, PacketContainer packet) {
    pm.sendServerPacket(player, packet);
  }
}
