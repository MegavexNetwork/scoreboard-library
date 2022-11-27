package net.megavex.scoreboardlibrary.implementation.commons;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;
import net.kyori.adventure.translation.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LocaleProvider {
  Locale DEFAULT_LOCALE = Locale.US;

  static @NotNull LocaleProvider localeProvider() {
    var lookup = MethodHandles.publicLookup();
    try {
      var adventureMethod = lookup.findVirtual(Player.class, "locale", MethodType.methodType(Locale.class));
      return player -> {
        try {
          return (Locale) adventureMethod.invokeExact(player);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      };
    } catch (IllegalAccessException | NoSuchMethodException ignored) {
    }

    var methodType = MethodType.methodType(String.class);
    try {
      var legacySpigotMethod = lookup.findVirtual(Player.Spigot.class, "getLocale", methodType);
      return player -> {
        try {
          var locale = Translator.parseLocale((String) legacySpigotMethod.invokeExact(player.spigot()));
          return locale == null ? DEFAULT_LOCALE : locale;
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      };
    } catch (IllegalAccessException | NoSuchMethodException ignored) {
    }

    try {
      var legacyMethod = lookup.findVirtual(Player.class, "getLocale", methodType);
      return player -> {
        try {
          var locale = Translator.parseLocale((String) legacyMethod.invokeExact(player));
          return locale == null ? DEFAULT_LOCALE : locale;
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      };
    } catch (IllegalAccessException | NoSuchMethodException ignored) {
      throw new RuntimeException("No way to get players locale found");
    }
  }

  @NotNull Locale locale(@NotNull Player player);
}
