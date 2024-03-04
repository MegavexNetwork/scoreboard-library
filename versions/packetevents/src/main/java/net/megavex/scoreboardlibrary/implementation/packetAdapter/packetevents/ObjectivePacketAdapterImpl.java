package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class ObjectivePacketAdapterImpl implements ObjectivePacketAdapter {
  private final PacketSender<PacketWrapper<?>> provider;
  private final String objectiveName;
  private WrapperPlayServerScoreboardObjective removePacket;

  public ObjectivePacketAdapterImpl(@NotNull PacketSender<PacketWrapper<?>> provider, @NotNull String objectiveName) {
    this.provider = provider;
    this.objectiveName = objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    WrapperPlayServerDisplayScoreboard packet = new WrapperPlayServerDisplayScoreboard(ObjectiveConstants.displaySlotIndex(slot), objectiveName);
    provider.sendPacket(players, packet);
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
      provider,
      players,
      locale -> createObjectivePacket(locale, packetType, value, renderType, scoreFormat)
    );
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = new WrapperPlayServerScoreboardObjective(
        objectiveName,
        WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE,
        null,
        null
      );
    }
    provider.sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(
    @NotNull Collection<Player> players,
    @NotNull String entry,
    int value,
    @Nullable Component display,
    @Nullable ScoreFormat scoreFormat
  ) {
    LocalePacketUtil.sendLocalePackets(
      provider,
      players,
      locale -> new WrapperPlayServerUpdateScore(
        entry,
        WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
        objectiveName,
        value,
        display != null ? GlobalTranslator.render(display, locale) : null,
        ScoreFormatConverter.convert(locale, scoreFormat)
      )
    );
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    WrapperPlayServerUpdateScore packet = new WrapperPlayServerUpdateScore(
      entry,
      WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
      objectiveName,
      Optional.empty()
    );
    provider.sendPacket(players, packet);
  }

  private WrapperPlayServerScoreboardObjective createObjectivePacket(
    @NotNull Locale locale,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat format
  ) {
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

    return new WrapperPlayServerScoreboardObjective(
      objectiveName,
      peMode,
      GlobalTranslator.render(value, locale),
      peRenderType,
      ScoreFormatConverter.convert(locale, format)
    );
  }
}
