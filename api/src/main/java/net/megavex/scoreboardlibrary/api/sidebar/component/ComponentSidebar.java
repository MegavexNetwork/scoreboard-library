package net.megavex.scoreboardlibrary.api.sidebar.component;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.animation.Animation;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.jetbrains.annotations.NotNull;

public class ComponentSidebar {
  private SidebarComponent titleComponent = drawable -> {
  };
  private final List<SidebarComponent> components = new ArrayList<>(4);

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

  // Convenience methods
  public void addStaticLine(@NotNull Component line) {
    addComponent(SidebarComponent.staticLine(line));
  }

  public void addBlankLine() {
    addComponent(SidebarComponent.blankLine());
  }

  public void addDynamicLine(@NotNull Supplier<Component> lineSupplier) {
    addComponent(SidebarComponent.dynamicLine(lineSupplier));
  }

  public void addAnimatedLine(@NotNull Animation<Component> animation) {
    addComponent(SidebarComponent.animatedLine(animation));
  }

  public void addAnimatedComponent(@NotNull Animation<SidebarComponent> animation) {
    addComponent(SidebarComponent.animatedComponent(animation));
  }

  public void update(@NotNull Sidebar sidebar) {
    Preconditions.checkNotNull(sidebar);

    titleComponent.draw(new SidebarTitleDrawable(sidebar));

    var lines = new SidebarLineDrawable(sidebar);
    for (var component : components) {
      component.draw(lines);
    }

    for (int i = lines.index; i < Sidebar.MAX_LINES; i++) {
      sidebar.line(i, null);
    }
  }

  private static class SidebarTitleDrawable implements LineDrawable {
    private final Sidebar sidebar;
    private boolean isFirst = true;

    public SidebarTitleDrawable(@NotNull Sidebar sidebar) {
      this.sidebar = sidebar;
    }

    @Override
    public void drawLine(@NotNull Component line) {
      if (isFirst) {
        sidebar.title(line);
        isFirst = false;
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
      if (index < Sidebar.MAX_LINES) {
        sidebar.line(index++, line);
      }
    }
  }
}
