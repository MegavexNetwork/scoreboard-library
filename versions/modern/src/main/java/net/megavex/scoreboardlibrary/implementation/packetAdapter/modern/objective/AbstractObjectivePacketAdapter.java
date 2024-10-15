package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractObjectivePacketAdapter implements ObjectivePacketAdapter {
  protected final String objectiveName;
  private Object removePacket;

  public AbstractObjectivePacketAdapter(@NotNull String objectiveName) {
    this.objectiveName = objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    ModernPacketSender.INSTANCE.sendPacket(players, createDisplayPacket(slot));
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      this.removePacket = PacketAccessors.OBJECTIVE_CONSTRUCTOR.invoke();
      PacketAccessors.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      PacketAccessors.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    ModernPacketSender.INSTANCE.sendPacket(players, removePacket);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Object packet;
    if (PacketAccessors.IS_1_20_3_OR_ABOVE) {
      packet = Objects.requireNonNull(PacketAccessors.RESET_SCORE_CONSTRUCTOR).invoke(entry, objectiveName);
    } else {
      packet = Objects.requireNonNull(PacketAccessors.SCORE_1_20_2_CONSTRUCTOR)
        .invoke(ServerScoreboard.Method.REMOVE, objectiveName, entry, 0);
    }
    ModernPacketSender.INSTANCE.sendPacket(players, packet);
  }

  protected @NotNull Object createDisplayPacket(@NotNull ObjectiveDisplaySlot displaySlot) {
    Object packet;
    if (PacketAccessors.IS_1_20_2_OR_ABOVE) {
      packet = new ClientboundSetDisplayObjectivePacket(DisplaySlotProvider.toNms(displaySlot), null);
    } else {
      packet = Objects.requireNonNull(PacketAccessors.DISPLAY_1_20_1_CONSTRUCTOR)
        .invoke(ObjectiveConstants.displaySlotIndex(displaySlot), null);
    }
    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    return packet;
  }

  protected @NotNull Object createScorePacket(
    @NotNull String entry,
    int value,
    @Nullable net.minecraft.network.chat.Component nmsDisplay,
    @Nullable Object numberFormat
  ) {
    if (PacketAccessors.IS_1_20_5_OR_ABOVE) {
      return new ClientboundSetScorePacket(entry, objectiveName, value, Optional.ofNullable(nmsDisplay), Optional.ofNullable((NumberFormat) numberFormat));
    } else if (PacketAccessors.IS_1_20_3_OR_ABOVE) {
      return Objects.requireNonNull(PacketAccessors.SCORE_1_20_3_CONSTRUCTOR)
        .invoke(entry, objectiveName, value, nmsDisplay, numberFormat);
    } else {
      return Objects.requireNonNull(PacketAccessors.SCORE_1_20_2_CONSTRUCTOR)
        .invoke(ServerScoreboard.Method.CHANGE, objectiveName, entry, value);
    }
  }

  protected @NotNull Object createObjectivePacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull net.minecraft.network.chat.Component nmsValue,
    @NotNull ObjectiveRenderType renderType,
    @Nullable Object numberFormat
  ) {
    Object packet = PacketAccessors.OBJECTIVE_CONSTRUCTOR.invoke();
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.OBJECTIVE_VALUE_FIELD.set(packet, nmsValue);

    if (PacketAccessors.IS_1_20_3_OR_ABOVE) {
      assert PacketAccessors.OBJECTIVE_NUMBER_FORMAT_FIELD != null;
      Object value;
      if (PacketAccessors.IS_1_20_5_OR_ABOVE) {
        value = Optional.ofNullable(numberFormat);
      } else {
        value = numberFormat;
      }
      PacketAccessors.OBJECTIVE_NUMBER_FORMAT_FIELD.set(packet, value);
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
