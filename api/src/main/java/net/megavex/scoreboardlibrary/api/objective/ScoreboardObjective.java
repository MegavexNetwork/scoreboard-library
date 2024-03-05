package net.megavex.scoreboardlibrary.api.objective;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a scoreboard objective.
 * To get an instance of this interface, use {@link ObjectiveManager#create}.
 * Note: this interface is not thread-safe, meaning you can only use it from one thread at a time,
 * although it does not have to be the main thread.
 */
public interface ScoreboardObjective {
  /**
   * @return The value (display name) of the objective, defaults to {@link Component#empty}
   */
  @NotNull Component value();

  /**
   * Sets the value (display name) of the objective.
   *
   * @param value New value
   */
  @NotNull ScoreboardObjective value(@NotNull Component value);

  /**
   * @return The render type of the objective, defaults to {@link ObjectiveRenderType#INTEGER}
   */
  @NotNull ObjectiveRenderType renderType();

  /**
   * Sets the render type of the objective.
   *
   * @param renderType New render type
   */
  @NotNull ScoreboardObjective renderType(@NotNull ObjectiveRenderType renderType);

  /**
   * @return the default score format for all scores in this objective, defaults to null
   */
  @Nullable ScoreFormat defaultScoreFormat();

  /**
   * Sets ghe default score format of this objective.
   *
   * @param defaultScoreFormat new default score format
   * @see #defaultScoreFormat()
   */
  void defaultScoreFormat(@Nullable ScoreFormat defaultScoreFormat);

  /**
   * Gets the score value for an entry, or null if the entry has no score registered.
   *
   * @param entry Entry to get score of
   * @return The score, or null if it doesn't exist for the entry
   */
  @Nullable Integer score(@NotNull String entry);

  /**
   * Updates the score of an entry.
   *
   * @param entry Entry to update score of
   * @param score New score value
   */
  @NotNull ScoreboardObjective score(@NotNull String entry, int score);

  /**
   * Removes a score.
   *
   * @param entry Entry to remove score from
   */
  @NotNull ScoreboardObjective removeScore(@NotNull String entry);
}
