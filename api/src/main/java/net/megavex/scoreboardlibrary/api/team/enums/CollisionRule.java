package net.megavex.scoreboardlibrary.api.team.enums;

import org.jetbrains.annotations.ApiStatus;

public enum CollisionRule {
    ALWAYS("always"),
    NEVER("never"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam");

    private static final CollisionRule[] values = values();
    private final String key;

    CollisionRule(String key) {
        this.key = key;
    }

    @ApiStatus.Internal
    public static CollisionRule of(byte id) {
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
