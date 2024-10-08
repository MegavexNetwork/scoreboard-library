package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.PacketAccessors.*;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.RandomUtils.is1_8Plus;

public class ObjectivePacketAdapterImpl implements ObjectivePacketAdapter {
  private static final ConstructorAccessor<Object> packetPlayOutScoreboardObjectiveConstructor = ReflectUtil.findConstructorOrThrow(packetPlayOutScoreboardObjectiveClass);
  private static final ConstructorAccessor<Object> packetPlayOutScoreboardDisplayObjectiveConstructor = ReflectUtil.findConstructorOrThrow(packetPlayOutScoreboardDisplayObjectiveClass);
  private static final ConstructorAccessor<Object> packetPlayOutScoreboardScoreConstructor = ReflectUtil.findConstructorOrThrow(packetPlayOutScoreboardScoreClass, String.class);

  private static final Object enumScoreboardActionChange = is1_8Plus ? RandomUtils.getStaticField(enumScoreboardActionClass, "CHANGE") : null;
  private static final Object enumScoreboardHealthInteger = is1_8Plus ? RandomUtils.getStaticField(enumScoreboardHealthDisplayClass, "INTEGER") : null;
  private static final Object enumScoreboardHealthHearts = is1_8Plus ? RandomUtils.getStaticField(enumScoreboardHealthDisplayClass, "HEARTS") : null;

  private final PacketSender<Object> sender;
  private final String objectiveName;
  private Object removePacket;

  public ObjectivePacketAdapterImpl(@NotNull PacketSender<Object> sender, @NotNull String objectiveName) {
    this.sender = sender;
    this.objectiveName = objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    Object packet = packetPlayOutScoreboardDisplayObjectiveConstructor.invoke();
    PacketAccessors.DISPLAY_OBJECTIVE_POSITION.set(packet, ObjectiveConstants.displaySlotIndex(slot));
    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    sender.sendPacket(players, packet);
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    LocalePacketUtil.sendLocalePackets(
      sender,
      players,
      locale -> createPropertiesPacket(packetType, GlobalTranslator.render(value, locale), renderType)
    );
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = packetPlayOutScoreboardObjectiveConstructor.invoke();
      PacketAccessors.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      PacketAccessors.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    sender.sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(
    @NotNull Collection<Player> players,
    @NotNull String entry,
    int value,
    @Nullable Component display,
    @Nullable ScoreFormat scoreFormat
  ) {
    Object packet = packetPlayOutScoreboardScoreConstructor.invoke(entry);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.SCORE_VALUE_FIELD.set(packet, value);
    if (is1_8Plus) {
      PacketAccessors.SCORE_ACTION_FIELD_1_8.set(packet, enumScoreboardActionChange);
    } else {
      PacketAccessors.SCORE_ACTION_FIELD_1_7.set(packet, 0);
    }

    sender.sendPacket(players, packet);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Object packet = packetPlayOutScoreboardScoreConstructor.invoke(entry);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    sender.sendPacket(players, packet);
  }

  private @NotNull Object createPropertiesPacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType
  ) {
    Object packet = packetPlayOutScoreboardObjectiveConstructor.invoke();
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));

    String legacyValue = LegacyFormatUtil.limitLegacyText(LegacyComponentSerializer.legacySection().serialize(value), ObjectiveConstants.LEGACY_VALUE_CHAR_LIMIT);
    PacketAccessors.OBJECTIVE_DISPLAY_NAME_FIELD.set(packet, legacyValue);

    if (is1_8Plus) {
      Object nmsRenderType;
      switch (renderType) {
        case INTEGER:
          nmsRenderType = enumScoreboardHealthInteger;
          break;
        case HEARTS:
          nmsRenderType = enumScoreboardHealthHearts;
          break;
        default:
          throw new IllegalStateException();
      }
      PacketAccessors.OBJECTIVE_HEALTH_DISPLAY_FIELD.set(packet, nmsRenderType);
    }

    return packet;
  }
}
