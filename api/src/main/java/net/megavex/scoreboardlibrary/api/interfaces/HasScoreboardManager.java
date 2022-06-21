package net.megavex.scoreboardlibrary.api.interfaces;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

/**
 * Something that has a {@link ScoreboardManager}
 */
public interface HasScoreboardManager {
  /**
   * @return The {@link ScoreboardManager} which is associated with this object
   */
  @NotNull ScoreboardManager scoreboardManager();
}
