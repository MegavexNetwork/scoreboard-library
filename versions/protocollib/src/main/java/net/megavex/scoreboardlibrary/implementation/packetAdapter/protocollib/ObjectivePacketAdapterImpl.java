package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedNumberFormat;
import net.kyori.adventure.text.Component;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

public class ObjectivePacketAdapterImpl implements ObjectivePacketAdapter {
  private static final EnumWrappers.DisplaySlot[] DISPLAY_SLOTS = EnumWrappers.DisplaySlot.values();

  private final ProtocolManager pm;
  private final String objectiveName;
  private PacketContainer removePacket;

  public ObjectivePacketAdapterImpl(@NotNull ProtocolManager pm, @NotNull String objectiveName) {
    this.pm = pm;
    this.objectiveName = objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
    packet.getStrings().write(0, objectiveName);

    int displaySlotIdx = ObjectiveConstants.displaySlotIndex(slot);
    if (new MinecraftVersion(1, 20, 2).atOrAbove()) {
      packet.getDisplaySlots().write(0, DISPLAY_SLOTS[displaySlotIdx]);
    } else {
      packet.getIntegers().write(0, displaySlotIdx);
    }

    for (Player player : players) {
      pm.sendServerPacket(player, packet);
    }
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    PacketSender<PacketContainer> sender = pm::sendServerPacket;
    LocalePacketUtil.sendLocalePackets(sender, players, locale -> {
      EnumWrappers.RenderType wrappedRenderType;
      switch (renderType) {
        case INTEGER:
          wrappedRenderType = EnumWrappers.RenderType.INTEGER;
          break;
        case HEARTS:
          wrappedRenderType = EnumWrappers.RenderType.HEARTS;
          break;
        default:
          throw new IllegalStateException();
      }

      PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
      packet.getIntegers().write(0, ObjectiveConstants.mode(packetType));
      packet.getStrings().write(0, objectiveName);
      if (WrappedNumberFormat.isSupported() && scoreFormat != null) {
        WrappedNumberFormat wrapped = ScoreFormatConverter.convert(locale, scoreFormat);
        if (MinecraftVersion.v1_20_5.atOrAbove()) {
          packet.getOptionals(BukkitConverters.getWrappedNumberFormatConverter())
            .write(0, Optional.of(wrapped));
        } else {
          packet.getNumberFormats().write(0, wrapped);
        }
      }

      Component translatedValue = GlobalTranslator.render(value, locale);
      if (MinecraftVersion.AQUATIC_UPDATE.atOrAbove()) {
        packet.getChatComponents().write(0, ComponentConversions.wrapAdventureComponent(translatedValue));
      } else {
        String legacyValue = LegacyFormatUtil.limitLegacyText(
          legacySection().serialize(translatedValue),
          ObjectiveConstants.LEGACY_VALUE_CHAR_LIMIT
        );
        packet.getStrings().write(1, legacyValue);
      }

      packet.getRenderTypes().write(0, wrappedRenderType);
      return packet;
    });
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = pm.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
      removePacket.getIntegers().write(0, ObjectiveConstants.MODE_REMOVE);
      removePacket.getStrings().write(0, objectiveName);
    }

    for (Player player : players) {
      pm.sendServerPacket(player, removePacket);
    }
  }

  @Override
  public void sendScore(
    @NotNull Collection<Player> players,
    @NotNull String entry,
    int value,
    @Nullable Component display,
    @Nullable ScoreFormat scoreFormat
  ) {

    PacketSender<PacketContainer> sender = pm::sendServerPacket;
    LocalePacketUtil.sendLocalePackets(sender, players, locale -> {
      PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
      packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);
      packet.getStrings().write(0, entry)
        .write(1, objectiveName);
      packet.getIntegers().write(0, value);

      if (WrappedNumberFormat.isSupported() && scoreFormat != null) {
        WrappedNumberFormat wrapped = ScoreFormatConverter.convert(locale, scoreFormat);
        if (MinecraftVersion.v1_20_5.atOrAbove()) {
          packet.getOptionals(BukkitConverters.getWrappedNumberFormatConverter())
            .write(1, Optional.of(wrapped));
        } else {
          packet.getNumberFormats().write(0, wrapped);
        }
      }

      if (MinecraftVersion.v1_20_4.atOrAbove() && display != null) {
        WrappedChatComponent wrapped = ComponentConversions.wrapAdventureComponent(GlobalTranslator.render(display, locale));
        if (MinecraftVersion.v1_20_5.atOrAbove()) {
          packet.getChatComponents().write(0, wrapped);
        } else {
          packet.getOptionals(BukkitConverters.getWrappedChatComponentConverter())
            .write(0, Optional.of(wrapped));
        }
      }

      return packet;
    });

  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    PacketContainer packet;
    if (new MinecraftVersion(1, 20, 3).atOrAbove()) {
      packet = pm.createPacket(PacketType.Play.Server.RESET_SCORE);
      packet.getStrings().write(0, entry)
        .write(1, objectiveName);
    } else {
      packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
      packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
      packet.getStrings().write(0, entry)
        .write(1, objectiveName);
    }

    for (Player player : players) {
      pm.sendServerPacket(player, packet);
    }
  }
}
