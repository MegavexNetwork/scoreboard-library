package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

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
      this.removePacket = ReflectUtil.findEmptyConstructor(ClientboundSetObjectivePacket.class).invoke();
      PacketAccessors.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      PacketAccessors.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    sender.sendPacket(players, removePacket);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Packet<?> packet;
    try {
      Class.forName("net.minecraft.network.protocol.game.ClientboundResetScorePacket"); // Added in 1.20.3
      packet = new ClientboundResetScorePacket(entry, objectiveName);
    } catch (ClassNotFoundException ignored) {
      packet = Objects.requireNonNull(PacketAccessors.SCORE_LEGACY_CONSTRUCTOR)
        .invoke(ServerScoreboard.Method.REMOVE, objectiveName, entry, 0);
    }
    sender.sendPacket(players, packet);
  }

  protected @NotNull ClientboundSetDisplayObjectivePacket createDisplayPacket(@NotNull ObjectiveDisplaySlot displaySlot) {
    ClientboundSetDisplayObjectivePacket packet;
    try {
      Class.forName("net.minecraft.world.scores.DisplaySlot"); // Added in 1.20.2
      packet = new ClientboundSetDisplayObjectivePacket(DisplaySlotProvider.toNms(displaySlot), null);
    } catch (ClassNotFoundException ignored) {
      packet = Objects.requireNonNull(PacketAccessors.DISPLAY_LEGACY_CONSTRUCTOR)
        .invoke(ObjectiveConstants.displaySlotIndex(displaySlot), null);
    }

    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    return packet;
  }

  protected @NotNull ClientboundSetScorePacket createScorePacket(
    @NotNull String entry,
    int value,
    @Nullable net.minecraft.network.chat.Component nmsDisplay,
    @Nullable Object numberFormat
  ) {
    if (PacketAccessors.HAS_NUMBER_FORMAT) {
      return new ClientboundSetScorePacket(entry, objectiveName, value, nmsDisplay, (NumberFormat) numberFormat);
    } else {
      return Objects.requireNonNull(PacketAccessors.SCORE_LEGACY_CONSTRUCTOR)
        .invoke(ServerScoreboard.Method.CHANGE, objectiveName, entry, value);
    }
  }

  protected @NotNull ClientboundSetObjectivePacket createObjectivePacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull net.minecraft.network.chat.Component nmsValue,
    @NotNull ObjectiveRenderType renderType,
    @Nullable Object numberFormat
  ) {
    ClientboundSetObjectivePacket packet = PacketAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.OBJECTIVE_VALUE_FIELD.set(packet, nmsValue);

    if (numberFormat != null) {
      assert PacketAccessors.OBJECTIVE_NUMBER_FORMAT_FIELD != null;
      PacketAccessors.OBJECTIVE_NUMBER_FORMAT_FIELD.set(packet, (NumberFormat) numberFormat);
    }

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
