package net.megavex.scoreboardlibrary.api.animation;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class CollectionAnimation<T> implements Animation<T> {
  private final List<T> frames;
  private int currentFrameIndex;

  CollectionAnimation(@NotNull Collection<T> frames) {
    this.frames = ImmutableList.copyOf(frames);
  }

  @NotNull
  @Override
  public T currentFrame() {
    return frames.get(currentFrameIndex);
  }

  @Override
  public void nextFrame() {
    if (++currentFrameIndex == frames.size()) {
      currentFrameIndex = 0;
    }
  }
}
