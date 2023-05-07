package net.megavex.scoreboardlibrary.api.animation;

import java.util.List;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionAnimationTest {
  @Test
  void loopTest() {
    var frames = List.of(0, 1, 2);
    var animation = new CollectionSidebarAnimation<>(frames);
    assertEquals(0, animation.currentFrame());
    animation.nextFrame();
    assertEquals(1, animation.currentFrame());
    animation.nextFrame();
    assertEquals(2, animation.currentFrame());
    animation.nextFrame();
    assertEquals(0, animation.currentFrame());
  }
}
