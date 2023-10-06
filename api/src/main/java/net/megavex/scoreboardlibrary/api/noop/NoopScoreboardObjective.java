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
  public @NotNull ScoreboardObjective value(@NotNull Component value) {
    this.value = value;
    return this;
  }

  @Override
  public @NotNull ObjectiveRenderType renderType() {
    return renderType;
  }

  @Override
  public @NotNull ScoreboardObjective renderType(@NotNull ObjectiveRenderType renderType) {
    this.renderType = renderType;
    return this;
  }

  @Override
  public Integer score(@NotNull String entry) {
    return scores.get(entry);
  }

  @Override
  public @NotNull ScoreboardObjective score(@NotNull String entry, int score) {
    scores.put(entry, score);
    return this;
  }

  @Override
  public @NotNull ScoreboardObjective removeScore(@NotNull String entry) {
    scores.remove(entry);
    return this;
  }
}
