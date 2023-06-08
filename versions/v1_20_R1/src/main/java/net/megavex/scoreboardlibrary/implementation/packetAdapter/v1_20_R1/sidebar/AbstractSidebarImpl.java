package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_20_R1.sidebar;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collection;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_20_R1.PacketAdapterImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities.getField;

public abstract class AbstractSidebarImpl extends SidebarPacketAdapter<Packet<?>, PacketAdapterImpl> {
  static final UnsafeUtilities.PacketConstructor<ClientboundSetObjectivePacket> objectivePacketConstructor =
    UnsafeUtilities.findPacketConstructor(ClientboundSetObjectivePacket.class, MethodHandles.lookup());
  static final Field objectiveNameField = UnsafeUtilities.getField(ClientboundSetObjectivePacket.class, "d"),
    objectiveDisplayNameField = UnsafeUtilities.getField(ClientboundSetObjectivePacket.class, "e"),
    objectiveRenderTypeField = UnsafeUtilities.getField(ClientboundSetObjectivePacket.class, "f");
  private static final Field objectiveModeField = getField(ClientboundSetObjectivePacket.class, "g");

  public AbstractSidebarImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);
  }

  protected void createObjectivePacket(ClientboundSetObjectivePacket packet, int mode) {
    UnsafeUtilities.setField(objectiveNameField, packet, packetAdapter().objectiveName);
    UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(objectiveModeField), mode);
    UnsafeUtilities.setField(objectiveRenderTypeField, packet, ObjectiveCriteria.RenderType.INTEGER);
  }

  @Override
  public void removeLine(@NotNull Collection<Player> players, @NotNull String line) {
    packetAdapter().sendPacket(players, new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, packetAdapter().objectiveName, line, 0));
  }

  @Override
  public void score(@NotNull Collection<Player> players, int score, @NotNull String line) {
    var packet = new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, packetAdapter().objectiveName, line, score);
    packetAdapter().sendPacket(players, packet);
  }
}
