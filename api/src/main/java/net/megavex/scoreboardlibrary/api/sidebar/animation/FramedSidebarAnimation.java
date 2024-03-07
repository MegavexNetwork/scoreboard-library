package net.megavex.scoreboardlibrary.api.sidebar.animation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

/**
 * A {@link SidebarAnimation} with predetermined frames.
 *
 * @param <T> frame type
 */
public interface FramedSidebarAnimation<T> extends SidebarAnimation<T> {
  static <R> FramedSidebarAnimation<R> create(@NotNull Collection<R> frames) {
    Preconditions.checkNotNull(frames);
    Preconditions.checkArgument(!frames.isEmpty());
    return new CollectionSidebarAnimation<>(ImmutableList.copyOf(frames));
  }

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
