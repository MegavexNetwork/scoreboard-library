package net.megavex.scoreboardlibrary.internal.sidebar.line.locale;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

import static net.kyori.adventure.text.Component.empty;

// Implementation for new versions above 1.12.2
class LocaleLineImpl implements LocaleLine<Component> {

    private final GlobalLineInfo info;
    private final SidebarLineHandler handler;
    private final Collection<String> entries;
    private final TeamNMS.TeamInfoNMS<Component> bridge;
    private boolean update = false;

    public LocaleLineImpl(GlobalLineInfo info, SidebarLineHandler handler) {
        this.info = info;
        this.handler = handler;
        this.entries = Collections.singletonList(info.player());
        this.bridge = info.bridge.createTeamInfoNMS(this);
        bridge.updateTeamPackets(entries);
    }

    @Override
    public GlobalLineInfo info() {
        return info;
    }

    @Override
    public void value(Component renderedComponent) {
        update = true;
    }

    @Override
    public void updateTeam() {
        if (!update) {
            return;
        }

        bridge.updateTeamPackets(entries);
        if (handler.sidebar().visible()) bridge.updateTeam(handler.players(LineType.NEW));
        update = false;
    }

    @Override
    public void sendScore(Collection<Player> players) {
        handler.sidebar().sidebarBridge().score(players, info.objectiveScore, info.player());
    }

    @Override
    public void show(Collection<Player> players) {
        sendScore(players);
        bridge.createTeam(players);
    }

    @Override
    public void hide(Collection<Player> players) {
        handler.sidebar().sidebarBridge().removeLine(players, info.player());
        info.bridge.removeTeam(players);
    }

    @Override
    public Collection<String> entries() {
        return entries;
    }

    @Override
    public Component displayName() {
        return empty();
    }

    @Override
    public Component prefix() {
        return info.value;
    }

    @Override
    public Component suffix() {
        return empty();
    }
}
