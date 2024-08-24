package net.megavex.scoreboardlibrary.implementation;

import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public final class PacketAdapterLoader {
  private static final String MODERN = "modern",
    V1_8_R3 = "v1_8_R3",
    PACKET_EVENTS = "packetevents",
    LEGACY_REFLECTIONS = "legacyreflections";

  private PacketAdapterLoader() {
  }

  public static @NotNull PacketAdapterProvider loadPacketAdapter() throws NoPacketAdapterAvailableException {
    Class<?> nmsClass = findAndLoadImplementationClass();
    if (nmsClass == null) {
      throw new NoPacketAdapterAvailableException();
    }

    try {
      return (PacketAdapterProvider) nmsClass.getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("couldn't initialize packet adapter", e);
    }
  }

  private static @Nullable Class<?> findAndLoadImplementationClass() {
    String version = Bukkit.getServer().getBukkitVersion();
    int dashIndex = version.indexOf('-');
    if (dashIndex != -1) {
      version = version.substring(0, dashIndex);
    }

    Class<?> nmsClass = tryLoadVersion(version);
    if (nmsClass != null) {
      return nmsClass;
    }

    return tryLoadPacketEvents();
  }

  private static @Nullable Class<?> tryLoadVersion(@NotNull String serverVersion) {
    switch (serverVersion) {
      case "1.7.10":
        return tryLoadImplementationClass(LEGACY_REFLECTIONS);
      case "1.8.8":
        return tryLoadImplementationClass(V1_8_R3);
      case "1.17":
      case "1.17.1":
      case "1.18":
      case "1.18.1":
      case "1.18.2":
      case "1.19":
      case "1.19.1":
      case "1.19.2":
      case "1.19.3":
      case "1.19.4":
      case "1.20":
      case "1.20.1":
      case "1.20.2":
      case "1.20.3":
      case "1.20.4":
      case "1.20.5":
      case "1.20.6":
      case "1.21":
      case "1.21.1":
        return tryLoadImplementationClass(MODERN);
      default:
        // Hide from relocation checkers
        String property = "net.mega".concat("vex.scoreboardlibrary.forceModern");
        if (System.getProperty(property, "").equalsIgnoreCase("true")) {
          return tryLoadImplementationClass(MODERN);
        }

        return null;
    }
  }

  private static @Nullable Class<?> tryLoadPacketEvents() {
    Class<?> nmsClass = tryLoadImplementationClass(PACKET_EVENTS);
    if (nmsClass == null) {
      return null;
    }

    try {
      Class.forName("com.github.retrooper.packetevents.PacketEvents");
      return nmsClass;
    } catch (ClassNotFoundException ignored) {
      return null;
    }
  }

  private static @Nullable Class<?> tryLoadImplementationClass(@NotNull String name) {
    try {
      String path = "net.megavex.scoreboardlibrary.implementation.packetAdapter." + name + ".PacketAdapterProviderImpl";
      return Class.forName(path);
    } catch (ClassNotFoundException ignored) {
      return null;
    }
  }
}
