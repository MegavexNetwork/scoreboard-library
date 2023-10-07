package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.ObjectiveConstants;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class ObjectivePacketAdapterImpl extends ObjectivePacketAdapter<PacketWrapper<?>, PacketAdapterImpl> {
  private WrapperPlayServerScoreboardObjective removePacket;

  public ObjectivePacketAdapterImpl(@NotNull PacketAdapterImpl packetAdapter, @NotNull String objectiveName) {
    super(packetAdapter, objectiveName);
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    int slotIndex = ObjectiveConstants.displaySlotIndex(slot, false);
    int legacySlotIndex = ObjectiveConstants.displaySlotIndex(slot, true);

    if (slotIndex == legacySlotIndex) {
      packetAdapter().sendPacket(players, new WrapperPlayServerDisplayScoreboard(slotIndex, objectiveName()));
      return;
    }

    WrapperPlayServerDisplayScoreboard modern = new WrapperPlayServerDisplayScoreboard(slotIndex, objectiveName());
    WrapperPlayServerDisplayScoreboard legacy = new WrapperPlayServerDisplayScoreboard(legacySlotIndex, objectiveName());
    for (Player player : players) {
      if (!packetAdapter().isLegacy(player)) {
        packetAdapter().sendPacket(player, modern);
      } else {
        packetAdapter().sendPacket(player, legacy);
      }
    }
  }

  @Override
  public void sendProperties(@NotNull Collection<Player> players, @NotNull ObjectivePacketType packetType, @NotNull Component value, @NotNull ObjectiveRenderType renderType, boolean renderRequired) {
    if (!renderRequired) {
      packetAdapter().sendPacket(players, createObjectivePacket(packetType, value, renderType));
      return;
    }

    LocalePacketUtil.sendLocalePackets(
      packetAdapter().localeProvider(),
      null,
      packetAdapter(),
      players, locale -> createObjectivePacket(packetType, GlobalTranslator.render(value, locale), renderType)
    );
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = new WrapperPlayServerScoreboardObjective(objectiveName(), WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE, null, null);
    }
    packetAdapter().sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value) {
    WrapperPlayServerUpdateScore packet = new WrapperPlayServerUpdateScore(entry, WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM, objectiveName(), Optional.of(value));
    packetAdapter().sendPacket(players, packet);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    WrapperPlayServerUpdateScore packet = new WrapperPlayServerUpdateScore(entry, WrapperPlayServerUpdateScore.Action.REMOVE_ITEM, objectiveName(), Optional.empty());
    packetAdapter().sendPacket(players, packet);
  }

  private WrapperPlayServerScoreboardObjective createObjectivePacket(@NotNull ObjectivePacketType packetType, @NotNull Component value, @NotNull ObjectiveRenderType renderType) {
    WrapperPlayServerScoreboardObjective.ObjectiveMode peMode;
    switch (packetType) {
      case CREATE:
        peMode = WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE;
        break;
      case UPDATE:
        peMode = WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE;
        break;
      default:
        throw new IllegalStateException();
    }

    WrapperPlayServerScoreboardObjective.RenderType peRenderType;
    switch (renderType) {
      case INTEGER:
        peRenderType = WrapperPlayServerScoreboardObjective.RenderType.INTEGER;
        break;
      case HEARTS:
        peRenderType = WrapperPlayServerScoreboardObjective.RenderType.HEARTS;
        break;
      default:
        throw new IllegalStateException();
    }

    return new WrapperPlayServerScoreboardObjective(objectiveName(), peMode, value, peRenderType);
  }
}
