package net.megavex.scoreboardlibrary.api.team.enums;

import org.jetbrains.annotations.ApiStatus;

public enum NameTagVisibility {
  ALWAYS("always"),
  NEVER("never"),
  HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
  HIDE_FOR_OWN_TEAM("hideForOwnTeam");

  private static final NameTagVisibility[] values = values();
  private final String key;

  NameTagVisibility(String key) {
    this.key = key;
  }

  @ApiStatus.Internal
  public static NameTagVisibility of(byte id) {
    return values[id];
  }

  @ApiStatus.Internal
  public byte id() {
    return (byte) ordinal();
  }

  @ApiStatus.Internal
  public String key() {
    return this.key;
  }
}
