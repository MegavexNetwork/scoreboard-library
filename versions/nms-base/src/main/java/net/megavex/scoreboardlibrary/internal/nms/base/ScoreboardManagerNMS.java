package net.megavex.scoreboardlibrary.internal.nms.base;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LocaleUtilities;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ScoreboardManagerNMS<P> {
  public static ScoreboardManagerNMS<?> INSTANCE;
  public final String objectiveName;

  public ScoreboardManagerNMS() {
    var random = UUID.randomUUID().toString().substring(0, 5);
    this.objectiveName = "_s" + random;
  }

  public static <P> void sendLocalePackets(
    @Nullable Locale specificLocale,
    @NotNull ScoreboardManagerNMS<P> nms,
    @NotNull Collection<Player> players,
    @NotNull Function<Locale, P> packetFunction
  ) {
    if (players.isEmpty()) return;

    if (specificLocale != null) {
      var packet = packetFunction.apply(specificLocale);
      nms.sendPacket(players, packet);
    } else if (players.size() == 1) {
      var player = players.iterator().next();
      var packet = packetFunction.apply(LocaleUtilities.getPlayerLocale(player));
      nms.sendPacket(player, packet);
    } else {
      Map<Locale, P> map = CollectionProvider.map(1);
      for (var player : players) {
        var locale = LocaleUtilities.getPlayerLocale(player);
        var packet = map.computeIfAbsent(locale, i -> packetFunction.apply(locale));
        nms.sendPacket(player, packet);
      }
    }
  }

  // Sidebar
  public abstract @NotNull SidebarNMS<P, ?> createSidebarNMS(@NotNull Sidebar sidebar);

  public abstract void displaySidebar(@NotNull Iterable<Player> players);

  public abstract void removeSidebar(@NotNull Iterable<Player> players);

  // Team
  public abstract @NotNull TeamNMS<?, ?> createTeamNMS(@NotNull String teamName);

  public abstract boolean isLegacy(@NotNull Player player);

  // Packet
  public abstract void sendPacket(@NotNull Player player, @NotNull P packet);

  public final void sendPacket(@NotNull Iterable<Player> players, @NotNull P packet) {
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }
}
