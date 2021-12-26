package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.sidebar;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util.NativeAdventureUtil;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Sidebar implementation for PaperMC, using its native Adventure support to make performance better
 */
public class PaperSidebarNMSImpl extends AbstractSidebarImpl {

    private PacketPlayOutScoreboardObjective createPacket;
    private PacketPlayOutScoreboardObjective updatePacket;

    public PaperSidebarNMSImpl(NMSImpl impl, Sidebar sidebar) {
        super(impl, sidebar);
    }

    private void initialisePackets() {
        if (createPacket != null && updatePacket != null) return;

        createPacket = objectivePacketConstructor.invoke();
        updatePacket = objectivePacketConstructor.invoke();
        createObjectivePacket(createPacket, 0);
        createObjectivePacket(updatePacket, 2);
        updateTitle(sidebar.title());
    }

    private void updateDisplayName(PacketPlayOutScoreboardObjective packet, IChatBaseComponent displayName) {
        UnsafeUtilities.setField(objectiveDisplayNameField, packet, displayName);
    }

    @Override
    public void updateTitle(Component displayName) {
        if (createPacket != null && updatePacket != null) {
            IChatBaseComponent vanilla = NativeAdventureUtil.fromAdventureComponent(displayName);
            updateDisplayName(createPacket, vanilla);
            updateDisplayName(updatePacket, vanilla);
        }
    }

    @Override
    protected void sendObjectivePacket(Collection<Player> players, boolean create) {
        initialisePackets();
        impl.sendPacket(players, create ? createPacket : updatePacket);
    }
}
