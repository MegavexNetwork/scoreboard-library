package net.megavex.scoreboardlibrary.internal.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import org.bukkit.ChatColor;

public class GlobalLineInfo {

    private static final String[] lineColors = new String[15];

    static {
        ChatColor[] values = ChatColor.values();
        for (byte i = 0; i < lineColors.length; i++) {
            lineColors[i] = values[i].toString();
        }
    }

    private final byte line;
    private final TeamNMS<?, ?> bridge;
    private Component value;
    private byte objectiveScore;
    private boolean update, updateTeams, updateScore;

    public GlobalLineInfo(byte line) {
        this.line = line;
        this.bridge = ScoreboardManagerNMS.INSTANCE.createTeamNMS("_l" + line);
    }

    public String player() {
        return lineColors[line];
    }

    public byte line() {
        return this.line;
    }

    public TeamNMS<?, ?> bridge() {
        return this.bridge;
    }

    public Component value() {
        return this.value;
    }

    public byte objectiveScore() {
        return this.objectiveScore;
    }

    public boolean update() {
        return update;
    }

    public boolean updateTeams() {
        return this.updateTeams;
    }

    public boolean updateScore() {
        return this.updateScore;
    }

    public void value(Component value) {
        this.value = value;
    }

    public void objectiveScore(byte objectiveScore) {
        this.objectiveScore = objectiveScore;
    }

    public void update(boolean update) {
        this.update = update;
    }

    public void updateTeams(boolean updateTeams) {
        this.updateTeams = updateTeams;
    }

    public void updateScore(boolean updateScore) {
        this.updateScore = updateScore;
    }
}
