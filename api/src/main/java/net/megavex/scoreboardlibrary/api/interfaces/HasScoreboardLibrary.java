package net.megavex.scoreboardlibrary.api.interfaces;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import org.jetbrains.annotations.NotNull;

/**
 * Something that has a {@link ScoreboardLibrary}
 */
public interface HasScoreboardLibrary {
  /**
   * @return The {@link ScoreboardLibrary} which is associated with this object
   */
  @NotNull ScoreboardLibrary scoreboardLibrary();
}
