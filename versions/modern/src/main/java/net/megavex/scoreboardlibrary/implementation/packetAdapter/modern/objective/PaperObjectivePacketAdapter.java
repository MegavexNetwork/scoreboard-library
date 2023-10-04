package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PaperObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  private AdventureComponent lastValue;

  public PaperObjectivePacketAdapter(@NotNull PacketAdapterImpl packetAdapter, @NotNull String objectiveName) {
    super(packetAdapter, objectiveName);
  }

  @Override
  public void sendProperties(@NotNull Collection<Player> players, @NotNull ObjectivePacketType packetType, @NotNull Component value, @NotNull ObjectiveRenderType renderType, boolean renderRequired) {
    AdventureComponent nmsValue;
    if (this.lastValue != null && this.lastValue.adventure$component() == value) {
      nmsValue = this.lastValue;
    } else {
      nmsValue = NativeAdventureUtil.fromAdventureComponent(value);
      this.lastValue = nmsValue;
    }

    ClientboundSetObjectivePacket packet = createPacket(packetType, nmsValue, renderType);
    packetAdapter().sendPacket(players, packet);
  }
}
