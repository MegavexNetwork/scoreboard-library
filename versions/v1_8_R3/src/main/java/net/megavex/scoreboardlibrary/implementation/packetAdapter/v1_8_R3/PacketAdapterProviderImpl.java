package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider, PacketSender<Packet<?>> {
  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return new ObjectivePacketAdapterImpl(this, objectiveName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return true;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
  }
}
