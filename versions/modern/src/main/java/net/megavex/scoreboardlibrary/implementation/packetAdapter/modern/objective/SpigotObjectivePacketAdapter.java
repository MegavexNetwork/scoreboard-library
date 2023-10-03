package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SpigotObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public SpigotObjectivePacketAdapter(@NotNull PacketAdapterImpl packetAdapter, @NotNull String objectiveName) {
    super(packetAdapter, objectiveName);
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull ObjectivePacketType packetType,
    @NotNull Component displayName,
    @NotNull ObjectiveRenderType renderType,
    boolean renderRequired
  ) {
    LocalePacketUtil.sendLocalePackets(
      packetAdapter().localeProvider(),
      null,
      packetAdapter(),
      players,
      locale -> {
        net.minecraft.network.chat.Component nmsDisplayName = packetAdapter().fromAdventure(displayName, locale);
        return createPacket(packetType, nmsDisplayName, renderType);
      }
    );
  }
}
