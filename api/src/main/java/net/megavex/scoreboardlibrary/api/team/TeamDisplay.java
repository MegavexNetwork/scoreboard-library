package net.megavex.scoreboardlibrary.api.team;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;

@ApiStatus.NonExtendable
@NotThreadSafe
public interface TeamDisplay {
  /**
   * @return Team which is assigned to this {@link TeamDisplay}
   */
  @NotNull ScoreboardTeam team();

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
  @NotNull TeamDisplay displayName(@NotNull Component displayName);

  /**
   * @return Prefix
   */
  @NotNull Component prefix();

  /**
   * Sets the prefix
   *
   * @param prefix Prefix
   */
  @NotNull TeamDisplay prefix(@NotNull Component prefix);

  /**
   * @return Suffix
   */
  @NotNull Component suffix();

  /**
   * Sets the suffix
   *
   * @param suffix Suffix
   */
  @NotNull TeamDisplay suffix(@NotNull Component suffix);

  /**
   * @return Friendly fire rule
   */
  boolean friendlyFire();

  /**
   * Sets whether friendly fire is allowed
   *
   * @param friendlyFire whether friendly fire is allowed
   */
  @NotNull TeamDisplay friendlyFire(boolean friendlyFire);

  /**
   * @return Can see friendly invisibles rule
   */
  boolean canSeeFriendlyInvisibles();

  /**
   * Sets whether players can see friendly invisibles
   *
   * @param canSeeFriendlyInvisibles whether players can see friendly invisibles
   */
  @NotNull TeamDisplay canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

  /**
   * @return Name tag visibility rule
   */
  @NotNull NameTagVisibility nameTagVisibility();

  /**
   * Sets the {@link NameTagVisibility}
   *
   * @param nameTagVisibility Name tag visibility rule
   */
  @NotNull TeamDisplay nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility);

  /**
   * @return Collision rule
   */
  @NotNull CollisionRule collisionRule();

  /**
   * Sets the {@link CollisionRule}.
   *
   * @param collisionRule Collision rule
   */
  @NotNull TeamDisplay collisionRule(@NotNull CollisionRule collisionRule);

  /**
   * @return Player color
   */
  @Nullable NamedTextColor playerColor();

  /**
   * Sets the player color
   *
   * @param playerColor Player color
   */
  @NotNull TeamDisplay playerColor(@Nullable NamedTextColor playerColor);
}
