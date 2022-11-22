package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.nms.base.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
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
  public final TeamsPacketAdapter<?, ?> bridge;
  public Component value;
  public byte objectiveScore;
  public boolean update, updateTeams, updateScore;

  public GlobalLineInfo(AbstractSidebar sidebar, byte line) {
    this.line = line;
    this.bridge = sidebar.scoreboardLibrary().packetAdapter.createTeamNMS("_l" + line);
  }

  public String player() {
    return lineColors[line];
  }
}
