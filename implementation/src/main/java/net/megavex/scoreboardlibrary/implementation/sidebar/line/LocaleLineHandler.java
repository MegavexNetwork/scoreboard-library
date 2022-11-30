package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;

import java.util.Locale;

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

  public SidebarLineHandler lineHandler(LineType lineType) {
    switch (lineType) {
      case MODERN -> {
        if (modernLineHandler == null) {
          modernLineHandler = new SidebarLineHandler(LineType.MODERN, this);
        }

        return modernLineHandler;
      }
      case LEGACY -> {
        if (legacyLineHandler == null) {
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
