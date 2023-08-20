package net.megavex.scoreboardlibrary.implementation;

import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.CraftBukkitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public final class PacketAdapterLoader {
  private PacketAdapterLoader() {
  }

  public static @NotNull ScoreboardLibraryPacketAdapter<?> loadPacketAdapter() throws NoPacketAdapterAvailableException {
    Class<?> nmsClass = findAndLoadImplementationClass();
    if (nmsClass == null) {
      throw new NoPacketAdapterAvailableException();
    }

    try {
      return (ScoreboardLibraryPacketAdapter<?>) nmsClass.getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("couldn't initialize packet adapter", e);
    }
  }

  private static @Nullable Class<?> findAndLoadImplementationClass() {
    String version = CraftBukkitUtil.version();
    Class<?> nmsClass = tryLoadImplementationClass(version);
    if (nmsClass != null) {
      return nmsClass;
    }

    nmsClass = tryLoadModern(version);
    if (nmsClass != null) {
      return nmsClass;
    }

    return tryLoadPacketEvents();
  }

  private static @Nullable Class<?> tryLoadModern(@NotNull String version) {
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/
    switch (version) {
      case "v1_17_R1":
      case "v1_18_R1":
      case "v1_18_R2":
      case "v1_19_R1":
      case "v1_19_R2":
      case "v1_19_R3":
      case "v1_20_R1":
        return tryLoadImplementationClass("modern");
      default:
        return null;
    }
  }

  private static @Nullable Class<?> tryLoadPacketEvents() {
    Class<?> nmsClass = tryLoadImplementationClass("packetevents");
    if (nmsClass == null) {
      return null;
    }

    try {
      Class.forName("com.github.retrooper.packetevents.PacketEvents");
    } catch (ClassNotFoundException ignored) {
      return null;
    }

    return nmsClass;
  }

  private static @Nullable Class<?> tryLoadImplementationClass(@NotNull String name) {
    try {
      return Class.forName("net.megavex.scoreboardlibrary.implementation.packetAdapter." + name + ".PacketAdapterImpl");
    } catch (ClassNotFoundException ignored) {
      return null;
    }
  }
}
