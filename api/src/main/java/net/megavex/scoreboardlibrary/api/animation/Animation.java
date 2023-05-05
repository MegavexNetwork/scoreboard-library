package net.megavex.scoreboardlibrary.api.animation;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface Animation<T> {
  static <T> @NotNull Animation<T> animation(@NotNull Collection<T> frames) {
    return new CollectionAnimation<>(frames);
  }

  @NotNull T currentFrame();

  void nextFrame();
}
