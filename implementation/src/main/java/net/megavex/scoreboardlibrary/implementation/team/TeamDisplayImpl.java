package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static net.kyori.adventure.text.Component.empty;

public class TeamDisplayImpl implements TeamDisplay, ImmutableTeamProperties<Component> {
  private final ScoreboardTeamImpl team;
  private final TeamDisplayPacketAdapter packetAdapter;
  private final Set<Player> players = CollectionProvider.set(4);
  private final Set<String> entries = new CopyOnWriteArraySet<>();
  private Component displayName = empty(),
    prefix = empty(),
    suffix = empty();
  private boolean friendlyFire, canSeeFriendlyInvisibles;
  private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
  private CollisionRule collisionRule = CollisionRule.ALWAYS;
  private NamedTextColor playerColor = null;

  public TeamDisplayImpl(@NotNull ScoreboardTeamImpl team) {
    this.team = team;
    this.packetAdapter = team.packetAdapter().createTeamDisplayAdapter(this);
    updateTeamPackets();
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
      team.teamManager().taskQueue().add(new TeamManagerTask.AddEntries(this, Collections.singleton(entry)));
      return true;
    }

    return false;
  }

  @Override
  public boolean removeEntry(@NotNull String entry) {
    if (entries.remove(entry)) {
      team.teamManager().taskQueue().add(new TeamManagerTask.RemoveEntries(this, Collections.singleton(entry)));
      return true;
    }

    return false;
  }

  @Override
  public @NotNull Component displayName() {
    return displayName;
  }

  @Override
  public @NotNull TeamDisplay displayName(@NotNull ComponentLike displayName) {
    Preconditions.checkNotNull(displayName);

    Component component = displayName.asComponent();
    if (!Objects.equals(this.displayName, component)) {
      this.displayName = component;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull Component prefix() {
    return prefix;
  }

  @Override
  public @NotNull TeamDisplay prefix(@NotNull ComponentLike prefix) {
    Preconditions.checkNotNull(prefix);

    Component component = prefix.asComponent();
    if (!Objects.equals(this.prefix, component)) {
      this.prefix = component;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public @NotNull Component suffix() {
    return suffix;
  }

  @Override
  public @NotNull TeamDisplay suffix(@NotNull ComponentLike suffix) {
    Preconditions.checkNotNull(suffix);

    Component component = suffix.asComponent();
    if (!Objects.equals(this.suffix, component)) {
      this.suffix = component;
      scheduleUpdate();
    }

    return this;
  }

  @Override
  public boolean friendlyFire() {
    return friendlyFire;
  }

  @Override
  public @NotNull TeamDisplay friendlyFire(boolean friendlyFire) {
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
  public @NotNull TeamDisplay canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
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
  public @NotNull TeamDisplay nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility) {
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
  public @NotNull TeamDisplay collisionRule(@NotNull CollisionRule collisionRule) {
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
  public @NotNull TeamDisplay playerColor(@Nullable NamedTextColor playerColor) {
    if (!Objects.equals(this.playerColor, playerColor)) {
      this.playerColor = playerColor;
      scheduleUpdate();
    }

    return this;
  }

  public @NotNull TeamDisplayPacketAdapter packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Set<Player> players() {
    return players;
  }

  public void updateTeamPackets() {
    packetAdapter.updateTeamPackets(entries);
  }

  private void scheduleUpdate() {
    Queue<TeamManagerTask> taskQueue = team.teamManager().taskQueue();
    TeamManagerTask lastTask = taskQueue.peek();
    if (lastTask instanceof TeamManagerTask.UpdateTeamDisplay && ((TeamManagerTask.UpdateTeamDisplay) lastTask).teamDisplay() == this) {
      return;
    }

    taskQueue.add(new TeamManagerTask.UpdateTeamDisplay(this));
  }
}
