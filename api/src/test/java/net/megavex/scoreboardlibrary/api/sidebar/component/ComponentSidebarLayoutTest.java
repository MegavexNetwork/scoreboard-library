package net.megavex.scoreboardlibrary.api.sidebar.component;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import org.junit.jupiter.api.Test;


import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ComponentSidebarLayoutTest {
  private final Sidebar sidebar = new NoopScoreboardLibrary().createSidebar();

  @Test
  void maxLines() {
    var builder = SidebarComponent.builder();
    for (int i = 0; i < Sidebar.MAX_LINES + 1; i++) {
      builder.addComponent(SidebarComponent.staticLine(text(i)));
    }

    var componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, builder.build());

    componentSidebar.apply(sidebar);

    for (int i = 0; i < Sidebar.MAX_LINES; i++) {
      assertNotNull(sidebar.line(i));
    }
  }

  @Test
  void titleComponent() {
    var title = text("title");
    var componentSidebar = new ComponentSidebarLayout(SidebarComponent.staticLine(title), drawable -> {
    });
    componentSidebar.apply(sidebar);
    assertEquals(title, sidebar.title());
  }

  @Test
  void animatedLines() {
    var animation = new CollectionSidebarAnimation<Component>(List.of(text("frame 1"), text("frame 2")));
    var lines = SidebarComponent.builder().addAnimatedLine(animation).build();
    var componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, lines);

    componentSidebar.apply(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));
  }

  @Test
  void animatedComponents() {
    var frame1Line = text("frame with one line");
    var frame2Line1 = text("frame with");
    var frame2Line2 = text("two lines");

    var frame1 = SidebarComponent.staticLine(frame1Line);
    SidebarComponent frame2 = drawable -> {
      drawable.drawLine(frame2Line1);
      drawable.drawLine(frame2Line2);
    };

    var animation = new CollectionSidebarAnimation<>(List.of(frame1, frame2));
    var lines = SidebarComponent.builder().addAnimatedComponent(animation).build();
    var componentSidebar = new ComponentSidebarLayout(drawable -> {}, lines);

    componentSidebar.apply(sidebar);
    assertEquals(frame1Line, sidebar.line(0));
    assertNull(sidebar.line(1));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    assertEquals(frame2Line1, sidebar.line(0));
    assertEquals(frame2Line2, sidebar.line(1));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    assertEquals(frame1Line, sidebar.line(0));
    assertNull(sidebar.line(1));
  }
}
