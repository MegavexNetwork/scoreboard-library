package net.megavex.scoreboardlibrary.api.sidebar.component.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * A {@link SidebarAnimation} with predetermined frames
 *
 * @param <T> Frame type
 */
public interface FramedSidebarAnimation<T> extends SidebarAnimation<T> {
  /**
   * @return All frames in this animation
   */
  @Unmodifiable @NotNull List<T> frames();

  /**
   * @return The index of the current frame
   */
  int currentFrameIndex();

  /**
   * Switches the current frame
   *
   * @param frameIndex The index of the new current frame
   * @throws ArrayIndexOutOfBoundsException If frame is out of bounds
   */
  void switchFrame(int frameIndex);
}
