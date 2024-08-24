package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import java.lang.reflect.InvocationTargetException;

public class RandomUtils {
  public static final String MINECRAFT_VERSION = "v1_8_R3";

  public static Class<Object> getClass(String fullPath) {
    try {
      return (Class<Object>)Class.forName(fullPath);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  public static Class<Object> getServerClass(String path) {
    return getClass(String.format("net.minecraft.server.%s.%s", MINECRAFT_VERSION, path));
  }
  public static Class<Object> getCraftBukkitClass(String path) {
    return getClass(String.format("org.bukkit.craftbukkit.%s.%s", MINECRAFT_VERSION, path));
  }

  // TODO: method cache, either for all methods (cache elements & reuse when called) or for commonly used methods (dedicated fields)
  public static Object invokeMethod(Class<?> clazz, Object instance, String methodName, Object[] args, Class<?>[] argClasses) {
    // Note: can't just args to determine argClasses (eg if a method needs a Packet and you receive a type that inherits Packet)
    try {
      return clazz.getMethod(methodName, argClasses).invoke(instance, args);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
  public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object[] args, Class<?>[] argClasses) {
    try {
      return clazz.getDeclaredMethod(methodName, argClasses).invoke(null, args);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object getInstanceField(Class<?> clazz, Object instance, String fieldName) {
    try {
      return clazz.getField(fieldName).get(instance);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  public static Object getStaticField(Class<?> clazz, String fieldName) {
    try {
      return clazz.getDeclaredField(fieldName).get(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
}
