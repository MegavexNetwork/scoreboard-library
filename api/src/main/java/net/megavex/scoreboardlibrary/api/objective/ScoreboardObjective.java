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
  void value(@NotNull Component value);

  /**
   * @return The render type of the objective, default is {@link ObjectiveRenderType#INTEGER}
   */
  @NotNull ObjectiveRenderType renderType();

  /**
   * @param renderType Sets the render type of the objective
   */
  void renderType(@NotNull ObjectiveRenderType renderType);

  /**
   * Gets the score value for an entry
   *
   * @param entry Entry to get score of
   * @return The score, or 0 if score doesn't exist
   */
  int score(@NotNull String entry);

  /**
   * Sets the score for an entry
   *
   * @param entry Entry to update score of
   * @param score The new score value
   */
  void score(@NotNull String entry, int score);

  /**
   * Removes a score
   *
   * @param entry Entry to remove score from
   */
  void removeScore(@NotNull String entry);
}
