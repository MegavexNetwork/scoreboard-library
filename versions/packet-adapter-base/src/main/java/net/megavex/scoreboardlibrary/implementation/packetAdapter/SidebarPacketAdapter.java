package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class SidebarPacketAdapter<P, T extends ScoreboardLibraryPacketAdapter<P>> {
  protected static final int LEGACY_TITLE_CHARACTER_LIMIT = 32;

  protected static final int MODE_CREATE = 0,
    MODE_UPDATE = 2;

  private final T packetAdapter;
  private final Sidebar sidebar;

  public SidebarPacketAdapter(@NotNull T packetAdapter, @NotNull Sidebar sidebar) {
    this.packetAdapter = packetAdapter;
    this.sidebar = sidebar;
  }

  public @NotNull T packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Sidebar sidebar() {
    return sidebar;
  }

  public abstract void updateTitle(@NotNull Component displayName);

  public abstract void sendObjectivePacket(@NotNull Collection<Player> players, @NotNull ObjectivePacket type);

  public abstract void removeLine(@NotNull Collection<Player> players, @NotNull String line);

  public abstract void score(@NotNull Collection<Player> players, int score, @NotNull String line);

  public enum ObjectivePacket {
    CREATE,
    UPDATE
  }
}
