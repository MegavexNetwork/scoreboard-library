package net.megavex.scoreboardlibrary.api.sidebar.animation;

import org.jetbrains.annotations.NotNull;

/**
 * @param <T> frame type
 */
public interface SidebarAnimation<T> {
  /**
   * @return the current frame of the animation
   */
  @NotNull T currentFrame();

  /**
   * Advances to the next frame of the animation
   */
  void nextFrame();
}
