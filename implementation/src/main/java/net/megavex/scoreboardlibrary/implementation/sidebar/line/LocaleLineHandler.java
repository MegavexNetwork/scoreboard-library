package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.sidebar.BetterAbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LocaleLine;
import org.bukkit.entity.Player;

public class LocaleLineHandler {
  private final BetterAbstractSidebar sidebar;
  private final Locale locale;

  private SidebarLineHandler modernLineHandler, legacyLineHandler;

  public LocaleLineHandler(BetterAbstractSidebar sidebar, Locale locale) {
    this.sidebar = sidebar;
    this.locale = locale;
  }

  public BetterAbstractSidebar sidebar() {
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
          modernLineHandler = new SidebarLineHandler(this);
        }

        return modernLineHandler;
      }
      case LEGACY -> {
        if (legacyLineHandler == null) {
          legacyLineHandler = new SidebarLineHandler(this);
        }

        return legacyLineHandler;
      }
    }

    throw new RuntimeException();
  }

  public LocaleLine<?>[] linesInit(LineType lineType) {
    if (lineType == LineType.MODERN) {
      if (lines == null) {
        lines = new LocaleLine[sidebar.maxLines()];
        initLines(lineType);
      }
      return lines;
    } else {
      if (legacyLines == null) {
        legacyLines = new LocaleLine[sidebar.maxLines()];
        initLines(lineType);
      }
      return legacyLines;
    }
  }

  public void update() {
    update(LineType.MODERN);
    update(LineType.LEGACY);
  }

  private void update(LineType lineType) {
    var lines = lines(lineType);
    if (lines == null) return;
    var players = players(lineType);

    for (var line : lines) {
      if (line != null) {
        if (line.info().updateTeams) {
          line.updateTeam();
        }

        if (line.info().updateScore && sidebar.visible()) {
          line.sendScore(players);
        }
      }
    }
  }

  public void setLine(int line, Component renderedLine) {
    setLine(line, renderedLine, LineType.MODERN);
    setLine(line, renderedLine, LineType.LEGACY);
  }

  public void setLine(int line, Component renderedLine, LineType lineType) {
    setLine(line, renderedLine, lineType, true);
  }

  public void setLine(int line, Component renderedLine, LineType lineType, boolean sendPackets) {
    var lines = lines(lineType);
    if (lines == null) return;

    var localeLine = lines[line];
    if (renderedLine == null && localeLine == null) {
      return;
    }

    var newlyCreated = false;
    if (localeLine == null) {
      lines[line] = localeLine = lineType.create(sidebar.getLineInfo(line), this);
      newlyCreated = true;
    }

    if (renderedLine == null) {
      lines[line] = null;
    } else {
      localeLine.value(renderedLine);
    }

    if (!sidebar.visible() || !sendPackets) return;

    var players = players(lineType);

    if (renderedLine == null) {
      localeLine.hide(players);
    } else if (newlyCreated) {
      localeLine.show(players);
    }
  }

  public void show() {
    if (players != null) show(players, LineType.MODERN);
    if (legacyPlayers != null) show(legacyPlayers, LineType.LEGACY);
  }

  public void hide() {
    if (players != null) hide(players, LineType.MODERN);
    if (legacyPlayers != null) hide(legacyPlayers, LineType.LEGACY);
  }

  public void show(Collection<Player> players, LineType lineType) {
    var lines = linesInit(lineType);
    for (var line : lines) {
      if (line != null && line.info().value != null) {
        line.show(players);
      }
    }
  }

  public void hide(Collection<Player> players, LineType lineType) {
    var lines = lines(lineType);
    if (lines == null) return;
    for (var line : lines) {
      if (line != null && ((line.info().update) || line.info().value != null)) {
        line.hide(players);
      }
    }
  }

  private void initLines(LineType lineType) {

  }
}
