package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static net.kyori.adventure.text.Component.empty;

public class TeamInfoImpl implements TeamInfo, ImmutableTeamProperties<Component> {
  private final ScoreboardTeamImpl team;
  private final TeamsPacketAdapter.TeamInfoPacketAdapter<?> packetAdapter;

  private final Set<Player> players = CollectionProvider.set(4);

  private final Set<String> entries = CollectionProvider.set(4);
  private Component displayName = empty(),
    prefix = empty(),
    suffix = empty();
  private boolean friendlyFire, canSeeFriendlyInvisibles;
  private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
  private CollisionRule collisionRule = CollisionRule.ALWAYS;
  private NamedTextColor playerColor = null;

  public TeamInfoImpl(ScoreboardTeamImpl team) {
    this.team = team;
    this.packetAdapter = team.packetAdapter().createTeamInfoAdapter(this);
  }

  @Override
  public @NotNull ScoreboardTeam team() {
    return team;
  }

  @Override
  public @NotNull Collection<String> entries() {
    return Collections.unmodifiableSet(entries);
  }

  @Override
  public boolean addEntry(@NotNull String entry) {
    if (entries.add(entry)) {
      team.teamManager().taskQueue().add(new TeamManagerTask.AddEntries(this, Set.of(entry)));
      return true;
    }

    return false;
  }

  @Override
  public boolean removeEntry(@NotNull String entry) {
    if (entries.remove(entry)) {
      team.teamManager().taskQueue().add(new TeamManagerTask.RemoveEntries(this, Set.of(entry)));
      return true;
    }

    return false;
  }

  @Override
  public @NotNull Component displayName() {
    return displayName;
  }

  @Override
  public @NotNull TeamInfo displayName(@NotNull Component displayName) {
    Preconditions.checkNotNull(displayName);

    if (!Objects.equals(this.displayName, displayName)) {
      this.displayName = displayName;
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

    if (!Objects.equals(this.prefix, prefix)) {
      this.prefix = prefix;
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

    if (!Objects.equals(this.suffix, suffix)) {
      this.suffix = suffix;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public boolean friendlyFire() {
    return friendlyFire;
  }

  @Override
  public @NotNull TeamInfo friendlyFire(boolean friendlyFire) {
    if (this.friendlyFire != friendlyFire) {
      this.friendlyFire = friendlyFire;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public boolean canSeeFriendlyInvisibles() {
    return canSeeFriendlyInvisibles;
  }

  @Override
  public @NotNull TeamInfo canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
    if (this.canSeeFriendlyInvisibles != canSeeFriendlyInvisibles) {
      this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull NameTagVisibility nameTagVisibility() {
    return nameTagVisibility;
  }

  @Override
  public @NotNull TeamInfo nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility) {
    Preconditions.checkNotNull(nameTagVisibility);

    if (!Objects.equals(this.nameTagVisibility, nameTagVisibility)) {
      this.nameTagVisibility = nameTagVisibility;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull CollisionRule collisionRule() {
    return collisionRule;
  }

  @Override
  public @NotNull TeamInfo collisionRule(@NotNull CollisionRule collisionRule) {
    Preconditions.checkNotNull(collisionRule);

    if (!Objects.equals(this.collisionRule, collisionRule)) {
      this.collisionRule = collisionRule;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @Nullable NamedTextColor playerColor() {
    return playerColor;
  }

  @Override
  public @NotNull TeamInfo playerColor(@Nullable NamedTextColor playerColor) {
    if (!Objects.equals(this.playerColor, playerColor)) {
      this.playerColor = playerColor;
      scheduleUpdate();
    }

    return this;
  }

  public @NotNull TeamsPacketAdapter.TeamInfoPacketAdapter<?> packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Set<Player> players() {
    return players;
  }

  public void updateTeamPackets() {
    packetAdapter.updateTeamPackets(entries);
  }

  private void scheduleUpdate() {
    var taskQueue = team.teamManager().taskQueue();
    if (taskQueue.peek() instanceof TeamManagerTask.UpdateTeamInfo updateTeamTask && updateTeamTask.teamInfo() == this) {
      return;
    }

    taskQueue.add(new TeamManagerTask.UpdateTeamInfo(this));
  }
}
