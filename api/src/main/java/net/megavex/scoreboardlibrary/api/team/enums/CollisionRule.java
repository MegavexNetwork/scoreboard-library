package net.megavex.scoreboardlibrary.api.team.enums;

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

    public static CollisionRule of(byte id) {
        return values[id];
    }

    public String key() {
        return key;
    }

    public byte getId() {
        return (byte) ordinal();
    }
}
