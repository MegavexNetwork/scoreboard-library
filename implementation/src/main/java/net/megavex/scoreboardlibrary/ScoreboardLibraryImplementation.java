package net.megavex.scoreboardlibrary;

import com.google.common.collect.ImmutableList;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.exception.ScoreboardLibraryLoadException;
import net.megavex.scoreboardlibrary.internal.ScoreboardLibraryLogger;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

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

    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(ScoreboardLibraryImplementation.class);
    ScoreboardManagerProvider.loaderPlugin(plugin);

    try {
      Class.forName("net.kyori.adventure.Adventure");
    } catch (ClassNotFoundException e) {
      throw new ScoreboardLibraryLoadException("Adventure is not in the classpath");
    }

    String versionName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    Class<?> nmsClass;
    try {
      nmsClass = Class.forName("net.megavex.scoreboardlibrary.internal.nms." + versionName + ".NMSImpl");
    } catch (ClassNotFoundException ignored) {
      throw new ScoreboardLibraryLoadException("Server version " + versionName + " is not supported");
    }

    try {
      nmsClass.getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ScoreboardLibraryLoadException("Couldn't initialize NMS implementation", e);
    }

    ScoreboardManagerProviderImpl.init();
    ScoreboardLibraryLogger.logMessage("Loaded implementation " + nmsClass.getName());
  }

  public static synchronized void close() {
    if (ScoreboardManagerProvider.instance() == null) {
      return;
    }

    ScoreboardManagerProviderImpl provider = ScoreboardManagerProviderImpl.instance();
    if (provider != null && !provider.scoreboardManagerMap.isEmpty()) {
      ImmutableList.copyOf(provider.scoreboardManagerMap.values()).forEach(ScoreboardManager::close);
    }

    ScoreboardManagerProvider.instance(null);
    ScoreboardManagerProviderImpl.instance(null);
    ScoreboardLibraryLogger.logMessage("Closed");
  }
}
