package net.megavex.scoreboardlibrary.api.noop;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import static net.kyori.adventure.text.Component.empty;

class NoopTeamInfo implements TeamInfo {
  private final NoopScoreboardTeam team;

  private final Set<String> entries = new HashSet<>();
  private Component displayName = empty(),
    prefix = empty(),
    suffix = empty();
  private boolean friendlyFire, canSeeFriendlyInvisibles;
  private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
  private CollisionRule collisionRule = CollisionRule.ALWAYS;
  private NamedTextColor playerColor = null;

  NoopTeamInfo(@NotNull NoopScoreboardTeam team) {
    this.team = team;
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
    return entries.add(entry);
  }

  @Override
  public boolean removeEntry(@NotNull String entry) {
    return entries.remove(entry);
  }

  @Override
  public @NotNull Component displayName() {
    return displayName;
  }

  @Override
  public @NotNull TeamInfo displayName(@NotNull Component displayName) {
    this.displayName = displayName;
    return this;
  }

  @Override
  public @NotNull Component prefix() {
    return prefix;
  }

  @Override
  public @NotNull TeamInfo prefix(@NotNull Component prefix) {
    this.prefix = prefix;
    return this;
  }

  @Override
  public @NotNull Component suffix() {
    return suffix;
  }

  @Override
  public @NotNull TeamInfo suffix(@NotNull Component suffix) {
    this.suffix = suffix;
    return this;
  }

  @Override
  public boolean friendlyFire() {
    return friendlyFire;
  }

  @Override
  public @NotNull TeamInfo friendlyFire(boolean friendlyFire) {
    this.friendlyFire = friendlyFire;
    return this;
  }

  @Override
  public boolean canSeeFriendlyInvisibles() {
    return canSeeFriendlyInvisibles;
  }

  @Override
  public @NotNull TeamInfo canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
    this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    return this;
  }

  @Override
  public @NotNull NameTagVisibility nameTagVisibility() {
    return nameTagVisibility;
  }

  @Override
  public @NotNull TeamInfo nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility) {
    this.nameTagVisibility = nameTagVisibility;
    return this;
  }

  @Override
  public @NotNull CollisionRule collisionRule() {
    return collisionRule;
  }

  @Override
  public @NotNull TeamInfo collisionRule(@NotNull CollisionRule collisionRule) {
    this.collisionRule = collisionRule;
    return this;
  }

  @Override
  public @Nullable NamedTextColor playerColor() {
    return playerColor;
  }

  @Override
  public @NotNull TeamInfo playerColor(@Nullable NamedTextColor playerColor) {
    this.playerColor = playerColor;
    return this;
  }
}
