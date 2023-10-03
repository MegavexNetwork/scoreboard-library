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
  private AdventureComponent lastDisplayName;

  public PaperObjectivePacketAdapter(@NotNull PacketAdapterImpl packetAdapter, @NotNull String objectiveName) {
    super(packetAdapter, objectiveName);
  }

  @Override
  public void sendProperties(@NotNull Collection<Player> players, @NotNull ObjectivePacketType packetType, @NotNull Component displayName, @NotNull ObjectiveRenderType renderType, boolean renderRequired) {
    AdventureComponent nmsDisplayName;
    if (this.lastDisplayName != null && this.lastDisplayName.adventure$component() == displayName) {
      nmsDisplayName = this.lastDisplayName;
    } else {
      nmsDisplayName = NativeAdventureUtil.fromAdventureComponent(displayName);
      this.lastDisplayName = nmsDisplayName;
    }

    ClientboundSetObjectivePacket packet = createPacket(packetType, nmsDisplayName, renderType);
    packetAdapter().sendPacket(players, packet);
  }
}
