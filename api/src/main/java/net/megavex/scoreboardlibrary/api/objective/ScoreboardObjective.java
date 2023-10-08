package net.megavex.scoreboardlibrary.api.objective;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a scoreboard objective.
 */
public interface ScoreboardObjective {
  /**
   * @return The value (display name) of the objective
   */
  @NotNull Component value();

  /**
   * Sets the value (display name) of the objective
   *
   * @param value New value
   */
  @NotNull ScoreboardObjective value(@NotNull Component value);

  /**
   * @return The render type of the objective, default is {@link ObjectiveRenderType#INTEGER}
   */
  @NotNull ObjectiveRenderType renderType();

  /**
   * @param renderType Sets the render type of the objective
   */
  @NotNull ScoreboardObjective renderType(@NotNull ObjectiveRenderType renderType);

  /**
   * Gets the score value for an entry, or null if the entry has no score registered
   *
   * @param entry Entry to get score of
   * @return The score, or 0 if score doesn't exist
   */
  Integer score(@NotNull String entry);

  /**
   * Sets the score for an entry
   *
   * @param entry Entry to update score of
   * @param score The new score value
   */
  @NotNull ScoreboardObjective score(@NotNull String entry, int score);

  /**
   * Removes a score
   *
   * @param entry Entry to remove score from
   */
  @NotNull ScoreboardObjective removeScore(@NotNull String entry);
}
