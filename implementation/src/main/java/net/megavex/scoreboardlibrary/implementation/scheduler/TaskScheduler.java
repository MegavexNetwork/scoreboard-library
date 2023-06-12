package net.megavex.scoreboardlibrary.implementation.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface TaskScheduler {
  static @NotNull TaskScheduler create(@NotNull Plugin plugin) {
    try {
      Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
      return new FoliaTaskScheduler(plugin);
    } catch (ClassNotFoundException ignored) {
      return new BukkitTaskScheduler(plugin);
    }
  }

  @NotNull RunningTask runEveryTick(@NotNull Runnable runnable);

  void runNextTick(@NotNull Runnable runnable);
}
