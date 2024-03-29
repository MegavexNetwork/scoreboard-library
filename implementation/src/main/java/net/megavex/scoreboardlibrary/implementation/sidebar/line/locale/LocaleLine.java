package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface LocaleLine {
  @NotNull GlobalLineInfo info();

  void value(@NotNull Component renderedComponent);

  void updateTeam();

  default void resetOldPlayer() {
  }

  void sendScore(@NotNull Collection<Player> players);

  void show(@NotNull Collection<Player> players);

  void hide(@NotNull Collection<Player> players);
}
