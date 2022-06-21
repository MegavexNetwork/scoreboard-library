package net.megavex.scoreboardlibrary.internal.sidebar.line;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.internal.sidebar.line.locale.LineType;
import net.megavex.scoreboardlibrary.internal.sidebar.line.locale.LocaleLine;
import org.bukkit.entity.Player;

public class SidebarLineHandler {

  private final AbstractSidebar sidebar;
  private final Locale locale;

  private Set<Player> players, legacyPlayers;
  private LocaleLine<?>[] lines, legacyLines;

  public SidebarLineHandler(AbstractSidebar sidebar, Locale locale) {
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
    if (players != null && !players.isEmpty()) return true;
    return legacyPlayers != null && !legacyPlayers.isEmpty();
  }

  public Set<Player> players(LineType lineType) {
    if (lineType == LineType.NEW) {
      return players;
    } else {
      return legacyPlayers;
    }
  }

  public Set<Player> playersInit(LineType lineType) {
    if (lineType == LineType.NEW) {
      if (players == null) players = CollectionProvider.set(1);
      return players;
    } else {
      if (legacyPlayers == null) legacyPlayers = CollectionProvider.set(1);
      return legacyPlayers;
    }
  }

  public LocaleLine<?>[] lines(LineType lineType) {
    if (lineType == LineType.NEW) {
      return lines;
    } else {
      return legacyLines;
    }
  }

  public LocaleLine<?>[] linesInit(LineType lineType) {
    if (lineType == LineType.NEW) {
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
    update(LineType.NEW);
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
    setLine(line, renderedLine, LineType.NEW);
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
    if (players != null) show(players, LineType.NEW);
    if (legacyPlayers != null) show(legacyPlayers, LineType.LEGACY);
  }

  public void hide() {
    if (players != null) hide(players, LineType.NEW);
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
    for (var line : sidebar.lines) {
      if (line != null) {
        setLine(line.line, sidebar.componentTranslator().translate(line.value, locale), lineType, false);
      }
    }
  }
}
