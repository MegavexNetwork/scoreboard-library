package net.megavex.scoreboardlibrary.api.sidebar.component;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.empty;

public interface SidebarComponent {
  static @NotNull SidebarComponent staticLine(@NotNull ComponentLike line) {
    Preconditions.checkNotNull(line);
    return drawable -> drawable.drawLine(line);
  }

  static @NotNull SidebarComponent staticLine(@NotNull ComponentLike line, @NotNull ScoreFormat scoreFormat) {
    Preconditions.checkNotNull(line);
    return drawable -> drawable.drawLine(line, scoreFormat);
  }

  static @NotNull SidebarComponent blankLine() {
    return drawable -> drawable.drawLine(empty());
  }

  static @NotNull <T extends ComponentLike> SidebarComponent dynamicLine(@NotNull Supplier<T> lineSupplier) {
    return drawable -> drawable.drawLine(lineSupplier.get());
  }

  static @NotNull <T extends ComponentLike> SidebarComponent animatedLine(@NotNull SidebarAnimation<T> animation) {
    Preconditions.checkNotNull(animation);
    return drawable -> drawable.drawLine(animation.currentFrame());
  }

  static @NotNull <T extends SidebarComponent> SidebarComponent animatedComponent(@NotNull SidebarAnimation<T> animation) {
    Preconditions.checkNotNull(animation);
    return drawable -> animation.currentFrame().draw(drawable);
  }

  static @NotNull Builder builder() {
    return new Builder();
  }

  void draw(@NotNull LineDrawable drawable);

  final class Builder {
    private final List<SidebarComponent> children = new ArrayList<>(4);

    private Builder() {
    }

    public @NotNull Builder addComponent(@NotNull SidebarComponent component) {
      Preconditions.checkNotNull(component);
      children.add(component);
      return this;
    }

    public @NotNull Builder addStaticLine(@NotNull ComponentLike line) {
      return addComponent(SidebarComponent.staticLine(line));
    }

    public @NotNull Builder addStaticLine(@NotNull ComponentLike line, @NotNull ScoreFormat scoreFormat) {
      return addComponent(SidebarComponent.staticLine(line, scoreFormat));
    }

    public @NotNull Builder addBlankLine() {
      return addComponent(SidebarComponent.blankLine());
    }

    public @NotNull <T extends ComponentLike> Builder addDynamicLine(@NotNull Supplier<T> lineSupplier) {
      return addComponent(SidebarComponent.dynamicLine(lineSupplier));
    }

    public <T extends ComponentLike> @NotNull Builder addAnimatedLine(@NotNull SidebarAnimation<T> animation) {
      return addComponent(SidebarComponent.animatedLine(animation));
    }

    public @NotNull <T extends SidebarComponent> Builder addAnimatedComponent(@NotNull SidebarAnimation<T> animation) {
      return addComponent(SidebarComponent.animatedComponent(animation));
    }

    public @NotNull SidebarComponent build() {
      ImmutableList<SidebarComponent> children = ImmutableList.copyOf(this.children);
      return drawable -> {
        for (SidebarComponent child : children) {
          child.draw(drawable);
        }
      };
    }
  }
}
