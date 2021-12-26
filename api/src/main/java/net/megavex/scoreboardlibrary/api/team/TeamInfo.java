package net.megavex.scoreboardlibrary.api.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface TeamInfo {

    /**
     * Creates an empty {@link TeamInfo}
     *
     * @return TeamInfo
     */
    static TeamInfo teamInfo() {
        return teamInfo(null);
    }

    /**
     * Creates a {@link TeamInfo} with specified entries
     *
     * @param entries Entries the TeamInfo should have
     * @return TeamInfo
     */
    static TeamInfo teamInfo(@Nullable Collection<String> entries) {
        return ScoreboardManagerProvider.instance().teamInfo(entries);
    }

    // Main

    /**
     * Gets the Team which is assigned to this TeamInfo
     *
     * @return Assigned Team
     * @throws IllegalStateException If not assigned
     */
    ScoreboardTeam team();

    /**
     * Returns whether this TeamInfo is assigned to a Team
     *
     * @return If assigned
     */
    boolean isAssigned();

    /**
     * Unassigns TeamInfo info from the currently assigned Team
     */
    void unassign();

    /**
     * Returns the name of the assigned Team
     *
     * @return Name of Team
     */
    @Nullable
    default String name() {
        return team() == null ? null : team().name();
    }

    // Update

    boolean autoUpdate();

    void autoUpdate(boolean autoUpdate);

    void update();

    // Entries

    /**
     * Adds an entry
     *
     * @param entry Entry to add
     * @return Whether the entry was added
     */
    boolean addEntry(String entry);

    /**
     * Removes an entry
     *
     * @param entry Entry to remove
     * @return Whether the entry was removed
     */
    boolean removeEntry(String entry);

    // Properties

    Component displayName();

    /**
     * Sets the display name
     *
     * @param displayName Display name
     */
    void displayName(Component displayName);

    Component prefix();

    /**
     * Sets the prefix
     *
     * @param prefix Prefix
     */
    void prefix(Component prefix);

    Component suffix();

    /**
     * Sets the suffix
     *
     * @param suffix Suffix
     */
    void suffix(Component suffix);

    default boolean friendlyFire() {
        return false;
    }

    /**
     * Sets whether friendly fire is allowed
     *
     * @param allowFriendlyFire whether friendly fire is allowed
     */
    void friendlyFire(boolean allowFriendlyFire);

    default boolean canSeeFriendlyInvisibles() {
        return false;
    }

    /**
     * Sets whether players can see friendly invisibles
     *
     * @param canSeeFriendlyInvisibles whether players can see friendly invisibles
     */
    void canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

    default NameTagVisibility nameTagVisibility() {
        return NameTagVisibility.ALWAYS;
    }

    /**
     * Sets the {@link NameTagVisibility}
     *
     * @param nameTagVisibility Name tag visibility rule
     */
    void nameTagVisibility(NameTagVisibility nameTagVisibility);

    /**
     * Gets the {@link CollisionRule}. This will be ignored on 1.8 since in this version teams don't support this
     *
     * @return Collision rule
     */
    default CollisionRule collisionRule() {
        return CollisionRule.ALWAYS;
    }

    /**
     * Sets the {@link CollisionRule}
     *
     * @param collisionRule Collision rule
     */
    void collisionRule(CollisionRule collisionRule);

    /**
     * Gets the {@link NamedTextColor} of the player. This will be ignored on 1.8 since in this version teams don't support this
     *
     * @return Color
     */
    @Nullable
    default NamedTextColor playerColor() {
        return null;
    }

    /**
     * Sets the {@link NamedTextColor}
     *
     * @param color Color
     */
    void playerColor(NamedTextColor color);

    // Options

    default void unpackOptions(int options) {
        friendlyFire((options & 1) > 0);
        canSeeFriendlyInvisibles((options & 2) > 0);
    }
}
