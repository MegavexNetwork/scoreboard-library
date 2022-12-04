package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public sealed class SidebarTask {
  public static final class Close extends SidebarTask {
    public static final Close INSTANCE = new Close();

    private Close() {
    }
  }

  public static final class AddPlayers extends SidebarTask {
    private final Collection<Player> players;

    public AddPlayers(@NotNull Collection<Player> players) {
      this.players = players;
    }

    public @NotNull Collection<Player> players() {
      return players;
    }
  }

  public static final class RemovePlayers extends SidebarTask {
    private final Collection<Player> players;

    public RemovePlayers(@NotNull Collection<Player> players) {
      this.players = players;
    }

    public @NotNull Collection<Player> players() {
      return players;
    }
  }

  public static final class UpdateLine extends SidebarTask {
    private final int line;

    public UpdateLine(int line) {
      this.line = line;
    }

    public int line() {
      return line;
    }
  }

  public static final class UpdateScores extends SidebarTask {
    public static final UpdateScores INSTANCE = new UpdateScores();

    private UpdateScores() {
    }
  }

  public static final class UpdateTitle extends SidebarTask {

    public static final UpdateScores INSTANCE = new UpdateScores();
    private UpdateTitle() {
    }

  }
}
