package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RandomUtils {
  private static final String NMS_VERSION_STRING = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  public static final VersionType MINECRAFT_VERSION = VersionType.fromNmsVersion(NMS_VERSION_STRING);

  public static final boolean is1_8Plus = MINECRAFT_VERSION.isHigherOrEqual(VersionType.v1_8);
  public static final boolean is1_7Minus = MINECRAFT_VERSION.isLower(VersionType.v1_8);

  public static Class<Object> getClass(String fullPath) {
    System.out.println("IS 1.8 PLUS: " + is1_8Plus);
    System.out.println("MC VERSION: " + MINECRAFT_VERSION);
    try {
      return (Class<Object>)Class.forName(fullPath);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  public static Class<Object> getServerClass(String path) {
    return getClass(String.format("net.minecraft.server.%s.%s", NMS_VERSION_STRING, path));
  }
  public static Class<Object> getCraftBukkitClass(String path) {
    return getClass(String.format("org.bukkit.craftbukkit.%s.%s", NMS_VERSION_STRING, path));
  }

  // TODO: method cache, either for all methods (cache elements & reuse when called) or for commonly used methods (dedicated fields)
  public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] argClasses) {
    // Note: can't just args to determine argClasses (eg if a method needs a Packet and you receive a type that inherits Packet)
    try {
      return clazz.getMethod(methodName, argClasses);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
  public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>[] argClasses) {
    try {
      return clazz.getDeclaredMethod(methodName, argClasses);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }


  public static Object invokeMethod(Object instance, Method method, Object[] args) {
    // Note: can't just args to determine argClasses (eg if a method needs a Packet and you receive a type that inherits Packet)
    try {
      return method.invoke(instance, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  public static Object invokeStaticMethod(Method method, Object[] args) {
    try {
      return method.invoke(null, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
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

  enum VersionType {
    v1_7, v1_8, v1_9, v1_10, v1_11, v1_12;

    public boolean isHigherOrEqual(VersionType other) {
      return this.ordinal() >= other.ordinal();
    }
    public boolean isLower(VersionType other) {
      return !isHigherOrEqual(other);
    }

    public static VersionType fromNmsVersion(String version) {
      return valueOf(version.split("_R")[0]);
    }
  }
}
