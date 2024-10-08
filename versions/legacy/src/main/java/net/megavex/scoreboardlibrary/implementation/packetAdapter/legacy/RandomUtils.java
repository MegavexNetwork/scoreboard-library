package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}
