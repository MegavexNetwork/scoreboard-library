package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PacketEventsSender implements PacketSender<PacketWrapper<?>> {
  private final PacketEventsAPI<?> packetEvents;

  public PacketEventsSender(@NotNull PacketEventsAPI<?> packetEvents) {
    this.packetEvents = packetEvents;
  }

  //@SuppressWarnings("UnstableApiUsage, deprecation")
  @Override
  public void sendPacket(Player player, PacketWrapper<?> packet) {
    packetEvents.getPlayerManager().sendPacket(player, packet);

    /*
    ServerVersion serverVer = packetEvents.getServerManager().getVersion();
    ClientVersion clientVer = packetEvents.getPlayerManager().getClientVersion(player);
    if (serverVer.isOlderThan(ServerVersion.V_1_13) || clientVer.isNewerThanOrEquals(ClientVersion.V_1_13)) {
      packetEvents.getPlayerManager().sendPacket(player, packet);
      return;
    }

    // This is a hack to send packets directly skipping plugins such as ViaVersion and ViaRewind
    // Based on https://discord.com/channels/721686193061888071/755472595096174873/1193239879560212520
    // (PacketEvents Discord)

    User user = packetEvents.getPlayerManager().getUser(player);
    if (user == null) {
      return;
    }

    Object channel = user.getChannel();
    if (!ChannelHelper.isOpen(channel)) {
      return;
    }

    String viaEncoder = "via-encoder";
    if (ChannelHelper.getPipelineHandler(channel, viaEncoder) == null) {
      packetEvents.getPlayerManager().sendPacket(player, packet);
      return;
    }

    packet.buffer = ChannelHelper.pooledByteBuf(channel);

    int id = Objects.requireNonNull(packet.getPacketTypeData().getPacketType()).getId(user.getClientVersion());
    ByteBufHelper.writeVarInt(packet.buffer, id);
    packet.setServerVersion(clientVer.toServerVersion());
    packet.write();
    ChannelHelper.writeAndFlushInContext(channel, viaEncoder, packet.buffer);
    */
  }
}
