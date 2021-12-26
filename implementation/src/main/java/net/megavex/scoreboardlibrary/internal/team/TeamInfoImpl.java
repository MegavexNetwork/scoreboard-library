package net.megavex.scoreboardlibrary.internal.team;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LegacyFormatUtil;
import org.bukkit.entity.Player;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.parseChar;

public class TeamInfoImpl implements TeamInfo, ImmutableTeamProperties<Component> {

    public final Set<Player> players = CollectionProvider.set(4);
    public final Set<String> entries = Collections.synchronizedSet(CollectionProvider.set(4));
    public List<String> addEntries = CollectionProvider.list(1);
    public List<String> removeEntries = CollectionProvider.list(1);
    public Component displayName = empty(), prefix = empty(), suffix = empty();
    public boolean allowFriendlyFire;
    public boolean canSeeFriendlyInvisibles;

    public byte nameTagVisibility = NameTagVisibility.ALWAYS.id();
    public byte collisionRule = CollisionRule.ALWAYS.getId();
    public char playerColor = '\0';

    public TeamNMS.TeamInfoNMS<Component> nms;
    boolean updateTeam, updateEntries;
    boolean autoUpdate = true;
    private ScoreboardTeamImpl team;
    private short id;

    public TeamInfoImpl() {
    }

    public TeamInfoImpl(ScoreboardTeamImpl team) {
        assign(team);
    }

    public static void syncEntries(Collection<Player> players, TeamInfoImpl old, TeamInfoImpl info) {
        if (old != null && !old.entries.isEmpty()) {
            List<String> entries = new ArrayList<>(old.entries);
            entries.removeAll(info.entries);
            if (!entries.isEmpty())
                info.nms.removeEntries(players, entries);

            entries = new ArrayList<>(info.entries);
            entries.removeAll(old.entries);
            if (!entries.isEmpty())
                info.nms.addEntries(players, entries);
        } else if (!info.entries.isEmpty()) {
            info.nms.addEntries(players, info.entries);
        }
    }

    public void assign(ScoreboardTeamImpl team) {
        if (this.team == team) {
            return;
        }

        this.team = team;
        this.id = team.idCounter++;

        team.getInfoSet().add(this);
        nms = team.nms.createTeamInfoNMS(this);
        nms.updateTeamPackets(entries);
    }

    @Override
    public ScoreboardTeamImpl team() {
        if (team == null) throw new IllegalStateException("Not assigned to a team");
        return team;
    }

    @Override
    public boolean isAssigned() {
        return team != null;
    }

    @Override
    public void unassign() {
        if (isAssigned()) {
            Preconditions.checkState(team.globalInfo() != this, "Cannot unnasign a global TeamInfo");
            team.getInfoSet().remove(this);

            TeamInfoImpl global = team.globalInfo();
            global.addPlayers(players);
            global.nms.updateTeam(players);

            syncEntries(players, this, global);

            players.clear();
            team = null;
        }
    }

    @Override
    public Set<String> entries() {
        return Collections.unmodifiableSet(entries);
    }

    @Override
    public boolean addEntry(String entry) {
        Preconditions.checkNotNull(entry, "entry");
        if (entries.add(entry)) {
            entries.add(entry);
            addEntries.add(entry);
            removeEntries.remove(entry);
            updateEntries();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEntry(String entry) {
        Preconditions.checkNotNull(entry, "entry");
        if (entries.remove(entry)) {
            entries.remove(entry);
            removeEntries.add(entry);
            addEntries.remove(entry);
            updateEntries();
            return true;
        }
        return false;
    }

    @Override
    public Component displayName() {
        return displayName;
    }

    @Override
    public void displayName(Component displayName) {
        Preconditions.checkNotNull(displayName);

        if (!Objects.equals(displayName, this.displayName)) {
            this.displayName = displayName;
            updateTeam = true;
        }
    }

    @Override
    public Component prefix() {
        return prefix;
    }

    @Override
    public void prefix(Component prefix) {
        Preconditions.checkNotNull(prefix);

        if ((!Objects.equals(prefix, this.prefix))) {
            this.prefix = prefix;
            updateTeam = true;
        }
    }

    @Override
    public Component suffix() {
        return suffix;
    }

    @Override
    public void suffix(Component suffix) {
        Preconditions.checkNotNull(suffix);

        if (!Objects.equals(suffix, this.suffix)) {
            this.suffix = suffix;
            updateTeam = true;
        }
    }

    @Override
    public boolean friendlyFire() {
        return allowFriendlyFire;
    }

    @Override
    public void friendlyFire(boolean allowFriendlyFire) {
        if (this.allowFriendlyFire != allowFriendlyFire) {
            this.allowFriendlyFire = allowFriendlyFire;
            updateTeam = true;
        }
    }

    @Override
    public void canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        if (this.canSeeFriendlyInvisibles != canSeeFriendlyInvisibles) {
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
            updateTeam = true;
        }
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return canSeeFriendlyInvisibles;
    }

    @Override
    public NameTagVisibility nameTagVisibility() {
        return NameTagVisibility.of(nameTagVisibility);
    }

    @Override
    public void nameTagVisibility(NameTagVisibility nameTagVisibility) {
        Preconditions.checkNotNull(nameTagVisibility, "nameTagVisibility");

        if (this.nameTagVisibility != nameTagVisibility.id()) {
            this.nameTagVisibility = nameTagVisibility.id();
            updateTeam = true;
        }
    }

    @Override
    public CollisionRule collisionRule() {
        return CollisionRule.of(collisionRule);
    }

    @Override
    public void collisionRule(CollisionRule collisionRule) {
        Preconditions.checkNotNull(collisionRule, "collisionRule");

        if (this.collisionRule != collisionRule.getId()) {
            this.collisionRule = collisionRule.getId();
            updateTeam = true;
        }
    }

    @Override
    public NamedTextColor playerColor() {
        if (playerColor == '\0') return null;
        return (NamedTextColor) requireNonNull(parseChar(playerColor)).color();
    }

    @Override
    public void playerColor(NamedTextColor color) {
        if (color == null) {
            playerColor = '\0';
            return;
        }

        char c = LegacyFormatUtil.getChar(color);
        if (this.playerColor != c) {
            this.playerColor = c;
            updateTeam = true;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, id);
    }

    @Override
    public boolean autoUpdate() {
        return autoUpdate;
    }

    @Override
    public void autoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    @Override
    public void update() {
        if (updateTeam) {
            nms.updateTeamPackets(entries);
            if (!players.isEmpty()) {
                nms.updateTeam(players);
            }
            updateTeam = false;
        }

        if (updateEntries) {
            if (!players.isEmpty()) {
                if (!addEntries.isEmpty()) {
                    nms.addEntries(players, addEntries);
                    addEntries = CollectionProvider.list(1);
                }
                if (!removeEntries.isEmpty()) {
                    nms.removeEntries(players, removeEntries);
                    removeEntries = CollectionProvider.list(1);
                }
            }
            updateEntries = false;
        }
    }

    public void addPlayers(Collection<Player> players) {
        update();
        this.players.addAll(players);
    }

    protected void updateEntries() {
        if (!updateEntries) {
            if (!players.isEmpty()) {
                updateEntries = true;
            } else {
                addEntries.clear();
                removeEntries.clear();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamInfoImpl teamInfo = (TeamInfoImpl) o;
        return allowFriendlyFire == teamInfo.allowFriendlyFire &&
                canSeeFriendlyInvisibles == teamInfo.canSeeFriendlyInvisibles &&
                Objects.equals(players, teamInfo.players) &&
                Objects.equals(entries, teamInfo.entries) &&
                Objects.equals(addEntries, teamInfo.addEntries) &&
                Objects.equals(removeEntries, teamInfo.removeEntries) &&
                Objects.equals(displayName, teamInfo.displayName) &&
                Objects.equals(prefix, teamInfo.prefix) &&
                Objects.equals(suffix, teamInfo.suffix) &&
                nameTagVisibility == teamInfo.nameTagVisibility &&
                Objects.equals(nms, teamInfo.nms) &&
                Objects.equals(team, teamInfo.team);
    }

    @Override
    public String toString() {
        return "TeamInfoImpl{" +
                "players=" + players +
                ", entries=" + entries +
                ", team=" + team +
                '}';
    }
}
