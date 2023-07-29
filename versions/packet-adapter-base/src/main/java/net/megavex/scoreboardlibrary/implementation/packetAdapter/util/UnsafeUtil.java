package net.megavex.scoreboardlibrary.implementation.packetAdapter.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import sun.misc.Unsafe;

public class UnsafeUtil {
  public static final Unsafe UNSAFE;
  private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);

  static {
    try {
      Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafeField.setAccessible(true);
      UNSAFE = Objects.requireNonNull((Unsafe) theUnsafeField.get(null));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private UnsafeUtil() {
  }

  public static Field getField(Class<?> clazz, String name) {
    try {
      Field field = clazz.getDeclaredField(name);
      if (Modifier.isStatic(field.getModifiers())) {
        throw new IllegalArgumentException("field " + field.getName() + " is static");
      }
      return field;
    } catch (NoSuchFieldException e) {
      throw new Error(e);
    }
  }

  public static void setField(Field field, Object packet, Object value) {
    if (field == null || packet == null || value == null) {
      throw new NullPointerException();
    } else if (field.getDeclaringClass() != packet.getClass()) {
      throw new RuntimeException("field class does not match the packet class");
    } else if (!field.getType().isInstance(value)) {
      throw new RuntimeException("field type does not match value type");
    }

    UNSAFE.putObject(packet, UNSAFE.objectFieldOffset(field), value);
  }

  public static <T> PacketConstructor<T> findPacketConstructor(Class<T> packetClass, MethodHandles.Lookup lookup) {
    try {
      MethodHandle constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE);
      return () -> {
        try {
          // noinspection unchecked
          return (T) constructor.invoke();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      };
    } catch (NoSuchMethodException | IllegalAccessException ignored) {
    }

    return () -> {
      try {
        // noinspection unchecked
        return (T) UNSAFE.allocateInstance(packetClass);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  @FunctionalInterface
  public interface PacketConstructor<T> {
    T invoke();
  }
}
