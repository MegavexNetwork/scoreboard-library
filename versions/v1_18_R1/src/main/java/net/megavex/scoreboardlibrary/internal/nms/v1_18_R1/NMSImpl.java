package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.ScoreboardLibraryLogger;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.SidebarNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.sidebar.PaperSidebarNMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.sidebar.SidebarNMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.team.PaperTeamNMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.team.TeamNMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util.ProtocolSupportUtil;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Objects;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
import static net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities.getField;
import static net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities.setField;

public class NMSImpl extends ScoreboardManagerNMS<Packet<?>> {

    private final PacketPlayOutScoreboardDisplayObjective displayPacket = new PacketPlayOutScoreboardDisplayObjective(1, null);
    private final PacketPlayOutScoreboardObjective removePacket = UnsafeUtilities.findPacketConstructor(PacketPlayOutScoreboardObjective.class, MethodHandles.lookup()).invoke();
    private boolean nativeAdventure, protocolSupport;

    public NMSImpl() {
        setField(getField(PacketPlayOutScoreboardDisplayObjective.class, "b"), displayPacket, objectiveName);
        setField(getField(PacketPlayOutScoreboardObjective.class, "d"), removePacket, objectiveName);
        UnsafeUtilities.UNSAFE.putInt(removePacket, UnsafeUtilities.UNSAFE.objectFieldOffset(getField(PacketPlayOutScoreboardDisplayObjective.class, "a")), 1);

        try {
            Class.forName("io.papermc.paper.adventure.PaperAdventure");
            nativeAdventure = true;
            ScoreboardLibraryLogger.logMessage("Detected native Adventure");
        } catch (ClassNotFoundException ignored) { // Imagine still using Spigot
            ScoreboardLibraryLogger.logMessage("Not using native Adventure");
        }

        if (Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null) {
            var description = ScoreboardManagerProvider.loaderPlugin().getDescription();
            if (description.getDepend().contains("ProtocolSupport")
                    || description.getSoftDepend().contains("ProtocolSupport")) {
                protocolSupport = true;
                ScoreboardLibraryLogger.logMessage("Detected ProtocolSupport");
            }
        }
    }

    public boolean protocolSupport() {
        return protocolSupport;
    }

    @Override
    public SidebarNMS<Packet<?>, ?> createSidebarNMS(Sidebar sidebar) {
        return nativeAdventure ? new PaperSidebarNMSImpl(this, sidebar) : new SidebarNMSImpl(this, sidebar);
    }

    @Override
    public void displaySidebar(Iterable<Player> players) {
        sendPacket(players, displayPacket);
    }

    @Override
    public void removeSidebar(Iterable<Player> players) {
        sendPacket(players, removePacket);
    }

    @Override
    public TeamNMS<?, ?> createTeamNMS(String teamName) {
        return nativeAdventure ? new PaperTeamNMSImpl(this, teamName) : new TeamNMSImpl(this, teamName);
    }

    @Override
    public boolean isLegacy(Player player) {
        if (!protocolSupport) return false;
        return ProtocolSupportUtil.isLegacy(player);
    }

    @Override
    public void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().b.a(packet);
    }

    public IChatBaseComponent fromAdventure(Component component, Locale locale) {
        if (nativeAdventure) return NativeAdventureUtil.fromAdventureComponent(component);

        component = GlobalTranslator.render(component, Objects.requireNonNull(locale));
        return IChatBaseComponent.ChatSerializer.a(gson().serializeToTree(component));
    }
}
