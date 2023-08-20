package net.megavex.scoreboardlibrary.api.sidebar.component;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.*;

class ComponentSidebarLayoutTest {
  private final Sidebar sidebar = new NoopScoreboardLibrary().createSidebar();

  @Test
  void maxLines() {
    SidebarComponent.Builder builder = SidebarComponent.builder();
    for (int i = 0; i < Sidebar.MAX_LINES + 1; i++) {
      builder.addComponent(SidebarComponent.staticLine(text(i)));
    }

    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, builder.build());

    componentSidebar.apply(sidebar);

    for (int i = 0; i < Sidebar.MAX_LINES; i++) {
      assertNotNull(sidebar.line(i));
    }
  }

  @Test
  void titleComponent() {
    Component title = text("title");
    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(SidebarComponent.staticLine(title), drawable -> {
    });
    componentSidebar.apply(sidebar);
    assertEquals(title, sidebar.title());
  }

  @Test
  void animatedLines() {
    SidebarAnimation<Component> animation = new CollectionSidebarAnimation<>(Arrays.asList(text("frame 1"), text("frame 2")));
    SidebarComponent lines = SidebarComponent.builder().addAnimatedLine(animation).build();
    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, lines);

    componentSidebar.apply(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    assertEquals(animation.currentFrame(), sidebar.line(0));
  }

  @Test
  void animatedComponents() {
    Component frame1Line = text("frame with one line");
    Component frame2Line1 = text("frame with");
    Component frame2Line2 = text("two lines");

    SidebarComponent frame1 = SidebarComponent.staticLine(frame1Line);
    SidebarComponent frame2 = drawable -> {
      drawable.drawLine(frame2Line1);
      drawable.drawLine(frame2Line2);
    };

    SidebarAnimation<SidebarComponent> animation = new CollectionSidebarAnimation<>(Arrays.asList(frame1, frame2));
    SidebarComponent lines = SidebarComponent.builder().addAnimatedComponent(animation).build();
    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, lines);

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
