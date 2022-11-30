package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public class LocaleLineHandler {
  private final AbstractSidebar sidebar;
  private final Locale locale;

  private SidebarLineHandler modernLineHandler, legacyLineHandler;

  public LocaleLineHandler(AbstractSidebar sidebar, Locale locale) {
    this.sidebar = sidebar;
    this.locale = locale;
  }

  public AbstractSidebar sidebar() {
    return sidebar;
  }

  public Locale locale() {
    return locale;
  }

  public boolean hasPlayers() {
    return (modernLineHandler != null && !modernLineHandler.players().isEmpty()) || (legacyLineHandler != null && !legacyLineHandler.players().isEmpty());
  }

  public @NotNull SidebarLineHandler lineHandler(@NotNull LineType lineType) {
    return Objects.requireNonNull(lineHandler(lineType, true));
  }

  public @Nullable SidebarLineHandler lineHandler(@NotNull LineType lineType, boolean create) {
    switch (lineType) {
      case MODERN -> {
        if (modernLineHandler == null && create) {
          modernLineHandler = new SidebarLineHandler(LineType.MODERN, this);
        }

        return modernLineHandler;
      }
      case LEGACY -> {
        if (legacyLineHandler == null && create) {
          legacyLineHandler = new SidebarLineHandler(LineType.LEGACY, this);
        }

        return legacyLineHandler;
      }
    }

    throw new RuntimeException();
  }

  public void update() {
    if (modernLineHandler != null) {
      modernLineHandler.update();
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.update();
    }
  }

  public void setLine(int line, Component renderedLine) {
    if (modernLineHandler != null) {
      modernLineHandler.setLine(line, renderedLine);
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.setLine(line, renderedLine);
    }
  }

  public void show() {
    if (modernLineHandler != null) {
      modernLineHandler.show();
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.show();
    }
  }

  public void hide() {
     if (modernLineHandler != null) {
      modernLineHandler.hide();
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.hide();
    }
  }
}
