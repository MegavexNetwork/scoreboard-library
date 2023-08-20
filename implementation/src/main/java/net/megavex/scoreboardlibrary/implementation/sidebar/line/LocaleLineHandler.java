package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

  public void addPlayer(@NotNull Player player) {
    boolean isLegacy = sidebar.scoreboardLibrary().packetAdapter().isLegacy(player);
    LineType lineType = isLegacy ? LineType.LEGACY : LineType.MODERN;
    lineHandler(lineType).players().add(player);
  }

  public void removePlayer(@NotNull Player player) {
    if (modernLineHandler != null && modernLineHandler.players().remove(player)) {
      return;
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.players().remove(player);
    }
  }

  public @NotNull SidebarLineHandler lineHandler(@NotNull LineType lineType) {
    switch (lineType) {
      case MODERN: {
        if (modernLineHandler == null) {
          modernLineHandler = new SidebarLineHandler(LineType.MODERN, this);
        }

        return modernLineHandler;
      }
      case LEGACY: {
        if (legacyLineHandler == null) {
          legacyLineHandler = new SidebarLineHandler(LineType.LEGACY, this);
        }

        return legacyLineHandler;
      }
    }

    throw new RuntimeException();
  }

  public void updateLine(int lineIndex, Component renderedValue) {
    if (modernLineHandler != null) {
      modernLineHandler.setLine(lineIndex, renderedValue);
      modernLineHandler.updateLine(lineIndex);
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.setLine(lineIndex, renderedValue);
      legacyLineHandler.updateLine(lineIndex);
    }
  }

  public void updateScores() {
    if (modernLineHandler != null) {
      modernLineHandler.updateScores();
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.updateScores();
    }
  }

  public void show(Player player) {
    if (modernLineHandler != null && modernLineHandler.players().contains(player)) {
      modernLineHandler.show(Collections.singleton(player));
    } else if (legacyLineHandler != null && legacyLineHandler.players().contains(player)) {
      legacyLineHandler.show(Collections.singleton(player));
    }
  }

  public void hide(Player player) {
    if (modernLineHandler != null && modernLineHandler.players().contains(player)) {
      modernLineHandler.hide(Collections.singleton(player));
    } else if (legacyLineHandler != null && legacyLineHandler.players().contains(player)) {
      legacyLineHandler.hide(Collections.singleton(player));
    }
  }

  public void hide() {
    if (modernLineHandler != null) {
      modernLineHandler.hide(modernLineHandler.players());
    }

    if (legacyLineHandler != null) {
      legacyLineHandler.hide(legacyLineHandler.players());
    }
  }
}
