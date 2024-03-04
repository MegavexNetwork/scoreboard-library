package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SpigotObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public SpigotObjectivePacketAdapter(@NotNull PacketAdapterProviderImpl packetAdapter, @NotNull ComponentProvider componentProvider, @NotNull String objectiveName) {
    super(packetAdapter, componentProvider, objectiveName);
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
      locale -> {
        Object numberFormat = ScoreFormatConverter.convert(componentProvider, locale, scoreFormat);
        return createPacket(packetType, componentProvider.fromAdventure(value, locale), renderType, numberFormat);
      }
    );
  }
}
