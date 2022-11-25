package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team.TeamsPacketAdapterImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<PacketWrapper<?>> {
  private final WrapperPlayServerDisplayScoreboard displayPacket;
  private final WrapperPlayServerScoreboardObjective removePacket;

  public PacketAdapterImpl() {
    Preconditions.checkState(PacketEvents.getAPI() != null, "PacketEvents isn't loaded");

    this.displayPacket = new WrapperPlayServerDisplayScoreboard(1, this.objectiveName);
    this.removePacket = new WrapperPlayServerScoreboardObjective(
      this.objectiveName,
      WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE,
      null,
      null
    );
  }

  @Override
  public @NotNull SidebarPacketAdapter<PacketWrapper<?>, ?> createSidebarPacketAdapter(@NotNull Sidebar sidebar) {
    return new SidebarPacketAdapterImpl(this, sidebar);
  }

  @Override
  public void displaySidebar(@NotNull Iterable<Player> players) {
    sendPacket(players, displayPacket);
  }

  @Override
  public void removeSidebar(@NotNull Iterable<Player> players) {
    sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return PacketEvents.getAPI()
      .getPlayerManager()
      .getClientVersion(player)
      .isOlderThanOrEquals(ClientVersion.V_1_12_2);
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull PacketWrapper<?> packet) {
    PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
  }
}
