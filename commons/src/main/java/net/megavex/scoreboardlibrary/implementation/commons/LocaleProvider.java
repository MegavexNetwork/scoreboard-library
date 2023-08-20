package net.megavex.scoreboardlibrary.implementation.commons;

import net.kyori.adventure.translation.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;

public interface LocaleProvider {
  Locale DEFAULT_LOCALE = Locale.US;

  static @NotNull LocaleProvider localeProvider() {
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    try {
      MethodHandle adventureMethod = lookup.findVirtual(Player.class, "locale", MethodType.methodType(Locale.class));
      return player -> {
        try {
          return (Locale) adventureMethod.invokeExact(player);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      };
    } catch (IllegalAccessException | NoSuchMethodException ignored) {
    }

    MethodType methodType = MethodType.methodType(String.class);
    try {
      MethodHandle legacySpigotMethod = lookup.findVirtual(Player.Spigot.class, "getLocale", methodType);
      return player -> {
        try {
          Locale locale = Translator.parseLocale((String) legacySpigotMethod.invokeExact(player.spigot()));
          return locale == null ? DEFAULT_LOCALE : locale;
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      };
    } catch (IllegalAccessException | NoSuchMethodException ignored) {
    }

    try {
      MethodHandle legacyMethod = lookup.findVirtual(Player.class, "getLocale", methodType);
      return player -> {
        try {
          @org.jetbrains.annotations.Nullable Locale locale = Translator.parseLocale((String) legacyMethod.invokeExact(player));
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
