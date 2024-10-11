package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class LegacyMinecraftClasses {
  private static final String NMS_VERSION_STRING = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

  private LegacyMinecraftClasses() {
  }

  // TODO: should be moved to MinecraftReflection at some point

  public static @NotNull String server(String path) {
    return "net.minecraft.server." + NMS_VERSION_STRING + "." + path;
  }
}
