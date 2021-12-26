package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.sidebar;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;

import static net.kyori.adventure.text.Component.empty;

public class SidebarNMSImpl extends AbstractSidebarImpl {

    private final ClientboundSetObjectivePacket createPacket;
    private final ClientboundSetObjectivePacket updatePacket;

    public SidebarNMSImpl(NMSImpl impl, Sidebar sidebar) {
        super(impl, sidebar);

        Locale locale = sidebar.locale();
        if (locale != null) {
            createPacket = objectivePacketConstructor.invoke();
            createObjectivePacket(createPacket, 0);
            updateDisplayName(createPacket, empty(), locale);

            updatePacket = objectivePacketConstructor.invoke();
            createObjectivePacket(updatePacket, 2);
            updateDisplayName(updatePacket, empty(), locale);
        } else {
            createPacket = null;
            updatePacket = null;
        }
    }

    private void updateDisplayName(ClientboundSetObjectivePacket packet, Component displayName, Locale locale) {
        net.minecraft.network.chat.Component vanilla = impl.fromAdventure(displayName, locale);
        UnsafeUtilities.setField(objectiveDisplayNameField, packet, vanilla);
    }

    @Override
    public void updateTitle(Component displayName) {
        Locale locale = sidebar.locale();
        if (locale != null) {
            updateDisplayName(createPacket, displayName, locale);
            updateDisplayName(updatePacket, displayName, locale);
        }
    }

    @Override
    protected void sendObjectivePacket(Collection<Player> players, boolean create) {
        if (sidebar.locale() != null) {
            impl.sendPacket(players, create ? createPacket : updatePacket);
        } else {
            ScoreboardManagerNMS.sendLocaleDependantPackets(sidebar.locale(), impl, players, locale -> {
                ClientboundSetObjectivePacket packet = objectivePacketConstructor.invoke();
                createObjectivePacket(createPacket, create ? 0 : 2);
                updateDisplayName(packet, sidebar.title(), locale);
                return packet;
            });
        }
    }
}
