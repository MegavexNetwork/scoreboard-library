package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import java.lang.reflect.Method;

public final class OtherAccessors {
  public static final Class<?> packetClass = RandomUtils.getClassOrThrow(RandomUtils.server("Packet"));
  public static final Class<?> craftPlayerClass = RandomUtils.getClassOrThrow(RandomUtils.craftBukkit("entity.CraftPlayer"));

  public static final Method craftPlayerGetHandleMethod = RandomUtils.getMethod(craftPlayerClass, "getHandle", null);

  private static final Class<Object> enumChatFormatClass = RandomUtils.getClassOrThrow(RandomUtils.server("EnumChatFormat"));
  public static final Method enumChatFormatBStaticMethod = RandomUtils.getStaticMethod(enumChatFormatClass, "b", new Class<?>[]{String.class});
  public static final Method enumChatFormatBMethod = RandomUtils.getMethod(enumChatFormatClass, "b", null);

  private OtherAccessors() {
  }
}
