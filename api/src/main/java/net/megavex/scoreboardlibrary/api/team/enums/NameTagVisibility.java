package net.megavex.scoreboardlibrary.api.team.enums;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents all possible name tag visibility rule values of a team.
 */
public enum NameTagVisibility {
  ALWAYS("always"),
  NEVER("never"),
  HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
  HIDE_FOR_OWN_TEAM("hideForOwnTeam");

  private final String key;

  NameTagVisibility(String key) {
    this.key = key;
  }

  @ApiStatus.Internal
  public String key() {
    return this.key;
  }
}
