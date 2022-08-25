package net.megavex.scoreboardlibrary.internal.nms.base;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LocaleUtilities;
import org.bukkit.entity.Player;

public abstract class ScoreboardManagerNMS<P> {
  public static ScoreboardManagerNMS<?> INSTANCE;
  public final String objectiveName;

  public ScoreboardManagerNMS() {
    Preconditions.checkState(INSTANCE == null);
    INSTANCE = this;

    var random = UUID.randomUUID().toString().substring(0, 5);
    this.objectiveName = "_s" + random;
  }

  public static <P> void sendLocalePackets(
    Locale specificLocale,
    ScoreboardManagerNMS<P> nms,
    Collection<Player> players,
    Function<Locale, P> packetFunction
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
  public abstract SidebarNMS<P, ?> createSidebarNMS(Sidebar sidebar);

  public abstract void displaySidebar(Iterable<Player> players);

  public abstract void removeSidebar(Iterable<Player> players);

  // Team
  public abstract TeamNMS<?, ?> createTeamNMS(String teamName);

  public abstract boolean isLegacy(Player player);

  // Packet
  public abstract void sendPacket(Player players, P packet);

  public final void sendPacket(Iterable<Player> players, P packet) {
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }
}
