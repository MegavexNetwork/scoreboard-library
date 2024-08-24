package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections;

import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider, PacketSender<Object> {
  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return new ObjectivePacketAdapterImpl(this, objectiveName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    return LineRenderingStrategy.LEGACY;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Object packet) {
    // Original line:
    //((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet)packet);
    Class<Object> packetClass = RandomUtils.getServerClass("Packet");
    Class<Object> craftPlayerClass = RandomUtils.getCraftBukkitClass("entity.CraftPlayer");

    Object craftPlayerHandle = RandomUtils.invokeMethod(craftPlayerClass, player, "getHandle", null, null);
    Object playerConnection = RandomUtils.getInstanceField(craftPlayerHandle.getClass(), craftPlayerHandle, "playerConnection");

    RandomUtils.invokeMethod(playerConnection.getClass(), playerConnection, "sendPacket", new Object[]{packet}, new Class<?>[]{packetClass});
  }
}
