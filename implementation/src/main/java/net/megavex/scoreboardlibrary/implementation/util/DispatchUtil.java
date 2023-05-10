package net.megavex.scoreboardlibrary.implementation.util;

import java.util.concurrent.TimeUnit;
import net.kyori.adventure.util.Ticks;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class DispatchUtil {
  private static boolean isFolia;

  private DispatchUtil() {
  }

  static {
    try {
      Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
      isFolia = true;
    } catch (ClassNotFoundException ignored) {
    }
  }

  public interface RunningTask {
    void cancel();
  }

  public static RunningTask runEveryTick(@NotNull Plugin plugin, @NotNull Runnable runnable) {
    var server = plugin.getServer();
    if (isFolia) {
      var task = server.getAsyncScheduler().runAtFixedRate(
        plugin,
        t -> runnable.run(),
        Ticks.SINGLE_TICK_DURATION_MS,
        Ticks.SINGLE_TICK_DURATION_MS,
        TimeUnit.MILLISECONDS
      );
      return task::cancel;
    } else {
      var task = server.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 1, 1);
      return task::cancel;
    }
  }

  public static void runNextTick(@NotNull Plugin plugin, @NotNull Runnable runnable) {
    var server = plugin.getServer();
    if (isFolia) {
      server.getAsyncScheduler().runDelayed(plugin, t -> runnable.run(), Ticks.SINGLE_TICK_DURATION_MS, TimeUnit.MILLISECONDS);
    } else {
      server.getScheduler().runTaskLaterAsynchronously(plugin, runnable, 1);
    }
  }
}
