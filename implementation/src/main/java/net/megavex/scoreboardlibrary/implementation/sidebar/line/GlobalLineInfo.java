package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalLineInfo {
  private static final String[] lineColors = new String[15];

  static {
    var values = ChatColor.values();
    for (int i = 0; i < lineColors.length; i++) {
      lineColors[i] = values[i].toString();
    }
  }

  private final int line;
  private final TeamsPacketAdapter<?, ?> packetAdapter;
  private Component value;
  private int objectiveScore;
  private boolean updateScore;

  public GlobalLineInfo(@NotNull AbstractSidebar sidebar, int line) {
    this.line = line;
    this.packetAdapter = sidebar.scoreboardLibrary().packetAdapter.createTeamPacketAdapter("_l" + line);
  }

  public @NotNull String player() {
    return lineColors[line];
  }

  public int line() {
    return line;
  }

  public @NotNull TeamsPacketAdapter<?, ?> bridge() {
    return packetAdapter;
  }

  public @Nullable Component value() {
    return value;
  }

  public void value(@Nullable Component value) {
    this.value = value;
  }

  public int objectiveScore() {
    return objectiveScore;
  }

  public void objectiveScore(int objectiveScore) {
    this.objectiveScore = objectiveScore;
  }

  public boolean updateScore() {
    return updateScore;
  }

  public void updateScore(boolean updateScore) {
    this.updateScore = updateScore;
  }
}
