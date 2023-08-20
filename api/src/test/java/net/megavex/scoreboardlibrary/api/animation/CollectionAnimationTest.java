package net.megavex.scoreboardlibrary.api.animation;

import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionAnimationTest {
  @Test
  void loopTest() {
    List<Integer> frames = Arrays.asList(0, 1, 2);
    CollectionSidebarAnimation<Integer> animation = new CollectionSidebarAnimation<>(frames);
    assertEquals(0, animation.currentFrame());
    animation.nextFrame();
    assertEquals(1, animation.currentFrame());
    animation.nextFrame();
    assertEquals(2, animation.currentFrame());
    animation.nextFrame();
    assertEquals(0, animation.currentFrame());
  }
}
