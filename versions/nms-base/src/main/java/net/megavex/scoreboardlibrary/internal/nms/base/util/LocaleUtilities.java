package net.megavex.scoreboardlibrary.internal.nms.base.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;
import net.kyori.adventure.translation.Translator;
import net.megavex.scoreboardlibrary.internal.ScoreboardLibraryLogger;
import org.bukkit.entity.Player;

@SuppressWarnings("JavaLangInvokeHandleSignature")
public final class LocaleUtilities {
  private static MethodHandle adventureMethod, legacySpigotMethod, legacyMethod;

  static {
    var lookup = MethodHandles.publicLookup();
    try {
      adventureMethod = lookup.findVirtual(Player.class, "locale", MethodType.methodType(Locale.class));
    } catch (IllegalAccessException | NoSuchMethodException e) {
      var methodType = MethodType.methodType(String.class);
      try {
        legacySpigotMethod = lookup.findVirtual(Player.Spigot.class, "getLocale", methodType);
      } catch (IllegalAccessException | NoSuchMethodException ex) {
        try {
          legacyMethod = lookup.findVirtual(Player.class, "getLocale", methodType);
        } catch (IllegalAccessException | NoSuchMethodException exc) {
          ScoreboardLibraryLogger.logMessage("No way of getting a player's locale was found");
        }
      }
    }
  }

  private LocaleUtilities() {
  }

  public static Locale getPlayerLocale(Player player) {
    try {
      if (adventureMethod != null) {
        return (Locale) adventureMethod.invokeExact(player);
      } else if (legacySpigotMethod != null) {
        return Translator.parseLocale((String) legacySpigotMethod.invokeExact(player.spigot()));
      } else if (legacyMethod != null) {
        return Translator.parseLocale((String) legacyMethod.invokeExact(player));
      } else {
        return Locale.US;
      }
    } catch (Throwable e) {
      e.printStackTrace();
      return Locale.US;
    }
  }
}
