package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.ObjectiveConstants;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ObjectivePacketAdapterImpl extends ObjectivePacketAdapter<Packet<PacketListenerPlayOut>, PacketAdapterImpl> {
  private PacketPlayOutScoreboardObjective removePacket;

  public ObjectivePacketAdapterImpl(@NotNull PacketAdapterImpl packetAdapter, @NotNull String objectiveName) {
    super(packetAdapter, objectiveName);
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
    PacketAccessors.DISPLAY_OBJECTIVE_POSITION.set(packet, ObjectiveConstants.displaySlotIndex(slot));
    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName());
    packetAdapter().sendPacket(players, packet);
  }

  @Override
  public void sendProperties(@NotNull Collection<Player> players, @NotNull ObjectivePacketType packetType, @NotNull Component value, @NotNull ObjectiveRenderType renderType, boolean renderRequired) {
    if (!renderRequired) {
      packetAdapter().sendPacket(players, createPropertiesPacket(packetType, value, renderType));
      return;
    }

    LocalePacketUtil.sendLocalePackets(
      packetAdapter().localeProvider(),
      null,
      packetAdapter(),
      players,
      locale -> createPropertiesPacket(packetType, GlobalTranslator.render(value, locale), renderType)
    );
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = new PacketPlayOutScoreboardObjective();
      PacketAccessors.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName());
      PacketAccessors.OBJECTIVE_MODE_FIELD.set(removePacket, 1);
    }
    packetAdapter().sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value) {
    PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(entry);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, objectiveName());
    PacketAccessors.SCORE_VALUE_FIELD.set(packet, value);
    PacketAccessors.SCORE_ACTION_FIELD.set(packet, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
    packetAdapter().sendPacket(players, packet);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(entry);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, objectiveName());
    packetAdapter().sendPacket(players, packet);
  }

  private PacketPlayOutScoreboardObjective createPropertiesPacket(ObjectivePacketType packetType, @NotNull Component value, @NotNull ObjectiveRenderType renderType) {
    PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName());
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));

    String legacyValue = LegacyFormatUtil.limitLegacyText(LegacyComponentSerializer.legacySection().serialize(value), ObjectiveConstants.LEGACY_VALUE_CHAR_LIMIT);
    PacketAccessors.OBJECTIVE_DISPLAY_NAME_FIELD.set(packet, legacyValue);

    IScoreboardCriteria.EnumScoreboardHealthDisplay nmsRenderType;
    switch (renderType) {
      case INTEGER:
        nmsRenderType = IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
        break;
      case HEARTS:
        nmsRenderType = IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS;
        break;
      default:
        throw new IllegalStateException();
    }

    PacketAccessors.OBJECTIVE_HEALTH_DISPLAY_FIELD.set(packet, nmsRenderType);
    return packet;
  }
}
