package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import java.lang.reflect.Method;

public final class OtherAccessors {
  public static final Class<?> PACKET_CLASS = RandomUtils.getClassOrThrow(RandomUtils.server("Packet"));
  public static final Class<?> CRAFT_PLAYER_CLASS = RandomUtils.getClassOrThrow(RandomUtils.craftBukkit("entity.CraftPlayer"));

  public static final Method CRAFT_PLAYER_GET_HANDLE = RandomUtils.getMethod(CRAFT_PLAYER_CLASS, "getHandle", null);

  private static final Class<Object> ENUM_CHAT_FORMAT_CLASS = RandomUtils.getClassOrThrow(RandomUtils.server("EnumChatFormat"));
  public static final Method ENUM_CHAT_FORMAT_B_STATIC = RandomUtils.getStaticMethod(ENUM_CHAT_FORMAT_CLASS, "b", new Class<?>[]{String.class});
  public static final Method ENUM_CHAT_FORMAT_B = RandomUtils.getMethod(ENUM_CHAT_FORMAT_CLASS, "b", null);

  private OtherAccessors() {
  }
}
