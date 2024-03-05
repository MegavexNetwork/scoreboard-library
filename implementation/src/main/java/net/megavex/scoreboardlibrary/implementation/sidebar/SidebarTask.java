package net.megavex.scoreboardlibrary.implementation.sidebar;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SidebarTask {
  private SidebarTask() {
  }

  public static final class Close extends SidebarTask {
    public static final Close INSTANCE = new Close();

    private Close() {
    }
  }

  public static final class AddPlayer extends SidebarTask {
    private final Player player;

    public AddPlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class RemovePlayer extends SidebarTask {
    private final Player player;

    public RemovePlayer(@NotNull Player players) {
      this.player = players;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class ReloadPlayer extends SidebarTask {
    private final Player player;

    public ReloadPlayer(@NotNull Player players) {
      this.player = players;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class UpdateLine extends SidebarTask {
    private final int line;
    private final boolean updateValue, updateScore;

    public UpdateLine(int line, boolean updateValue, boolean updateScore) {
      this.line = line;
      this.updateValue = updateValue;
      this.updateScore = updateScore;
    }

    public int line() {
      return line;
    }

    public boolean updateValue() {
      return updateValue;
    }

    public boolean updateScore() {
      return updateScore;
    }
  }

  public static final class UpdateScores extends SidebarTask {
    public static final UpdateScores INSTANCE = new UpdateScores();

    private UpdateScores() {
    }
  }

  public static final class UpdateTitle extends SidebarTask {
    public static final UpdateTitle INSTANCE = new UpdateTitle();

    private UpdateTitle() {
    }
  }
}
