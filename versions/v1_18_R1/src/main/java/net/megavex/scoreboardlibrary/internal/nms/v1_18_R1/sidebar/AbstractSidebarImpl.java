package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.sidebar;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.SidebarNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collection;

import static net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities.getField;

public abstract class AbstractSidebarImpl extends SidebarNMS<Packet<?>, NMSImpl> {

    static final UnsafeUtilities.PacketConstructor<PacketPlayOutScoreboardObjective> objectivePacketConstructor = UnsafeUtilities
            .findPacketConstructor(PacketPlayOutScoreboardObjective.class, MethodHandles.lookup());
    static final Field objectiveNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "d"),
            objectiveDisplayNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "e"),
            objectiveRenderTypeField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "f");
    private static final Field objectiveModeField = getField(PacketPlayOutScoreboardObjective.class, "g");

    public AbstractSidebarImpl(NMSImpl impl, Sidebar sidebar) {
        super(impl, sidebar);
    }

    protected void createObjectivePacket(PacketPlayOutScoreboardObjective packet, int mode) {
        UnsafeUtilities.setField(objectiveNameField, packet, impl.objectiveName);
        UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(objectiveModeField), mode);
        UnsafeUtilities.setField(objectiveRenderTypeField, packet, IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
    }

    @Override
    public void removeLine(Collection<Player> players, String line) {
        impl.sendPacket(players, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.b, impl.objectiveName, line, 0));
    }

    @Override
    public void score(Collection<Player> players, int score, String line) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, impl.objectiveName, line, score);
        impl.sendPacket(players, packet);
    }
}
