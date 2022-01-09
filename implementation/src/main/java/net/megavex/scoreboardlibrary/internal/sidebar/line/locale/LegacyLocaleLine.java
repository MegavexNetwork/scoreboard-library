package net.megavex.scoreboardlibrary.internal.sidebar.line.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

class LegacyLocaleLine implements LocaleLine<String> {

    private final GlobalLineInfo info;
    private final SidebarLineHandler handler;
    private final TeamNMS.TeamInfoNMS<String> bridge;
    private String player, oldPlayer;
    private String prefix, suffix;
    private String currentValue;
    private boolean update = false;

    public LegacyLocaleLine(GlobalLineInfo info, SidebarLineHandler handler) {
        this.info = info;
        this.handler = handler;
        this.player = info.player();
        this.bridge = info.bridge.createLegacyTeamInfoNMS(this);
        bridge.updateTeamPackets(entries());
    }

    public GlobalLineInfo info() {
        return info;
    }

    @Override
    public String displayName() {
        return "";
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public String suffix() {
        return suffix;
    }

    @Override
    public Collection<String> entries() {
        return Collections.singleton(player);
    }

    @Override
    public void value(Component renderedComponent) {
        String legacyValue = LegacyComponentSerializer.legacySection().serialize(renderedComponent);

        oldPlayer = player;

        if (legacyValue.length() <= 16) {
            this.prefix = legacyValue;
            this.suffix = "";

            if (this.currentValue != null && this.currentValue.length() > 32) {
                this.player = info.player();
            }
        } else {
            boolean color = legacyValue.charAt(15) == ChatColor.COLOR_CHAR;

            int prefixEnd = color ? 15 : 16;
            this.prefix = legacyValue.substring(0, prefixEnd);

            this.player = info.player() + ChatColor.RESET
                    + ChatColor.getLastColors(prefix +
                    ChatColor.COLOR_CHAR + (color ? legacyValue.charAt(16) : ""));

            int playerEnd = prefixEnd;
            if (legacyValue.length() > 32) {
                int remaining = 16 - player.length();
                assert remaining > 0;

                playerEnd += remaining;
                player += legacyValue.substring(prefixEnd, playerEnd);
            }

            this.suffix = legacyValue.substring(playerEnd + (color ? 2 : 0));
            if (suffix.length() > 16) {
                String newSuffix = suffix.substring(0, 16);
                if (newSuffix.endsWith(String.valueOf(ChatColor.COLOR_CHAR)) &&
                        ChatColor.getByChar(suffix.charAt(16)) != null) {
                    newSuffix = newSuffix.substring(0, 15);
                }

                suffix = newSuffix;
            }
        }

        currentValue = legacyValue;
        update = true;

        if (oldPlayer.equals(player)) {
            oldPlayer = null;
        }
    }

    @Override
    public void updateTeam() {
        if (!update) {
            return;
        }

        Set<Player> players = handler.players(LineType.LEGACY);
        if (oldPlayer != null) {
            bridge.removeEntries(players, Collections.singleton(oldPlayer));
            handler.sidebar().sidebarBridge().removeLine(players, oldPlayer);
            oldPlayer = null;
        }

        Collection<String> entries = entries();
        bridge.updateTeamPackets(entries);
        bridge.addEntries(players, entries);
        bridge.updateTeam(players);

        update = false;
    }

    @Override
    public void sendScore(Collection<Player> players) {
        if (oldPlayer != null) {
            handler.sidebar().sidebarBridge().removeLine(players, oldPlayer);
        }

        handler.sidebar().sidebarBridge().score(players, info.objectiveScore, player);
    }

    @Override
    public void show(Collection<Player> players) {
        sendScore(players);
        bridge.createTeam(players);
    }

    @Override
    public void hide(Collection<Player> players) {
        if (oldPlayer != null) {
            handler.sidebar().sidebarBridge().removeLine(players, oldPlayer);
        }

        handler.sidebar().sidebarBridge().removeLine(players, player);
        info.bridge.removeTeam(players);
    }
}

