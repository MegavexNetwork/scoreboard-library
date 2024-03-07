package net.megavex.scoreboardlibrary.api.sidebar.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

final class CollectionSidebarAnimation<T> implements FramedSidebarAnimation<T> {
  private final List<T> frames;
  private int currentFrameIndex;

  CollectionSidebarAnimation(List<T> frames) {
    this.frames = frames;
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
