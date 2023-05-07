package net.megavex.scoreboardlibrary.api.sidebar.component.animation;

import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentBasedSidebar;
import org.jetbrains.annotations.NotNull;

/**
 * An animation that can be used with {@link ComponentBasedSidebar}s
 *
 * @param <T> Frame type
 */
public interface SidebarAnimation<T> {
  /**
   * @return The current frame of the animation
   */
  @NotNull T currentFrame();

  /**
   * Advances to the next frame of the animation
   */
  void nextFrame();
}
