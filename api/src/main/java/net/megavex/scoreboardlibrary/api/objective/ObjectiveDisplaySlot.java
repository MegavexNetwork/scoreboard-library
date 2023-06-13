package net.megavex.scoreboardlibrary.api.objective;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface ObjectiveDisplaySlot {
  PlayerList PLAYER_LIST = new PlayerList();
  Sidebar SIDEBAR = new Sidebar();
  BelowName BELOW_NAME = new BelowName();

  static @NotNull TeamSidebar teamSidebar(@NotNull NamedTextColor teamColor) {
    Preconditions.checkNotNull(teamColor);
    return new TeamSidebar(teamColor);
  }

  class PlayerList implements ObjectiveDisplaySlot {
    private PlayerList() {
    }
  }

  class Sidebar implements ObjectiveDisplaySlot {
    private Sidebar() {
    }
  }

  class BelowName implements ObjectiveDisplaySlot {
    private BelowName() {
    }
  }

  class TeamSidebar implements ObjectiveDisplaySlot {
    private final NamedTextColor teamColor;

    private TeamSidebar(@NotNull NamedTextColor teamColor) {
      this.teamColor = teamColor;
    }

    public @NotNull NamedTextColor teamColor() {
      return teamColor;
    }
  }
}
