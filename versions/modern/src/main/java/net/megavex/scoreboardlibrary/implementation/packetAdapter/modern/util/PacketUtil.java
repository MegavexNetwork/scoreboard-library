package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.CraftBukkitUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

public final class PacketUtil {
  private static final MethodHandle GET_HANDLE;
  private static final MethodHandle PLAYER_CONNECTION;
  private static final MethodHandle SEND_PACKET;

  private PacketUtil() {
  }

  static {
    Class<?> craftPlayer;
    try {
      craftPlayer = Class.forName("org.bukkit.craftbukkit." + CraftBukkitUtil.version() + ".entity.CraftPlayer");
    } catch (ClassNotFoundException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    MethodType methodType = MethodType.methodType(ServerPlayer.class);
    try {
      GET_HANDLE = lookup.findVirtual(craftPlayer, "getHandle", methodType);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandle playerConnection;
    try {
      playerConnection = lookup.findGetter(ServerPlayer.class, "c", ServerGamePacketListenerImpl.class);
    } catch (NoSuchFieldException e) {
      try {
        playerConnection = lookup.findGetter(ServerPlayer.class, "b", ServerGamePacketListenerImpl.class);
      } catch (NoSuchFieldException | IllegalAccessException ex) {
        throw new ExceptionInInitializerError(ex);
      }
    } catch (IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }

    PLAYER_CONNECTION = playerConnection;

    MethodType sendMethodType = MethodType.methodType(void.class, Packet.class);
    MethodHandle sendPacket;
    try {
      sendPacket = lookup.findVirtual(ServerGamePacketListenerImpl.class, "a", sendMethodType);
    } catch (NoSuchMethodException e) {
      try {
        sendPacket = lookup.findVirtual(ServerGamePacketListenerImpl.class, "sendPacket", sendMethodType);
      } catch (NoSuchMethodException | IllegalAccessException ex) {
        throw new ExceptionInInitializerError(ex);
      }
    } catch (IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }

    SEND_PACKET = sendPacket;
  }

  public static void sendPacket(Player player, Packet<?> packet) {
    try {
      ServerPlayer handle = (ServerPlayer) GET_HANDLE.invoke(player);
      ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl) PLAYER_CONNECTION.invoke(handle);
      SEND_PACKET.invoke(connection, packet);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't send packet to player", e);
    }
  }
}
