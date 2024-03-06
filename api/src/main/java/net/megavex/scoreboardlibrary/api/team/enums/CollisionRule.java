package net.megavex.scoreboardlibrary.api.team.enums;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents all possible collision rule values of a team.
 *
 * @since Minecraft 1.9
 */
public enum CollisionRule {
  ALWAYS("always"),
  NEVER("never"),
  PUSH_OTHER_TEAMS("pushOtherTeams"),
  PUSH_OWN_TEAM("pushOwnTeam");

  private final String key;

  CollisionRule(String key) {
    this.key = key;
  }

  @ApiStatus.Internal
  public String key() {
    return this.key;
  }
}
