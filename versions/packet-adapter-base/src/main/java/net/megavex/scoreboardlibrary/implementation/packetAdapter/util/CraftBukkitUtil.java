package net.megavex.scoreboardlibrary.implementation.packetAdapter.util;

import org.bukkit.Bukkit;

public final class CraftBukkitUtil {
  private static final String version;

  private CraftBukkitUtil() {
  }

  static {
    version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }

  public static String version() {
    return version;
  }
}
