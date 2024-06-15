package net.megavex.scoreboardlibrary.implementation;

import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public final class PacketAdapterLoader {
  private static final String MODERN = "modern",
    V1_8_R3 = "v1_8_R3",
    PACKET_EVENTS = "packetevents",
    PROTOCOL_LIB = "protocollib";

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

    Class<?> plibClass = tryLoadProtocolLib();
    if (plibClass != null) {
      return plibClass;
    }

    return tryLoadPacketEvents();
  }

  private static @Nullable Class<?> tryLoadVersion(@NotNull String serverVersion) {
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/
    switch (serverVersion) {
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

  private static @Nullable Class<?> tryLoadProtocolLib() {
    Plugin loaderPlugin = JavaPlugin.getProvidingPlugin(PacketAdapterLoader.class);

    Plugin plibPlugin = loaderPlugin.getServer().getPluginManager().getPlugin("ProtocolLib");
    if (plibPlugin == null) {
      return null;
    }

    PluginDescriptionFile d = loaderPlugin.getDescription();
    if (!d.getDepend().contains(plibPlugin.getName()) && !d.getSoftDepend().contains(plibPlugin.getName())) {
      return null;
    }

    try {
      // ensure we are on a supported ProtocolLib version
      Class.forName("com.comphenix.protocol.wrappers.WrappedTeamParameters");
      return tryLoadImplementationClass(PROTOCOL_LIB);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private static @Nullable Class<?> tryLoadPacketEvents() {
    try {
      Class.forName("com.github.retrooper.packetevents.PacketEvents");
      return tryLoadImplementationClass(PACKET_EVENTS);
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
