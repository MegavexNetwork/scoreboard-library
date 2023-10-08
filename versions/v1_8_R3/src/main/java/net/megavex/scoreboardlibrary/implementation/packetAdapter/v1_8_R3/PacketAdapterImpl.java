package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<Packet<PacketListenerPlayOut>> {
  public PacketAdapterImpl() {
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
    return true;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<PacketListenerPlayOut> packet) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
  }
}
