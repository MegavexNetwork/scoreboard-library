package net.megavex.scoreboardlibrary.api.sidebar.component;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.jetbrains.annotations.NotNull;

public class ComponentSidebar {
  private final Sidebar sidebar;
  private SidebarComponent titleComponent = drawable -> {
  };
  private final List<SidebarComponent> components = new ArrayList<>(4);

  public ComponentSidebar(@NotNull Sidebar sidebar) {
    this.sidebar = sidebar;
  }

  public @NotNull Sidebar sidebar() {
    return sidebar;
  }

  public @NotNull SidebarComponent titleComponent() {
    return titleComponent;
  }

  public void titleComponent(@NotNull SidebarComponent titleComponent) {
    Preconditions.checkNotNull(titleComponent);
    this.titleComponent = titleComponent;
  }

  public void addComponent(@NotNull SidebarComponent component) {
    components.add(component);
  }

  public void update() {
    titleComponent.draw(new SidebarTitleDrawable());

    var lines = new SidebarLineDrawable();
    for (var component : components) {
      component.draw(lines);
    }
    lines.clearRemainingLines();
  }

  private class SidebarTitleDrawable implements LineDrawable {
    private boolean isFirst = true;

    @Override
    public void drawLine(@NotNull Component line) {
      if (isFirst) {
        sidebar.title(line);
        isFirst = false;
      }
    }
  }

  private class SidebarLineDrawable implements LineDrawable {
    private int index = 0;

    @Override
    public void drawLine(@NotNull Component line) {
      if (index < Sidebar.MAX_LINES) {
        sidebar.line(index++, line);
      }
    }

    private void clearRemainingLines() {
      for (int i = index; i < Sidebar.MAX_LINES; i++) {
        sidebar.line(i, null);
      }
    }
  }
}
