package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LocaleLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class SidebarLineHandler {
  private final LineType lineType;
  private final LocaleLineHandler localeLineHandler;
  private final Set<Player> players = CollectionProvider.set(1);
  private final LocaleLine<?>[] lines;

  public SidebarLineHandler(@NotNull LineType lineType, @NotNull LocaleLineHandler localeLineHandler) {
    this.lineType = lineType;
    this.localeLineHandler = localeLineHandler;
    this.lines = new LocaleLine[localeLineHandler.sidebar().maxLines()];

    for (GlobalLineInfo line : localeLineHandler.sidebar().lines()) {
      if (line != null) {
        Component value = line.value();
        if (value != null) {
          setLine(line.line(), GlobalTranslator.render(value, localeLineHandler.locale()), false);
        }
      }
    }
  }

  public @NotNull LocaleLineHandler localeLineHandler() {
    return localeLineHandler;
  }

  public @NotNull Set<Player> players() {
    return players;
  }

  public void updateLine(int lineIndex) {
    LocaleLine<?> line = lines[lineIndex];
    if (line != null) {
      line.updateTeam();
    }
  }

  public void updateScores() {
    for (LocaleLine<?> line : lines) {
      if (line != null && line.info().updateScore()) {
        line.sendScore(players);
        line.info().updateScore(false);
      }
    }
  }

  public void setLine(int line, Component renderedLine) {
    setLine(line, renderedLine, true);
  }

  private void setLine(int line, Component renderedLine, boolean sendPackets) {
    LocaleLine<?> localeLine = lines[line];
    if (renderedLine == null && localeLine == null) {
      return;
    }

    boolean newlyCreated = false;
    if (localeLine == null) {
      lines[line] = localeLine = lineType.create(localeLineHandler.sidebar().lines()[line], this);
      newlyCreated = true;
    }

    if (renderedLine == null) {
      lines[line] = null;
    } else {
      localeLine.value(renderedLine);
    }

    if (sendPackets) {
      if (renderedLine == null) {
        localeLine.hide(players);
      } else if (newlyCreated) {
        localeLine.show(players);
      }
    }
  }

  public void show(Collection<Player> players) {
    for (LocaleLine<?> line : lines) {
      if (line != null) {
        line.show(players);
      }
    }
  }

  public void hide(Collection<Player> players) {
    for (LocaleLine<?> line : lines) {
      if (line != null) {
        line.hide(players);
      }
    }
  }
}
