package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import java.util.Set;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LineType;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LocaleLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SidebarLineHandler {
  private final LineType lineType;
  private final LocaleLineHandler localeLineHandler;
  private final Set<Player> players = CollectionProvider.set(1);
  private final LocaleLine<?>[] lines;

  public SidebarLineHandler(@NotNull LineType lineType, @NotNull LocaleLineHandler localeLineHandler) {
    this.lineType = lineType;
    this.localeLineHandler = localeLineHandler;
    this.lines = new LocaleLine[localeLineHandler.sidebar().maxLines()];

    for (var line : localeLineHandler.sidebar().lines()) {
      if (line != null) {
        setLine(line.line, GlobalTranslator.render(line.value, localeLineHandler.locale()), false);
      }
    }
  }

  public @NotNull LocaleLineHandler localeLineHandler() {
    return localeLineHandler;
  }

  public @NotNull Set<Player> players() {
    return players;
  }

  public @NotNull LocaleLine<?>[] lines() {
    return lines;
  }

  public void update() {
    for (var line : lines) {
      if (line != null) {
        if (line.info().updateTeams) {
          line.updateTeam();
        }

        if (line.info().updateScore) {
          line.sendScore(players);
        }
      }
    }
  }


  public void setLine(int line, Component renderedLine) {
    setLine(line, renderedLine, true);
  }

  private void setLine(int line, Component renderedLine, boolean sendPackets) {
    var localeLine = lines[line];
    if (renderedLine == null && localeLine == null) {
      return;
    }

    var newlyCreated = false;
    if (localeLine == null) {
      lines[line] = localeLine = lineType.create(localeLineHandler.sidebar().getLineInfo(line), this);
      newlyCreated = true;
    }

    if (renderedLine == null) {
      lines[line] = null;
    } else {
      localeLine.value(renderedLine);
    }

    if (renderedLine == null) {
      localeLine.hide(players);
    } else if (newlyCreated) {
      localeLine.show(players);
    }
  }

  public void show() {
    for (var line : lines) {
      line.show(players);
    }
  }

  public void hide() {
    for (var line : lines) {
      line.hide(players);
    }
  }
}
