package net.megavex.scoreboardlibrary.internal.nms.base;

import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import org.jetbrains.annotations.Nullable;

public interface ImmutableTeamProperties<T> {
  default Collection<String> entries() {
    return Set.of();
  }

  T displayName();

  T prefix();

  T suffix();

  default boolean friendlyFire() {
    return false;
  }

  default boolean canSeeFriendlyInvisibles() {
    return false;
  }

  default NameTagVisibility nameTagVisibility() {
    return NameTagVisibility.ALWAYS;
  }

  default CollisionRule collisionRule() {
    return CollisionRule.ALWAYS;
  }

  @Nullable
  default NamedTextColor playerColor() {
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
