package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.OtherAccessors.craftPlayerGetHandleMethod;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.OtherAccessors.packetClass;

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
    Object craftPlayerHandle = RandomUtils.invokeMethod(player, craftPlayerGetHandleMethod, null);

    Object playerConnection = RandomUtils.getInstanceField(craftPlayerHandle.getClass(), craftPlayerHandle, "playerConnection");

    // Note: getMethod not cached rn because I need a playerConnection object first.
    RandomUtils.invokeMethod(playerConnection, RandomUtils.getMethod(playerConnection.getClass(), "sendPacket",new Class<?>[]{packetClass}), new Object[]{packet});
  }
}
