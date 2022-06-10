package net.megavex.scoreboardlibrary.internal.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import org.bukkit.ChatColor;

public class GlobalLineInfo {

  private static final String[] lineColors = new String[15];

  static {
    var values = ChatColor.values();
    for (byte i = 0; i < lineColors.length; i++) {
      lineColors[i] = values[i].toString();
    }
  }

  public final byte line;
  public final TeamNMS<?, ?> bridge;
  public Component value;
  public byte objectiveScore;
  public boolean update, updateTeams, updateScore;

  public GlobalLineInfo(byte line) {
    this.line = line;
    this.bridge = ScoreboardManagerNMS.INSTANCE.createTeamNMS("_l" + line);
  }

  public String player() {
    return lineColors[line];
  }
}
