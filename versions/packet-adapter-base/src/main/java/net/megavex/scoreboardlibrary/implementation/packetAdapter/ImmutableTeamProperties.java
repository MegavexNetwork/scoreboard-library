package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import java.util.Collection;
import java.util.Collections;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ImmutableTeamProperties<T> {
  default @NotNull Collection<String> entries() {
    return Collections.emptySet();
  }

  @NotNull T displayName();

  @NotNull T prefix();

  @NotNull T suffix();

  default boolean friendlyFire() {
    return false;
  }

  default boolean canSeeFriendlyInvisibles() {
    return false;
  }

  default @NotNull NameTagVisibility nameTagVisibility() {
    return NameTagVisibility.ALWAYS;
  }

  default @NotNull CollisionRule collisionRule() {
    return CollisionRule.ALWAYS;
  }

  default @Nullable NamedTextColor playerColor() {
    return null;
  }

  default int packOptions() {
    int options = 0;

    if (this.friendlyFire()) {
      options |= 1;
    }

    if (this.canSeeFriendlyInvisibles()) {
      options |= 2;
    }

    return options;
  }
}
