package net.megavex.scoreboardlibrary.implementation.team;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public sealed class TeamManagerTask {
  public static final class Close extends TeamManagerTask {
    public static final Close INSTANCE = new Close();

    private Close() {
    }
  }

  public static final class AddPlayer extends TeamManagerTask {
    private final Player player;

    public AddPlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class RemovePlayer extends TeamManagerTask {
    private final Player player;

    public RemovePlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class AddTeam extends TeamManagerTask {
    private final ScoreboardTeamImpl team;

    public AddTeam(@NotNull ScoreboardTeamImpl team) {
      this.team = team;
    }

    public @NotNull ScoreboardTeamImpl team() {
      return team;
    }
  }

  public static final class UpdateTeamInfo extends TeamManagerTask {
    private final TeamInfoImpl teamInfo;

    public UpdateTeamInfo(@NotNull TeamInfoImpl teamInfo) {
      this.teamInfo = teamInfo;
    }

    public @NotNull TeamInfoImpl teamInfo() {
      return teamInfo;
    }
  }

  public static final class AddEntries extends TeamManagerTask {
    private final TeamInfoImpl teamInfo;
    private final Collection<String> entry;

    public AddEntries(@NotNull TeamInfoImpl teamInfo, @NotNull Collection<String> entries) {
      this.teamInfo = teamInfo;
      this.entry = entries;
    }

    public @NotNull TeamInfoImpl teamInfo() {
      return teamInfo;
    }

    public @NotNull Collection<String> entries() {
      return entry;
    }
  }

  public static final class RemoveEntries extends TeamManagerTask {
    private final TeamInfoImpl teamInfo;
    private final Collection<String> entry;

    public RemoveEntries(@NotNull TeamInfoImpl teamInfo, @NotNull Collection<String> entries) {
      this.teamInfo = teamInfo;
      this.entry = entries;
    }

    public @NotNull TeamInfoImpl teamInfo() {
      return teamInfo;
    }

    public @NotNull Collection<String> entries() {
      return entry;
    }
  }

  public static final class ChangeTeamInfo extends TeamManagerTask {
    private final Player player;
    private final ScoreboardTeamImpl team;
    private final TeamInfoImpl oldTeamInfo;
    private final TeamInfoImpl newTeamInfo;

    public ChangeTeamInfo(@NotNull Player player, @NotNull ScoreboardTeamImpl team, @NotNull TeamInfoImpl oldTeamInfo, @NotNull TeamInfoImpl newTeamInfo) {
      this.player = player;
      this.team = team;
      this.oldTeamInfo = oldTeamInfo;
      this.newTeamInfo = newTeamInfo;
    }

    public @NotNull Player player() {
      return player;
    }

    public @NotNull ScoreboardTeamImpl team() {
      return team;
    }

    public @NotNull TeamInfoImpl oldTeamInfo() {
      return oldTeamInfo;
    }

    public @NotNull TeamInfoImpl newTeamInfo() {
      return newTeamInfo;
    }
  }
}
