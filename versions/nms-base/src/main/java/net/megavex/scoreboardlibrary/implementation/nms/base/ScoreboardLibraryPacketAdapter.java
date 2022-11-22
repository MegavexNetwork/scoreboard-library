package net.megavex.scoreboardlibrary.implementation.nms.base;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider.localeProvider;

public abstract class ScoreboardLibraryPacketAdapter<P> {
  public final String objectiveName;
  public final LocaleProvider localeProvider;

  public ScoreboardLibraryPacketAdapter() {
    var random = UUID.randomUUID().toString().substring(0, 5);
    this.objectiveName = "_s" + random;
    this.localeProvider = localeProvider();
  }

  // Sidebar
  public abstract @NotNull SidebarPacketAdapter<P, ?> createSidebarNMS(@NotNull Sidebar sidebar);

  public abstract void displaySidebar(@NotNull Iterable<Player> players);

  public abstract void removeSidebar(@NotNull Iterable<Player> players);

  // Team
  public abstract @NotNull TeamsPacketAdapter<?, ?> createTeamNMS(@NotNull String teamName);

  public abstract boolean isLegacy(@NotNull Player player);

  // Packet
  public abstract void sendPacket(@NotNull Player player, @NotNull P packet);

  public final void sendPacket(@NotNull Iterable<Player> players, @NotNull P packet) {
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }
}
