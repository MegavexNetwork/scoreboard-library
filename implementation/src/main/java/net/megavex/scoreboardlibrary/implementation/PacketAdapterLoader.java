package net.megavex.scoreboardlibrary.implementation;

import java.lang.reflect.InvocationTargetException;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PacketAdapterLoader {
  private PacketAdapterLoader() {
  }

  public static @NotNull ScoreboardLibraryPacketAdapter<?> loadPacketAdapter() throws NoPacketAdapterAvailableException {
    String versionName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    Class<?> nmsClass = tryLoadImplementationClass(versionName);

    if (nmsClass == null) {
      nmsClass = tryLoadImplementationClass("packetevents");
      if (nmsClass == null) {
        throw new NoPacketAdapterAvailableException();
      }

      try {
        Class.forName("com.github.retrooper.packetevents.PacketEvents");
      } catch (ClassNotFoundException ignored) {
        throw new NoPacketAdapterAvailableException();
      }
    }

    try {
      return (ScoreboardLibraryPacketAdapter<?>) nmsClass.getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("couldn't initialize packet adapter", e);
    }
  }

  private static @Nullable Class<?> tryLoadImplementationClass(@NotNull String name) {
    try {
      return Class.forName("net.megavex.scoreboardlibrary.implementation.packetAdapter." + name + ".PacketAdapterImpl");
    } catch (ClassNotFoundException ignored) {
      return null;
    }
  }
}
