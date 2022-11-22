package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.nms.base.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.parseChar;

public class TeamInfoImpl implements TeamInfo, ImmutableTeamProperties<Component> {
  public final Set<Player> players = Collections.synchronizedSet(CollectionProvider.set(4));
  public final Set<String> entries = Collections.synchronizedSet(CollectionProvider.set(4));

  private final AtomicBoolean updateTeam = new AtomicBoolean();
  public Component displayName = empty(),
    prefix = empty(),
    suffix = empty();
  public boolean allowFriendlyFire, canSeeFriendlyInvisibles;
  public byte nameTagVisibility = NameTagVisibility.ALWAYS.id(),
    collisionRule = CollisionRule.ALWAYS.id();
  public char playerColor = '\0';

  public TeamsPacketAdapter.TeamInfoNMS<Component> nms;
  private ScoreboardTeamImpl team;
  private List<String> addEntries, removeEntries;
  private short id;
  private volatile boolean updateEntries;

  public TeamInfoImpl() {
  }

  public static void syncEntries(Collection<Player> players, TeamInfoImpl old, TeamInfoImpl info) {
    if (old != null && !old.entries.isEmpty()) {
      var entries = new ArrayList<>(old.entries);
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
    if (this.team == team) return;

    this.team = team;
    this.id = team.idCounter++;

    team.teamInfos().add(this);
    nms = team.packetAdapter.createTeamInfoNMS(this, team.teamManager.componentTranslator());
    nms.updateTeamPackets(entries);
  }

  @Override
  public @NotNull ScoreboardTeamImpl team() {
    if (team == null) throw new IllegalStateException("Not assigned to a team");
    return team;
  }

  @Override
  public boolean isAssigned() {
    return team != null;
  }

  @Override
  public synchronized void unassign() {
    if (!isAssigned()) return;

    Preconditions.checkState(team.globalInfo() != this, "Cannot unnasign a global TeamInfo");
    team.teamInfos().remove(this);

    var global = team.globalInfo();
    global.addPlayers(players);
    global.nms.updateTeam(players);

    syncEntries(players, this, global);

    players.clear();
    team = null;
  }

  @Override
  public @NotNull Collection<String> entries() {
    return Collections.unmodifiableSet(entries);
  }

  @Override
  public boolean addEntry(@NotNull String entry) {
    Preconditions.checkNotNull(entry);

    synchronized (entries) {
      if (entries.add(entry)) {
        entries.add(entry);
        if (addEntries == null) addEntries = CollectionProvider.list(1);
        addEntries.add(entry);
        if (removeEntries != null) removeEntries.remove(entry);
        updateEntries();
        return true;
      }
      return false;
    }
  }

  @Override
  public boolean removeEntry(@NotNull String entry) {
    Preconditions.checkNotNull(entry);

    synchronized (entries) {
      if (entries.remove(entry)) {
        entries.remove(entry);
        if (removeEntries == null) removeEntries = CollectionProvider.list(1);
        removeEntries.add(entry);
        if (addEntries != null) addEntries.remove(entry);
        updateEntries();
        return true;
      }
      return false;
    }
  }

  @Override
  public @NotNull Component displayName() {
    return displayName;
  }

  @Override
  public @NotNull TeamInfo displayName(@NotNull Component displayName) {
    Preconditions.checkNotNull(displayName);

    if (!Objects.equals(displayName, this.displayName)) {
      this.displayName = displayName;
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull Component prefix() {
    return prefix;
  }

  @Override
  public @NotNull TeamInfo prefix(@NotNull Component prefix) {
    Preconditions.checkNotNull(prefix);

    if ((!Objects.equals(prefix, this.prefix))) {
      this.prefix = prefix;
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull Component suffix() {
    return suffix;
  }

  @Override
  public @NotNull TeamInfo suffix(@NotNull Component suffix) {
    Preconditions.checkNotNull(suffix);

    if (!Objects.equals(suffix, this.suffix)) {
      this.suffix = suffix;
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public boolean friendlyFire() {
    return allowFriendlyFire;
  }

  @Override
  public @NotNull TeamInfo friendlyFire(boolean allowFriendlyFire) {
    if (this.allowFriendlyFire != allowFriendlyFire) {
      this.allowFriendlyFire = allowFriendlyFire;
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull TeamInfo canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
    if (this.canSeeFriendlyInvisibles != canSeeFriendlyInvisibles) {
      this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public boolean canSeeFriendlyInvisibles() {
    return canSeeFriendlyInvisibles;
  }

  @Override
  public @NotNull NameTagVisibility nameTagVisibility() {
    return NameTagVisibility.of(nameTagVisibility);
  }

  @Override
  public @NotNull TeamInfo nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility) {
    Preconditions.checkNotNull(nameTagVisibility);

    if (this.nameTagVisibility != nameTagVisibility.id()) {
      this.nameTagVisibility = nameTagVisibility.id();
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull CollisionRule collisionRule() {
    return CollisionRule.of(collisionRule);
  }

  @Override
  public @NotNull TeamInfo collisionRule(@NotNull CollisionRule collisionRule) {
    Preconditions.checkNotNull(collisionRule);

    if (this.collisionRule != collisionRule.id()) {
      this.collisionRule = collisionRule.id();
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public NamedTextColor playerColor() {
    if (playerColor == '\0') return null;
    return (NamedTextColor) requireNonNull(parseChar(playerColor)).color();
  }

  @Override
  public @NotNull TeamInfo playerColor(NamedTextColor color) {
    if (color == null) {
      playerColor = '\0';
      return this;
    }

    var c = LegacyFormatUtil.getChar(color);
    if (this.playerColor != c) {
      this.playerColor = c;
      updateTeam.set(true);
      scheduleUpdate();
    }

    return this;
  }

  public void scheduleUpdate() {
    team.teamManager.update.set(true);
  }

  public void update() {
    if (updateTeam.getAndSet(false)) {
      nms.updateTeamPackets(entries);
      if (!players.isEmpty()) {
        nms.updateTeam(players);
      }
    }

    if (updateEntries) {
      synchronized (entries) {
        if (!players.isEmpty()) {
          if (addEntries != null && !addEntries.isEmpty()) {
            nms.addEntries(players, addEntries);
            addEntries.clear();
          }
          if (removeEntries != null && !removeEntries.isEmpty()) {
            nms.removeEntries(players, removeEntries);
            removeEntries.clear();
          }
        }
        updateEntries = false;
      }
    }
  }

  public void addPlayers(Collection<Player> players) {
    this.players.addAll(players);
  }

  private void updateEntries() {
    if (updateEntries) return;

    if (!players.isEmpty()) {
      updateEntries = true;
      scheduleUpdate();
    } else {
      if (addEntries != null) addEntries.clear();
      if (removeEntries != null) removeEntries.clear();
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(team, id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public String toString() {
    return "TeamInfo{" +
      "players=" + players +
      ", entries=" + entries +
      ", team=" + team +
      '}';
  }
}
