package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.sidebar;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class AbstractSidebarImpl extends SidebarPacketAdapter<Packet<?>, PacketAdapterImpl> {
  public AbstractSidebarImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);
  }

  protected void createObjectivePacket(ClientboundSetObjectivePacket packet, int mode) {
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, packetAdapter().objectiveName());
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, mode);
    PacketAccessors.OBJECTIVE_RENDER_TYPE_FIELD.set(packet, ObjectiveCriteria.RenderType.INTEGER);
  }

  @Override
  public void removeLine(@NotNull Collection<Player> players, @NotNull String line) {
    packetAdapter().sendPacket(players, new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, packetAdapter().objectiveName(), line, 0));
  }

  @Override
  public void score(@NotNull Collection<Player> players, int score, @NotNull String line) {
    ClientboundSetScorePacket packet = new ClientboundSetScorePacket(
      ServerScoreboard.Method.CHANGE,
      packetAdapter().objectiveName(),
      line,
      score
    );
    packetAdapter().sendPacket(players, packet);
  }
}
