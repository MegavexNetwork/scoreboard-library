package net.megavex.scoreboardlibrary.api.noop;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.Component.empty;

public class NoopScoreboardObjective implements ScoreboardObjective {
  private final Map<String, Integer> scores = new HashMap<>();
  private Component value = empty();
  private ObjectiveRenderType renderType;
  private ScoreFormat defaultScoreFormat;

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
  public @Nullable ScoreFormat defaultScoreFormat() {
    return defaultScoreFormat;
  }

  @Override
  public void defaultScoreFormat(@Nullable ScoreFormat defaultScoreFormat) {
    this.defaultScoreFormat = defaultScoreFormat;
  }

  @Override
  public @Nullable Integer score(@NotNull String entry) {
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
