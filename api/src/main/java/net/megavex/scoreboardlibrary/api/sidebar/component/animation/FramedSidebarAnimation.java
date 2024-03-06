package net.megavex.scoreboardlibrary.api.sidebar.component.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * A {@link SidebarAnimation} with predetermined frames.
 *
 * @param <T> frame type
 */
public interface FramedSidebarAnimation<T> extends SidebarAnimation<T> {
  /**
   * @return all frames in this animation
   */
  @Unmodifiable @NotNull List<T> frames();

  /**
   * @return the index of the current frame
   */
  int currentFrameIndex();

  /**
   * Switches the current frame
   *
   * @param frameIndex the index of the new current frame
   * @throws ArrayIndexOutOfBoundsException if frame is out of bounds
   */
  void switchFrame(int frameIndex);
}
