package net.megavex.scoreboardlibrary.api.sidebar.component;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.Component.empty;

public final class ComponentBasedSidebar {
  private SidebarComponent titleComponent = drawable -> {
  };
  private SidebarComponent rootComponent = drawable -> {
  };

  public @NotNull SidebarComponent titleComponent() {
    return titleComponent;
  }

  public void titleComponent(@NotNull SidebarComponent titleComponent) {
    Preconditions.checkNotNull(titleComponent);
    this.titleComponent = titleComponent;
  }

  public @NotNull SidebarComponent rootComponent() {
    return rootComponent;
  }

  public void rootComponent(@NotNull SidebarComponent rootComponent) {
    Preconditions.checkNotNull(rootComponent);
    this.rootComponent = rootComponent;
  }

  public void update(@NotNull Sidebar sidebar) {
    Preconditions.checkNotNull(sidebar);

    var titleDrawable = new SidebarTitleDrawable();
    titleComponent.draw(titleDrawable);
    sidebar.title(titleDrawable.title == null ? empty() : titleDrawable.title);

    var linesDrawable = new SidebarLineDrawable(sidebar);
    rootComponent.draw(linesDrawable);

    for (int i = linesDrawable.index; i < Sidebar.MAX_LINES; i++) {
      sidebar.line(i, null);
    }
  }

  private static class SidebarTitleDrawable implements LineDrawable {
    private Component title;

    @Override
    public void drawLine(@NotNull Component line) {
      Preconditions.checkNotNull(line);

      if (title == null) {
        title = line;
      }
    }
  }

  private static class SidebarLineDrawable implements LineDrawable {
    private final Sidebar sidebar;
    private int index = 0;

    public SidebarLineDrawable(@NotNull Sidebar sidebar) {
      this.sidebar = sidebar;
    }

    @Override
    public void drawLine(@NotNull Component line) {
      Preconditions.checkNotNull(line);

      if (index < Sidebar.MAX_LINES) {
        sidebar.line(index++, line);
      }
    }
  }
}
