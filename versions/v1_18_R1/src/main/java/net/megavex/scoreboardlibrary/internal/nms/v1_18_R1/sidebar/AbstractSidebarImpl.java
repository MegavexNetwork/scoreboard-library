package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.sidebar;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.SidebarNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collection;

import static net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities.getField;

public abstract class AbstractSidebarImpl extends SidebarNMS<Packet<?>, NMSImpl> {

    static final UnsafeUtilities.PacketConstructor<ClientboundSetObjectivePacket> objectivePacketConstructor = UnsafeUtilities
            .findPacketConstructor(ClientboundSetObjectivePacket.class, MethodHandles.lookup());
    static final Field objectiveNameField = UnsafeUtilities.getField(ClientboundSetObjectivePacket.class, "d"),
            objectiveDisplayNameField = UnsafeUtilities.getField(ClientboundSetObjectivePacket.class, "e"),
            objectiveRenderTypeField = UnsafeUtilities.getField(ClientboundSetObjectivePacket.class, "f");
    private static final Field objectiveModeField = getField(ClientboundSetObjectivePacket.class, "g");

    public AbstractSidebarImpl(NMSImpl impl, Sidebar sidebar) {
        super(impl, sidebar);
    }

    protected void createObjectivePacket(ClientboundSetObjectivePacket packet, int mode) {
        UnsafeUtilities.setField(objectiveNameField, packet, impl.objectiveName);
        UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(objectiveModeField), mode);
        UnsafeUtilities.setField(objectiveRenderTypeField, packet, ObjectiveCriteria.RenderType.INTEGER);
    }

    @Override
    public void removeLine(Collection<Player> players, String line) {
        impl.sendPacket(players, new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, impl.objectiveName, line, 0));
    }

    @Override
    public void score(Collection<Player> players, int score, String line) {
        ClientboundSetScorePacket packet = new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, impl.objectiveName, line, score);
        impl.sendPacket(players, packet);
    }
}
