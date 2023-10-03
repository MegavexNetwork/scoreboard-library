package net.megavex.scoreboardlibrary.api.objective;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface ObjectiveDisplaySlot {
  static @NotNull PlayerList playerList() {
    return PlayerList.INSTANCE;
  }

  static @NotNull Sidebar sidebar() {
    return Sidebar.INSTANCE;
  }

  static @NotNull BelowName belowName() {
    return BelowName.INSTANCE;
  }

  static @NotNull TeamSidebar teamSidebar(@NotNull NamedTextColor teamColor) {
    Preconditions.checkNotNull(teamColor);
    return new TeamSidebar(teamColor);
  }

  class PlayerList implements ObjectiveDisplaySlot {
    private static final PlayerList INSTANCE = new PlayerList();

    private PlayerList() {
    }

    @Override
    public String toString() {
      return "PlayerList";
    }
  }

  class Sidebar implements ObjectiveDisplaySlot {
    private static final Sidebar INSTANCE = new Sidebar();

    private Sidebar() {
    }

    @Override
    public String toString() {
      return "Sidebar";
    }
  }

  class BelowName implements ObjectiveDisplaySlot {
    private static final BelowName INSTANCE = new BelowName();

    private BelowName() {
    }

    @Override
    public String toString() {
      return "BelowName";
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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TeamSidebar that = (TeamSidebar) o;
      return teamColor.equals(that.teamColor);
    }

    @Override
    public int hashCode() {
      return teamColor.hashCode();
    }

    @Override
    public String toString() {
      return "TeamSidebar{teamColor=" + teamColor + "}";
    }
  }
}
