package net.megavex.scoreboardlibrary.api.animation;

import org.jetbrains.annotations.NotNull;

public interface Animation<T> {
  @NotNull T currentFrame();

  void nextFrame();
}
