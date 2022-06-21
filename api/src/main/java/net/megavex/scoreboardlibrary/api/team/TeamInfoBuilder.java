package net.megavex.scoreboardlibrary.api.team;

import java.util.Collection;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;

/**
 * Builder for {@link TeamInfo}s
 */
public class TeamInfoBuilder {
  private Component displayName, prefix, suffix;
  private boolean allowFriendlyFire, canSeeFriendlyInvisibles;
  private NameTagVisibility nameTagVisibility;
  private CollisionRule collisionRule;
  private NamedTextColor playerColor;
  private Collection<String> entries;

  public TeamInfo build() {
    var teamInfo = ScoreboardManagerProvider.instance().teamInfo(entries);
    teamInfo.displayName(displayName);
    teamInfo.prefix(prefix);
    teamInfo.suffix(suffix);
    teamInfo.friendlyFire(allowFriendlyFire);
    teamInfo.canSeeFriendlyInvisibles(canSeeFriendlyInvisibles);
    teamInfo.nameTagVisibility(nameTagVisibility == null ? NameTagVisibility.ALWAYS:nameTagVisibility);
    teamInfo.collisionRule(collisionRule == null ? CollisionRule.ALWAYS:collisionRule);
    teamInfo.playerColor(playerColor);
    return teamInfo;
  }

  public TeamInfoBuilder entries(Collection<String> entries) {
    this.entries = entries;
    return this;
  }

  public TeamInfoBuilder addEntry(String entry) {
    Objects.requireNonNull(entries).add(entry);
    return this;
  }

  public TeamInfoBuilder displayName(Component displayName) {
    this.displayName = displayName;
    return this;
  }

  public TeamInfoBuilder prefix(Component prefix) {
    this.prefix = prefix;
    return this;
  }

  public TeamInfoBuilder suffix(Component suffix) {
    this.suffix = suffix;
    return this;
  }

  public TeamInfoBuilder allowFriendlyFire(boolean allowFriendlyFire) {
    this.allowFriendlyFire = allowFriendlyFire;
    return this;
  }

  public TeamInfoBuilder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
    this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    return this;
  }

  public TeamInfoBuilder nameTagVisibility(NameTagVisibility nameTagVisibility) {
    this.nameTagVisibility = nameTagVisibility;
    return this;
  }

  public TeamInfoBuilder collisionRule(CollisionRule collisionRule) {
    this.collisionRule = collisionRule;
    return this;
  }

  public TeamInfoBuilder playerColor(NamedTextColor playerColor) {
    this.playerColor = playerColor;
    return this;
  }
}
