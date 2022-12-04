package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import java.util.UUID;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider.localeProvider;

public abstract class ScoreboardLibraryPacketAdapter<P> {
  protected static final int POSITION_SIDEBAR = 1;
  protected static final int OBJECTIVE_MODE_REMOVE = 1;

  public final String objectiveName;
  public final LocaleProvider localeProvider;

  public ScoreboardLibraryPacketAdapter() {
    var random = UUID.randomUUID().toString().substring(0, 5);
    this.objectiveName = "_s" + random;
    this.localeProvider = localeProvider();
  }

  // Sidebar
  public abstract @NotNull SidebarPacketAdapter<P, ?> createSidebarPacketAdapter(@NotNull Sidebar sidebar);

  public abstract void displaySidebar(@NotNull Iterable<Player> players);

  public abstract void removeSidebar(@NotNull Iterable<Player> players);

  // Team
  public abstract @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName);

  public abstract boolean isLegacy(@NotNull Player player);

  // Packet
  public abstract void sendPacket(@NotNull Player player, @NotNull P packet);

  public final void sendPacket(@NotNull Iterable<Player> players, @NotNull P packet) {
    for (var player : players) {
      sendPacket(player, packet);
    }
  }
}
