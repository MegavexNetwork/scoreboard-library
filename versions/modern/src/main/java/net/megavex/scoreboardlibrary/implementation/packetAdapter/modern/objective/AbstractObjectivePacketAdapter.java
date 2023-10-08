package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class AbstractObjectivePacketAdapter implements ObjectivePacketAdapter {
  protected final PacketSender<Packet<?>> sender;
  protected final ComponentProvider componentProvider;
  protected final String objectiveName;
  private ClientboundSetObjectivePacket removePacket;

  public AbstractObjectivePacketAdapter(@NotNull PacketSender<Packet<?>> sender, @NotNull ComponentProvider componentProvider, @NotNull String objectiveName) {
    this.sender = sender;
    this.componentProvider = componentProvider;
    this.objectiveName = objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    sender.sendPacket(players, createDisplayPacket(slot));
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      this.removePacket = ReflectUtil.findPacketConstructor(ClientboundSetObjectivePacket.class).invoke();
      PacketAccessors.SET_OBJECTIVE_NAME.set(removePacket, objectiveName);
      PacketAccessors.SET_OBJECTIVE_MODE.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    sender.sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value) {
    ClientboundSetScorePacket packet = new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, objectiveName, entry, value);
    sender.sendPacket(players, packet);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    ClientboundSetScorePacket packet = new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, objectiveName, entry, 0);
    sender.sendPacket(players, packet);
  }

  protected @NotNull ClientboundSetDisplayObjectivePacket createDisplayPacket(@NotNull ObjectiveDisplaySlot displaySlot) {
    ClientboundSetDisplayObjectivePacket packet;
    try {
      Class.forName("net.minecraft.world.scores.DisplaySlot"); // Added in 1.20.2
      packet = new ClientboundSetDisplayObjectivePacket(DisplaySlotProvider.toNms(displaySlot), null);
    } catch (ClassNotFoundException ignored) {
      ConstructorAccessor<ClientboundSetDisplayObjectivePacket> constructor = ReflectUtil.constructorAccessor(
        ClientboundSetDisplayObjectivePacket.class,
        int.class,
        Objective.class
      );
      packet = constructor.invoke(ObjectiveConstants.displaySlotIndex(displaySlot), null);
    }

    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    return packet;
  }

  protected @NotNull ClientboundSetObjectivePacket createPacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull net.minecraft.network.chat.Component nmsValue,
    @NotNull ObjectiveRenderType renderType
  ) {
    ClientboundSetObjectivePacket packet = PacketAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.OBJECTIVE_VALUE_FIELD.set(packet, nmsValue);

    ObjectiveCriteria.RenderType nmsRenderType;
    switch (renderType) {
      case INTEGER:
        nmsRenderType = ObjectiveCriteria.RenderType.INTEGER;
        break;
      case HEARTS:
        nmsRenderType = ObjectiveCriteria.RenderType.HEARTS;
        break;
      default:
        throw new IllegalStateException();
    }

    PacketAccessors.OBJECTIVE_RENDER_TYPE_FIELD.set(packet, nmsRenderType);
    return packet;
  }
}
