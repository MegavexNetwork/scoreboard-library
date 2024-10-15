package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MinecraftClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.core.RegistryAccess;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class RegistryUtil {
  public static final RegistryAccess MINECRAFT_REGISTRY;

  static {
    Class<?> craftRegistry = ReflectUtil.getClassOrThrow(MinecraftClasses.craftBukkit("CraftRegistry"));
    try {
      Method method = craftRegistry.getDeclaredMethod("getMinecraftRegistry");
      MINECRAFT_REGISTRY = (RegistryAccess) method.invoke(null);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private RegistryUtil() {
  }
}
