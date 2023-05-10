package net.megavex.scoreboardlibrary.api.sidebar.component.animation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class CollectionSidebarAnimation<T> implements FramedSidebarAnimation<T> {
  private final List<T> frames;
  private int currentFrameIndex;

  public CollectionSidebarAnimation(@NotNull Collection<T> frames) {
    Preconditions.checkNotNull(frames);
    Preconditions.checkArgument(!frames.isEmpty());
    this.frames = ImmutableList.copyOf(frames);
  }

  @Override
  public @Unmodifiable @NotNull List<T> frames() {
    return frames;
  }

  @Override
  public int currentFrameIndex() {
    return currentFrameIndex;
  }

  @Override
  public void switchFrame(int frameIndex) {
    if (frameIndex < 0 || frameIndex >= frames.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }

    currentFrameIndex = frameIndex;
  }

  @Override
  public @NotNull T currentFrame() {
    return frames.get(currentFrameIndex);
  }

  @Override
  public void nextFrame() {
    if (++currentFrameIndex == frames.size()) {
      currentFrameIndex = 0;
    }
  }
}
