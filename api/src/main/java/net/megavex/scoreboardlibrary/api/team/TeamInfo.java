package net.megavex.scoreboardlibrary.api.team;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TeamInfo {
  /**
   * Creates an empty {@link TeamInfo}
   *
   * @return TeamInfo
   */
  static @NotNull TeamInfo teamInfo() {
    return teamInfo(null);
  }

  /**
   * Creates a {@link TeamInfo} with specified entries
   *
   * @param entries Entries the TeamInfo should have
   * @return TeamInfo
   */
  static @NotNull TeamInfo teamInfo(@Nullable Collection<String> entries) {
    return ScoreboardManagerProvider.instance().teamInfo(entries);
  }

  // Main

  /**
   * @return Team which is assigned to this {@link TeamInfo}
   * @throws IllegalStateException If not assigned
   */
  @NotNull ScoreboardTeam team();

  /**
   * Returns whether this TeamInfo is assigned to a Team
   *
   * @return If assigned
   */
  boolean assigned();

  /**
   * Unassigns TeamInfo info from the currently assigned Team
   */
  void unassign();

  /**
   * Returns the name of the assigned Team
   *
   * @return Name of Team
   */
  default @Nullable String name() {
    return !assigned() ? null:team().name();
  }

  // Entries

  @NotNull Collection<String> entries();

  /**
   * Adds an entry
   *
   * @param entry Entry to add
   * @return Whether the entry was added
   */
  boolean addEntry(@NotNull String entry);

  /**
   * Removes an entry
   *
   * @param entry Entry to remove
   * @return Whether the entry was removed
   */
  boolean removeEntry(@NotNull String entry);

  // Properties

  /**
   * @return Display name
   */
  @NotNull Component displayName();

  /**
   * Sets the display name
   *
   * @param displayName Display name
   */
  @NotNull TeamInfo displayName(@NotNull Component displayName);

  /**
   * @return Prefix
   */
  @NotNull Component prefix();

  /**
   * Sets the prefix
   *
   * @param prefix Prefix
   */
  @NotNull TeamInfo prefix(@NotNull Component prefix);

  /**
   * @return Suffix
   */
  @NotNull Component suffix();

  /**
   * Sets the suffix
   *
   * @param suffix Suffix
   */
  @NotNull TeamInfo suffix(@NotNull Component suffix);

  /**
   * @return Friendly fire rule
   */
  boolean friendlyFire();

  /**
   * Sets whether friendly fire is allowed
   *
   * @param allowFriendlyFire whether friendly fire is allowed
   */
  @NotNull TeamInfo friendlyFire(boolean allowFriendlyFire);

  /**
   * @return Can see friendly invisibles rule
   */
  default boolean canSeeFriendlyInvisibles() {
    return false;
  }

  /**
   * Sets whether players can see friendly invisibles
   *
   * @param canSeeFriendlyInvisibles whether players can see friendly invisibles
   */
  @NotNull TeamInfo canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

  /**
   * @return Name tag visibility rule
   */
  @NotNull NameTagVisibility nameTagVisibility();

  /**
   * Sets the {@link NameTagVisibility}
   *
   * @param nameTagVisibility Name tag visibility rule
   */
  @NotNull TeamInfo nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility);

  /**
   * @return Collision rule
   */
  @NotNull CollisionRule collisionRule();

  /**
   * Sets the {@link CollisionRule}.
   * Note that this rule will not work on 1.8.
   *
   * @param collisionRule Collision rule
   */
  @NotNull TeamInfo collisionRule(@NotNull CollisionRule collisionRule);

  /**
   * @return Player color
   */
  @Nullable NamedTextColor playerColor();

  /**
   * Sets the player color
   *
   * @param playerColor Player color
   */
  @NotNull TeamInfo playerColor(@Nullable NamedTextColor playerColor);
}
