package net.megavex.scoreboardlibrary.implementation.player;

import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DisplayableQueue<T extends PlayerDisplayable> {
  private final UUID playerUuid;
  private final List<T> queue = CollectionProvider.list(1);

  public DisplayableQueue(@NotNull UUID playerUuid) {
    this.playerUuid = playerUuid;
  }

  public synchronized @Nullable T current() {
    if (queue.isEmpty()) {
      return null;
    } else {
      return queue.get(0);
    }
  }

  public synchronized void add(@NotNull T manager) {
    if (queue.contains(manager)) {
      throw new RuntimeException("manager already registered");
    }

    queue.add(manager);

    if (current() == manager) {
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        manager.show(player);
      }
    }
  }

  public synchronized void remove(@NotNull T manager) {
    if (!queue.remove(manager)) {
      throw new RuntimeException("manager not registered");
    }

    T newTeamManager = current();
    if (newTeamManager != null) {
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        newTeamManager.show(player);
      }
    }
  }
}
