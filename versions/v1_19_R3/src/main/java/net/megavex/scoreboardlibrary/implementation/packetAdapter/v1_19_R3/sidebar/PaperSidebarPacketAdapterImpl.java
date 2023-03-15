package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.sidebar;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Sidebar implementation for PaperMC, using its native Adventure support to make performance better
 */
public class PaperSidebarPacketAdapterImpl extends AbstractSidebarImpl {
  private ClientboundSetObjectivePacket createPacket;
  private ClientboundSetObjectivePacket updatePacket;

  public PaperSidebarPacketAdapterImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);
  }

  private void initialisePackets() {
    if (createPacket == null || updatePacket == null) {
      createPacket = objectivePacketConstructor.invoke();
      updatePacket = objectivePacketConstructor.invoke();
      createObjectivePacket(createPacket, MODE_CREATE);
      createObjectivePacket(updatePacket, MODE_UPDATE);
      updateTitle(sidebar().title());
    }
  }

  private void updateDisplayName(ClientboundSetObjectivePacket packet, net.minecraft.network.chat.Component displayName) {
    UnsafeUtilities.setField(objectiveDisplayNameField, packet, displayName);
  }

  @Override
  public void updateTitle(@NotNull Component displayName) {
    if (createPacket != null && updatePacket != null) {
      var vanilla = NativeAdventureUtil.fromAdventureComponent(displayName);
      updateDisplayName(createPacket, vanilla);
      updateDisplayName(updatePacket, vanilla);
    }
  }

  @Override
  public void sendObjectivePacket(@NotNull Collection<Player> players, @NotNull ObjectivePacket type) {
    initialisePackets();
    packetAdapter().sendPacket(players, type == ObjectivePacket.CREATE ? createPacket : updatePacket);
  }
}
