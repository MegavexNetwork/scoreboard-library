package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import java.lang.reflect.Method;

public class OtherAccessors {
  public static final Class<Object> packetClass = RandomUtils.getServerClass("Packet");
  public static final Class<Object> craftPlayerClass = RandomUtils.getCraftBukkitClass("entity.CraftPlayer");

  public static final Method craftPlayerGetHandleMethod = RandomUtils.getMethod(craftPlayerClass, "getHandle", null);

  private static final Class<Object> enumChatFormatClass = RandomUtils.getServerClass("EnumChatFormat");
  public static final Method enumChatFormatBStaticMethod = RandomUtils.getStaticMethod(enumChatFormatClass, "b", new Class<?>[]{String.class});
  public static final Method enumChatFormatBMethod = RandomUtils.getMethod(enumChatFormatClass, "b", null);
}
