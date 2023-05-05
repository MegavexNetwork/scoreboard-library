package net.megavex.scoreboardlibrary.api.animation;

import com.google.common.base.Preconditions;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface Animation<T> {
  static <T> @NotNull Animation<T> animation(@NotNull Collection<T> frames) {
    Preconditions.checkNotNull(frames);
    Preconditions.checkArgument(!frames.isEmpty());
    return new CollectionAnimation<>(frames);
  }

  @NotNull T currentFrame();
  void nextFrame();
}
