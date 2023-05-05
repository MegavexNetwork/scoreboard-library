package net.megavex.scoreboardlibrary.api.sidebar.component;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.animation.Animation;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.junit.jupiter.api.Test;


import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ComponentSidebarTest {
  private final ScoreboardLibrary scoreboardLibrary = new NoopScoreboardLibrary();

  @Test
  void maxLines() {
    var sidebar = new ComponentSidebar(scoreboardLibrary.createSidebar());
    for (int i = 0; i < Sidebar.MAX_LINES + 1; i++) {
      sidebar.addComponent(SidebarComponent.staticLine(text(i)));
    }

    sidebar.update();

    for (int i = 0; i < Sidebar.MAX_LINES; i++) {
      assertNotNull(sidebar.sidebar().line(i));
    }
  }

  @Test
  void titleComponent() {
    var sidebar = new ComponentSidebar(scoreboardLibrary.createSidebar());
    var title = text("title");
    sidebar.titleComponent(SidebarComponent.staticLine(title));
    sidebar.update();
    assertEquals(title, sidebar.sidebar().title());
  }

  @Test
  void staticLines() {
    var sidebar = new ComponentSidebar(scoreboardLibrary.createSidebar());
    var line1 = text("line 1");
    sidebar.addComponent(SidebarComponent.staticLine(line1));
    sidebar.update();
    assertEquals(line1, sidebar.sidebar().line(0));
    assertNull(sidebar.sidebar().line(1));

    var line2 = text("line 2");
    sidebar.addComponent(SidebarComponent.staticLine(line2));
    sidebar.update();
    assertEquals(line2, sidebar.sidebar().line(1));
  }

  @Test
  void animatedLines() {
    var sidebar = new ComponentSidebar(scoreboardLibrary.createSidebar());
    var animation = Animation.<Component>animation(List.of(text("frame 1"), text("frame 2")));
    sidebar.addComponent(SidebarComponent.animatedLine(animation));
    sidebar.update();
    assertEquals(animation.currentFrame(), sidebar.sidebar().line(0));
    animation.nextFrame();
    sidebar.update();
    assertEquals(animation.currentFrame(), sidebar.sidebar().line(0));
  }

  @Test
  void animatedComponents() {
    var sidebar = new ComponentSidebar(scoreboardLibrary.createSidebar());

    var frame1Line = text("frame with one line");
    var frame2Line1 = text("frame with");
    var frame2Line2 = text("two lines");

    var frame1 = SidebarComponent.staticLine(frame1Line);
    SidebarComponent frame2 = drawable -> {
      drawable.drawLine(frame2Line1);
      drawable.drawLine(frame2Line2);
    };

    var animation = Animation.animation(List.of(frame1, frame2));
    sidebar.addComponent(SidebarComponent.animatedComponent(animation));

    sidebar.update();
    assertEquals(frame1Line, sidebar.sidebar().line(0));
    assertNull(sidebar.sidebar().line(1));

    animation.nextFrame();
    sidebar.update();
    assertEquals(frame2Line1, sidebar.sidebar().line(0));
    assertEquals(frame2Line2, sidebar.sidebar().line(1));

    animation.nextFrame();
    sidebar.update();
    assertEquals(frame1Line, sidebar.sidebar().line(0));
    assertNull(sidebar.sidebar().line(1));
  }
}
