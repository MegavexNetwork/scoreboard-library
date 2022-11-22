package net.megavex.scoreboardlibrary;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.exception.ScoreboardLibraryLoadException;
import net.megavex.scoreboardlibrary.internal.ScoreboardLibraryLogger;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

/**
 * Main ScoreboardManager library implementation class
 */
public final class ScoreboardLibraryImplementation {
  private ScoreboardLibraryImplementation() {
  }

  /**
   * Initializes the library
   */
  public static synchronized void init() throws ScoreboardLibraryLoadException {
    if (ScoreboardManagerProvider.instance() != null) {
      return; // Already initialised
    }

    var plugin = JavaPlugin.getProvidingPlugin(ScoreboardLibraryImplementation.class);
    ScoreboardManagerProvider.loaderPlugin(plugin);

    try {
      Class.forName("net.kyori.adventure.Adventure");
    } catch (ClassNotFoundException e) {
      throw new ScoreboardLibraryLoadException("Adventure is not in the classpath");
    }

    var implementation = loadImplementation();
    ScoreboardManagerNMS.INSTANCE = implementation;
    ScoreboardManagerProviderImpl.init();
    ScoreboardLibraryLogger.logMessage("Loaded implementation " + implementation.getClass().getName());
  }

  private static ScoreboardManagerNMS<?> loadImplementation() throws ScoreboardLibraryLoadException {
    var versionName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    Class<?> nmsClass = tryLoadImplementationClass(versionName);

    if (nmsClass == null) {
      // If PacketEvents exists in the classpath, try to use the PacketEvents implementation instead
      try {
        Class.forName("com.github.retrooper.packetevents.PacketEvents");
      } catch (ClassNotFoundException ignored) {
        throw new ScoreboardLibraryLoadException("No implementation found");
      }

      nmsClass = tryLoadImplementationClass("packetevents");
      if (nmsClass == null) {
        throw new ScoreboardLibraryLoadException("No implementation found");
      }
    }

    try {
      return (ScoreboardManagerNMS<?>) nmsClass.getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ScoreboardLibraryLoadException("Couldn't initialize NMS implementation", e);
    }
  }

  private static @Nullable Class<?> tryLoadImplementationClass(String name) {
    try {
      return Class.forName("net.megavex.scoreboardlibrary.internal.nms." + name + ".NMSImpl");
    } catch (ClassNotFoundException ignored) {
      return null;
    }
  }

  public static synchronized void close() {
    if (ScoreboardManagerProvider.instance() == null) {
      return;
    }

    var provider = ScoreboardManagerProviderImpl.instance();
    if (provider != null && !provider.scoreboardManagerMap.isEmpty()) {
      ImmutableList.copyOf(provider.scoreboardManagerMap.values()).forEach(ScoreboardManager::close);
    }

    ScoreboardManagerProvider.instance(null);
    ScoreboardManagerProviderImpl.instance(null);
    ScoreboardLibraryLogger.logMessage("Implementation closed");
  }
}
