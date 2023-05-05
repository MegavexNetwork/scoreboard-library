package net.megavex.scoreboardlibrary.api.sidebar.component;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.animation.Animation;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.Component.empty;

public interface SidebarComponent {
  static @NotNull SidebarComponent staticLine(@NotNull Component line) {
    Preconditions.checkNotNull(line);
    return drawable -> drawable.drawLine(line);
  }

  static @NotNull SidebarComponent blankLine() {
    return drawable -> drawable.drawLine(empty());
  }

  static @NotNull SidebarComponent dynamicLine(@NotNull Supplier<Component> lineSupplier) {
    return drawable -> drawable.drawLine(lineSupplier.get());
  }

  static @NotNull SidebarComponent animatedLine(@NotNull Animation<Component> animation) {
    Preconditions.checkNotNull(animation);
    return drawable -> drawable.drawLine(animation.currentFrame());
  }

  static @NotNull SidebarComponent animatedComponent(@NotNull Animation<SidebarComponent> animation) {
    Preconditions.checkNotNull(animation);
    return drawable -> animation.currentFrame().draw(drawable);
  }

  void draw(@NotNull LineDrawable drawable);
}
