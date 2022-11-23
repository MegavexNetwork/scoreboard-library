package net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.sidebar;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.NMSImpl;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;

/**
 * Sidebar implementation for PaperMC, using its native Adventure support to make performance better
 */
public class PaperSidebarPacketAdapterImpl extends AbstractSidebarImpl {
  private ClientboundSetObjectivePacket createPacket;
  private ClientboundSetObjectivePacket updatePacket;

  public PaperSidebarPacketAdapterImpl(NMSImpl impl, Sidebar sidebar) {
    super(impl, sidebar);
  }

  private void initialisePackets() {
    if (createPacket == null || updatePacket == null) {
      synchronized (this) {
        if (createPacket == null || updatePacket == null) {
          createPacket = objectivePacketConstructor.invoke();
          updatePacket = objectivePacketConstructor.invoke();
          createObjectivePacket(createPacket, 0);
          createObjectivePacket(updatePacket, 2);
          updateTitle(sidebar.title());
        }
      }
    }
  }

  private void updateDisplayName(ClientboundSetObjectivePacket packet, net.minecraft.network.chat.Component displayName) {
    UnsafeUtilities.setField(objectiveDisplayNameField, packet, displayName);
  }

  @Override
  public void updateTitle(Component displayName) {
    if (createPacket != null && updatePacket != null) {
      var vanilla = NativeAdventureUtil.fromAdventureComponent(displayName);
      updateDisplayName(createPacket, vanilla);
      updateDisplayName(updatePacket, vanilla);
    }
  }

  @Override
  protected void sendObjectivePacket(Collection<Player> players, boolean create) {
    initialisePackets();
    impl.sendPacket(players, create ? createPacket:updatePacket);
  }
}
