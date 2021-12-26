package net.megavex.scoreboardlibrary.internal.nms.base;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LocaleUtilities;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public abstract class ScoreboardManagerNMS<P> {

    public static ScoreboardManagerNMS<?> INSTANCE;
    public final String objectiveName = "_s" + RandomStringUtils.randomAlphanumeric(5);

    public ScoreboardManagerNMS() {
        Preconditions.checkState(INSTANCE == null);
        INSTANCE = this;
    }

    public static <P> void sendLocaleDependantPackets(
            Locale specificLocale,
            ScoreboardManagerNMS<P> nms,
            Collection<Player> players,
            Function<Locale, P> packetFunction
    ) {
        if (players.isEmpty()) return;

        if (specificLocale != null) {
            P packet = packetFunction.apply(specificLocale);
            nms.sendPacket(players, packet);
        } else if (players.size() == 1) {
            Player player = players.iterator().next();
            P packet = packetFunction.apply(LocaleUtilities.getLocaleOfPlayer(player));
            nms.sendPacket(player, packet);
        } else {
            Map<Locale, P> map = CollectionProvider.map(1);
            for (Player player : players) {
                Locale locale = LocaleUtilities.getLocaleOfPlayer(player);
                P packet = map.get(locale);
                if (packet == null) {
                    map.put(locale, packetFunction.apply(locale));
                }

                nms.sendPacket(player, packet);
            }
        }
    }

    // Sidebar

    public abstract SidebarNMS<P, ?> createSidebarNMS(Sidebar sidebar);

    public abstract void displaySidebar(Iterable<Player> players);

    public abstract void removeSidebar(Iterable<Player> players);

    // Team

    public abstract TeamNMS<?, ?> createTeamNMS(String teamName);

    public abstract boolean isLegacy(Player player);

    // Packet

    public abstract void sendPacket(Player players, P packet);

    public final void sendPacket(Iterable<Player> players, P packet) {
        for (Player player : players) {
            sendPacket(player, packet);
        }
    }
}
