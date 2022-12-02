package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDependantLocaleSidebar extends AbstractSidebar {
  private final Map<Player, LocaleLineHandler> playerMap = new HashMap<>();
  private final Map<Locale, LocaleLineHandler> localeMap = new HashMap<>();

  public PlayerDependantLocaleSidebar(ScoreboardLibraryImpl scoreboardLibrary, int maxLines) {
    super(scoreboardLibrary, maxLines);
  }

  @Override
  public @Nullable Locale locale() {
    return null;
  }

  @Override
  protected void forEachSidebar(@NotNull Consumer<LocaleLineHandler> consumer) {
    if (localeMap != null) {
      for (var value : localeMap.values()) {
        consumer.accept(value);
      }
    }
  }

  @Override
  public @NotNull Collection<Player> players() {
    return playerMap == null ? Set.of() : playerMap.keySet();
  }

  @Override
  protected @Nullable LocaleLineHandler addPlayer0(@NotNull Player player) {
    var sidebar = playerMap.get(player);
    if (sidebar != null) {
      return null;
    }

    var locale = scoreboardLibrary().localeProvider.locale(player);

    sidebar = localeMap.get(locale);
    if (sidebar != null) {
      playerMap.put(player, sidebar);
      return sidebar;
    }

    sidebar = new LocaleLineHandler(this, locale);

    localeMap.put(locale, sidebar);
    playerMap.put(player, sidebar);

    return sidebar;
  }

  @Override
  protected @Nullable LocaleLineHandler removePlayer0(@NotNull Player player) {
    if (playerMap == null) return null;

    var lineHandler = playerMap.remove(player);
    if (lineHandler == null) return null;

    if (!lineHandler.hasPlayers() && localeMap != null) {
      localeMap.remove(lineHandler.locale());
    }

    return lineHandler;
  }
}
