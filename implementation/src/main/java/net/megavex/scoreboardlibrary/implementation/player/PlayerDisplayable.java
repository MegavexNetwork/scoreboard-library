package net.megavex.scoreboardlibrary.implementation.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerDisplayable {
  void show(@NotNull Player player);
}
