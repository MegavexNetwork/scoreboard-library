package net.megavex.scoreboardlibrary.api.noop;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NoopScoreboardObjective implements ScoreboardObjective {
  private final Map<String, Integer> scores = new HashMap<>();
  private Component value;
  private ObjectiveRenderType renderType;

  @Override
  public @NotNull Component value() {
    return value;
  }

  @Override
  public void value(@NotNull Component value) {
    this.value = value;
  }

  @Override
  public @NotNull ObjectiveRenderType renderType() {
    return renderType;
  }

  @Override
  public void renderType(@NotNull ObjectiveRenderType renderType) {
    this.renderType = renderType;
  }

  @Override
  public int score(@NotNull String entry) {
    return scores.get(entry);
  }

  @Override
  public void score(@NotNull String entry, int score) {
    scores.put(entry, score);
  }

  @Override
  public void removeScore(@NotNull String entry) {
    scores.remove(entry);
  }
}
