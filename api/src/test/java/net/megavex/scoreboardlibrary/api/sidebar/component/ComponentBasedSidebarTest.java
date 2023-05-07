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

class ComponentBasedSidebarTest {
  private final Sidebar sidebar = new NoopScoreboardLibrary().createSidebar();

  @Test
  void maxLines() {
    var componentSidebar = new ComponentBasedSidebar();
    var builder = SidebarComponent.builder();
    for (int i = 0; i < Sidebar.MAX_LINES + 1; i++) {
      builder.addComponent(SidebarComponent.staticLine(text(i)));
    }

    componentSidebar.rootComponent(builder.build());
    componentSidebar.update(sidebar);

    for (int i = 0; i < Sidebar.MAX_LINES; i++) {
      assertNotNull(sidebar.line(i));
    }
  }

  @Test
  void titleComponent() {
    var componentSidebar = new ComponentBasedSidebar();
    var title = text("title");
    componentSidebar.titleComponent(SidebarComponent.staticLine(title));
    componentSidebar.update(sidebar);
    assertEquals(title, sidebar.title());
  }

  @Test
  void staticLines() {
    var componentSidebar = new ComponentBasedSidebar();
    var line1 = text("line 1");
    componentSidebar.rootComponent(SidebarComponent.builder().addStaticLine(line1).build());
    componentSidebar.update(sidebar);
    assertEquals(line1, sidebar.line(0));
    assertNull(sidebar.line(1));

    var line2 = text("line 2");
    componentSidebar.rootComponent(SidebarComponent.builder().addStaticLine(line1).addStaticLine(line2).build());
    componentSidebar.update(sidebar);
    assertEquals(line2, sidebar.line(1));
  }

  @Test
  void animatedLines() {
    var componentSidebar = new ComponentBasedSidebar();
    var animation = new CollectionSidebarAnimation<Component>(List.of(text("frame 1"), text("frame 2")));
    componentSidebar.rootComponent(SidebarComponent.builder().addAnimatedLine(animation).build());

    componentSidebar.update(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));

    animation.nextFrame();
    componentSidebar.update(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));
  }

  @Test
  void animatedComponents() {
    var componentSidebar = new ComponentBasedSidebar();

    var frame1Line = text("frame with one line");
    var frame2Line1 = text("frame with");
    var frame2Line2 = text("two lines");

    var frame1 = SidebarComponent.staticLine(frame1Line);
    SidebarComponent frame2 = drawable -> {
      drawable.drawLine(frame2Line1);
      drawable.drawLine(frame2Line2);
    };

    var animation = new CollectionSidebarAnimation<>(List.of(frame1, frame2));
    componentSidebar.rootComponent(SidebarComponent.builder().addAnimatedComponent(animation).build());

    componentSidebar.update(sidebar);
    assertEquals(frame1Line, sidebar.line(0));
    assertNull(sidebar.line(1));

    animation.nextFrame();
    componentSidebar.update(sidebar);
    assertEquals(frame2Line1, sidebar.line(0));
    assertEquals(frame2Line2, sidebar.line(1));

    animation.nextFrame();
    componentSidebar.update(sidebar);
    assertEquals(frame1Line, sidebar.line(0));
    assertNull(sidebar.line(1));
  }
}
