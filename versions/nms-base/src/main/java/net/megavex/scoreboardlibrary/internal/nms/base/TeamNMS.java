package net.megavex.scoreboardlibrary.internal.nms.base;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class TeamNMS<P, T extends ScoreboardManagerNMS<P>> {

    public static final byte LEGACY_CHARACTER_LIMIT = 16;

    public static final byte MODE_CREATE = 0,
            MODE_REMOVE = 1,
            MODE_UPDATE = 2,
            MODE_ADD_ENTRIES = 3,
            MODE_REMOVE_ENTRIES = 4;

    protected final T impl;
    protected final String teamName;

    public TeamNMS(T impl, String teamName) {
        this.impl = impl;
        this.teamName = teamName;
    }

    public abstract void removeTeam(Iterable<Player> players);

    public abstract TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties);

    public TeamInfoNMS<String> createLegacyTeamInfoNMS(ImmutableTeamProperties<String> properties) {
        throw new UnsupportedOperationException();
    }

    public abstract static class TeamInfoNMS<C> {

        protected final ImmutableTeamProperties<C> properties;

        protected TeamInfoNMS(ImmutableTeamProperties<C> properties) {
            this.properties = properties;
        }

        public void updateTeamPackets(Collection<String> entries) {
        }

        public abstract void addEntries(Collection<Player> players, Collection<String> entries);

        public abstract void removeEntries(Collection<Player> players, Collection<String> entries);

        public abstract void createTeam(Collection<Player> players);

        public abstract void updateTeam(Collection<Player> players);
    }
}
