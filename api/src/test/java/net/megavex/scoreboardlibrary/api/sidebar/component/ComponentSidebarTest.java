package net.megavex.scoreboardlibrary.api.sidebar.component;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.animation.CollectionAnimation;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.junit.jupiter.api.Test;


import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ComponentSidebarTest {
  private final Sidebar sidebar = new NoopScoreboardLibrary().createSidebar();

  @Test
  void maxLines() {
    var componentSidebar = new ComponentSidebar();
    for (int i = 0; i < Sidebar.MAX_LINES + 1; i++) {
      componentSidebar.addComponent(SidebarComponent.staticLine(text(i)));
    }

    componentSidebar.update(sidebar);

    for (int i = 0; i < Sidebar.MAX_LINES; i++) {
      assertNotNull(sidebar.line(i));
    }
  }

  @Test
  void titleComponent() {
    var componentSidebar = new ComponentSidebar();
    var title = text("title");
    componentSidebar.titleComponent(SidebarComponent.staticLine(title));
    componentSidebar.update(sidebar);
    assertEquals(title, sidebar.title());
  }

  @Test
  void staticLines() {
    var componentSidebar = new ComponentSidebar();
    var line1 = text("line 1");
    componentSidebar.addComponent(SidebarComponent.staticLine(line1));
    componentSidebar.update(sidebar);
    assertEquals(line1, sidebar.line(0));
    assertNull(sidebar.line(1));

    var line2 = text("line 2");
    componentSidebar.addComponent(SidebarComponent.staticLine(line2));
    componentSidebar.update(sidebar);
    assertEquals(line2, sidebar.line(1));
  }

  @Test
  void animatedLines() {
    var componentSidebar = new ComponentSidebar();
    var animation = new CollectionAnimation<Component>(List.of(text("frame 1"), text("frame 2")));
    componentSidebar.addComponent(SidebarComponent.animatedLine(animation));
    componentSidebar.update(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));
    animation.nextFrame();
    componentSidebar.update(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));
  }

  @Test
  void animatedComponents() {
    var componentSidebar = new ComponentSidebar();

    var frame1Line = text("frame with one line");
    var frame2Line1 = text("frame with");
    var frame2Line2 = text("two lines");

    var frame1 = SidebarComponent.staticLine(frame1Line);
    SidebarComponent frame2 = drawable -> {
      drawable.drawLine(frame2Line1);
      drawable.drawLine(frame2Line2);
    };

    var animation = new CollectionAnimation<>(List.of(frame1, frame2));
    componentSidebar.addComponent(SidebarComponent.animatedComponent(animation));

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
