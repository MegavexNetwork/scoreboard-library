package net.megavex.scoreboardlibrary.api.objective;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
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
   * @return the value (display name) of the objective, defaults to {@link Component#empty}
   */
  @NotNull Component value();

  /**
   * Sets the value (display name) of the objective.
   *
   * @param value new value
   */
  @NotNull ScoreboardObjective value(@NotNull ComponentLike value);

  /**
   * @return the render type of the objective, defaults to {@link ObjectiveRenderType#INTEGER}
   */
  @NotNull ObjectiveRenderType renderType();

  /**
   * Sets the render type of the objective.
   *
   * @param renderType new render type
   */
  @NotNull ScoreboardObjective renderType(@NotNull ObjectiveRenderType renderType);

  /**
   * @return the default score format for all scores in this objective, defaults to null
   */
  @Nullable ScoreFormat defaultScoreFormat();

  /**
   * Sets the default score format of this objective.
   *
   * @param defaultScoreFormat new default score format
   * @see #defaultScoreFormat()
   */
  void defaultScoreFormat(@Nullable ScoreFormat defaultScoreFormat);

  /**
   * Get the score for an entry, or null if the entry has no score.
   *
   * @param entry entry to get score of
   * @return the score value, or null if it doesn't exist for the entry
   */
  @Nullable ObjectiveScore scoreInfo(@NotNull String entry);

  /**
   * Gets the score value for an entry, or null if the entry has no score.
   *
   * @param entry entry to get score of
   * @return the score value, or null if it doesn't exist for the entry
   * @deprecated use {@link #scoreInfo(String)} and {@link ObjectiveScore#value()} instead
   */
  @Deprecated
  default @Nullable Integer score(@NotNull String entry) {
    ObjectiveScore score = scoreInfo(entry);
    return score == null ? null : score.value();
  }

  /**
   * Updates the score of an entry.
   *
   * @param entry entry to update score of
   * @param score score
   */
  @NotNull ScoreboardObjective score(@NotNull String entry, ObjectiveScore score);

  /**
   * Updates the score of an entry.
   *
   * @param entry entry to update score of
   * @param scoreValue score value
   */
  default @NotNull ScoreboardObjective score(@NotNull String entry, int scoreValue) {
    return score(entry, new ObjectiveScore(scoreValue, null, null));
  }

  /**
   * Updates the score of an entry.
   *
   * @param entry entry to update score of
   * @param scoreValue score value
   * @param displayName score display name
   * @param scoreFormat score format
   */
  default @NotNull ScoreboardObjective score(
    @NotNull String entry,
    int scoreValue,
    @Nullable ComponentLike displayName,
    @Nullable ScoreFormat scoreFormat
  ) {
    return score(entry, new ObjectiveScore(scoreValue, displayName, scoreFormat));
  }

  /**
   * Updates the score of an entry.
   *
   * @param entry entry to update score of
   * @param scoreValue score value
   * @param displayName score display name
   */
  default @NotNull ScoreboardObjective score(
    @NotNull String entry,
    int scoreValue,
    @Nullable ComponentLike displayName
  ) {
    return score(entry, new ObjectiveScore(scoreValue, displayName, null));
  }

  /**
   * Updates the score of an entry.
   *
   * @param entry entry to update score of
   * @param scoreValue score value
   * @param scoreFormat score format
   */
  default @NotNull ScoreboardObjective score(
    @NotNull String entry,
    int scoreValue,
    @Nullable ScoreFormat scoreFormat
  ) {
    return score(entry, new ObjectiveScore(scoreValue, null, scoreFormat));
  }

  /**
   * Removes a score associated with an entry.
   *
   * @param entry entry to remove score of
   */
  @NotNull ScoreboardObjective removeScore(@NotNull String entry);

  /**
   * Refreshes the objective.
   */
  void refresh();
}
