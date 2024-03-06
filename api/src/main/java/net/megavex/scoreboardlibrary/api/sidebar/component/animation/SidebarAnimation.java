package net.megavex.scoreboardlibrary.api.sidebar.component.animation;

import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import org.jetbrains.annotations.NotNull;

/**
 * An animation that can be used with {@link ComponentSidebarLayout}s
 *
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
