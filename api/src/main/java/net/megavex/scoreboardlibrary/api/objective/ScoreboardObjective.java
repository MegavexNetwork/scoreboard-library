package net.megavex.scoreboardlibrary.api.objective;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardObjective {
  @NotNull Component value();

  void value(@NotNull Component value);

  @NotNull ObjectiveRenderType renderType();

  void renderType(@NotNull ObjectiveRenderType renderType);

  int score(@NotNull String entry);

  void score(@NotNull String entry, int score);
}
