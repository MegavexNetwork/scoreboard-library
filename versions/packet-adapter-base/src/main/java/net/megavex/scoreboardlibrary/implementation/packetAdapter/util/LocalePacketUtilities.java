package net.megavex.scoreboardlibrary.implementation.packetAdapter.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LocalePacketUtilities {
  private LocalePacketUtilities() {
  }

  public static <P> void sendLocalePackets(
    @NotNull LocaleProvider localeProvider,
    @Nullable Locale specificLocale,
    @NotNull ScoreboardLibraryPacketAdapter<P> nms,
    @NotNull Collection<Player> players,
    @NotNull Function<Locale, P> packetFunction
  ) {
    if (players.isEmpty()) return;

    if (specificLocale != null) {
      P packet = packetFunction.apply(specificLocale);
      nms.sendPacket(players, packet);
    } else if (players.size() == 1) {
      Player player = players.iterator().next();
      P packet = packetFunction.apply(localeProvider.locale(player));
      nms.sendPacket(player, packet);
    } else {
      Map<Locale, P> map = CollectionProvider.map(1);
      for (Player player : players) {
        Locale locale = localeProvider.locale(player);
        P packet = map.computeIfAbsent(locale, i -> packetFunction.apply(locale));
        nms.sendPacket(player, packet);
      }
    }
  }
}
