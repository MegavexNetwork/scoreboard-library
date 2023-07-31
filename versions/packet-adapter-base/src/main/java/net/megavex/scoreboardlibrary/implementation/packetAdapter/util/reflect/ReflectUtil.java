package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class ReflectUtil {
  // Inspired by
  // https://github.com/dmulloy2/ProtocolLib/blob/02e917cd08cf5b37a52052e22b223272a040e0df/src/main/java/com/comphenix/protocol/reflect/accessors/MethodHandleHelper.java

  private static final Unsafe UNSAFE;
  private static final MethodHandles.Lookup LOOKUP;
  private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);
  private static final MethodType VIRTUAL_FIELD_SETTER = MethodType.methodType(void.class, Object.class, Object.class);

  static {
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      UNSAFE = (Unsafe) theUnsafe.get(null);
    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandles.Lookup lookup;
    try {
      Field trustedLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      long offset = UNSAFE.staticFieldOffset(trustedLookup);
      Object baseValue = UNSAFE.staticFieldBase(trustedLookup);
      lookup = (MethodHandles.Lookup) UNSAFE.getObject(baseValue, offset);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      lookup = MethodHandles.lookup();
    }
    LOOKUP = lookup;
  }

  private ReflectUtil() {
  }

  public static <T, V> @NotNull FieldAccessor<T, V> fieldAccessor(@NotNull Class<T> clazz, @NotNull String name, @NotNull Class<V> valueClass) {
    try {
      MethodHandle setter = LOOKUP.findSetter(clazz, name, valueClass).asType(VIRTUAL_FIELD_SETTER);
      return new FieldAccessor<>(setter);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("couldn't get field accessor", e);
    }
  }

  public static <T> @NotNull ConstructorAccessor<T> constructorAccessor(@NotNull Class<T> clazz, @NotNull Class<?>... args) {
    try {
      MethodHandle handle = LOOKUP.findConstructor(clazz, MethodType.methodType(void.class, args));
      return new ConstructorAccessor<>(convertToGeneric(handle));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new IllegalStateException("couldn't get constructor accessor", e);
    }
  }

  public static <T> PacketConstructor<T> findPacketConstructor(Class<T> packetClass) {
    try {
      MethodHandle constructor = LOOKUP.findConstructor(packetClass, VOID_METHOD_TYPE);
      return () -> {
        try {
          // noinspection unchecked
          return (T) constructor.invoke();
        } catch (Throwable e) {
          throw new IllegalStateException("couldn't create packet instance", e);
        }
      };
    } catch (NoSuchMethodException | IllegalAccessException ignored) {
    }

    return () -> {
      try {
        // noinspection unchecked
        return (T) UNSAFE.allocateInstance(packetClass);
      } catch (Throwable e) {
        throw new IllegalStateException("couldn't allocate packet instance using unsafe", e);
      }
    };
  }

  private static MethodHandle convertToGeneric(MethodHandle handle) {
    MethodHandle target = handle.asFixedArity();
    MethodType methodType = MethodType.genericMethodType(0, true);
    target = target.asSpreader(Object[].class, handle.type().parameterCount());
    return target.asType(methodType);
  }
}
