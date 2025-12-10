package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PaperObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public PaperObjectivePacketAdapter(@NotNull String objectiveName) {
    super(objectiveName);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value, @Nullable Component display, @Nullable ScoreFormat scoreFormat) {
    net.minecraft.network.chat.Component nmsDisplay = display == null ? null : ModernComponentProvider.fromAdventure(display, null);
    Object numberFormat = ScoreFormatConverter.convert(null, scoreFormat);
    Object packet = createScorePacket(entry, value, nmsDisplay, numberFormat);
    ModernPacketSender.INSTANCE.sendPacket(players, packet);
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    AdventureComponent nmsValue = NativeAdventureUtil.fromAdventureComponent(value);
    Object numberFormat = ScoreFormatConverter.convert(null, scoreFormat);
    Object packet = createObjectivePacket(packetType, nmsValue, renderType, numberFormat);
    ModernPacketSender.INSTANCE.sendPacket(players, packet);
  }
}
