package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomUtils {
  private static final String NMS_VERSION_STRING = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

  public static @NotNull Class<Object> getClassOrThrow(String fullPath) {
    Class<Object> c = getOptionalClass(fullPath);
    if (c==null){
      throw new IllegalStateException("Required class " + fullPath + " not found");
    }
    return c;
  }

  public static @Nullable Class<Object> getOptionalClass(String fullPath) {
    try {
      //noinspection unchecked
      return (Class<Object>) Class.forName(fullPath);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  public static @NotNull String server(String path) {
    return "net.minecraft.server." + NMS_VERSION_STRING + "." + path;
  }

  public static @NotNull String craftBukkit(String path) {
    return "org.bukkit.craftbukkit." + NMS_VERSION_STRING + "." + path;
  }
}
