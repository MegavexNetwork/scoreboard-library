package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MinecraftClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ModernPacketSender implements PacketSender<Object> {
  public static final ModernPacketSender INSTANCE = new ModernPacketSender();

  private static final MethodHandle GET_HANDLE;
  private static final MethodHandle PLAYER_CONNECTION;
  private static final MethodHandle SEND_PACKET;

  private ModernPacketSender() {
  }

  static {
    Class<?> craftPlayer = ReflectUtil.getClassOrThrow(MinecraftClasses.craftBukkit("entity.CraftBukkit"));

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
    MethodHandle sendPacket = null;

    String[] sendPacketNames = {"a", "sendPacket", "b"};
    for (String name : sendPacketNames) {
      try {
        sendPacket = lookup.findVirtual(ServerGamePacketListenerImpl.class, name, sendMethodType);
      } catch (NoSuchMethodException ignored) {
      } catch (IllegalAccessException e) {
        throw new ExceptionInInitializerError(e);
      }
    }

    if (sendPacket == null) {
      throw new ExceptionInInitializerError(new RuntimeException("Couldn't find send packet method"));
    }

    SEND_PACKET = sendPacket;
  }

  @Override
  public void sendPacket(Player player, Object packet) {
    try {
      ServerPlayer handle = (ServerPlayer) GET_HANDLE.invoke(player);
      ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl) PLAYER_CONNECTION.invoke(handle);
      SEND_PACKET.invoke(connection, packet);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't send packet to player", e);
    }
  }
}
