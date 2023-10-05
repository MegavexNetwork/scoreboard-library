package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class ScoreboardLibraryPacketAdapter<P> {
  protected static final int POSITION_SIDEBAR = 1;
  protected static final int OBJECTIVE_MODE_REMOVE = 1;

  private final String objectiveName;
  private final LocaleProvider localeProvider;

  public ScoreboardLibraryPacketAdapter() {
    String random = UUID.randomUUID().toString().substring(0, 5);
    this.objectiveName = "_s" + random;
    this.localeProvider = LocaleProvider.localeProvider();
  }

  public @NotNull String objectiveName() {
    return objectiveName;
  }

  public @NotNull LocaleProvider localeProvider() {
    return localeProvider;
  }

  // Sidebar
  public abstract @NotNull SidebarPacketAdapter<P, ?> createSidebarPacketAdapter(@NotNull Sidebar sidebar);

  public abstract void displaySidebar(@NotNull Iterable<Player> players);

  public abstract void removeSidebar(@NotNull Iterable<Player> players);

  // Objective
  public @NotNull ObjectivePacketAdapter<?, ?> createObjectiveAdapter(@NotNull String objectiveName) {
    throw new UnsupportedOperationException();
  }

  // Team
  public abstract @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName);

  public abstract boolean isLegacy(@NotNull Player player);

  // Packet
  public abstract void sendPacket(@NotNull Player player, @NotNull P packet);

  public final void sendPacket(@NotNull Iterable<Player> players, @NotNull P packet) {
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }
}
