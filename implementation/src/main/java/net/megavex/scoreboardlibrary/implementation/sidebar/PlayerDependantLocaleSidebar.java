package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDependantLocaleSidebar extends AbstractSidebar {
  private volatile Map<Player, SidebarLineHandler> playerMap;
  private volatile Map<Locale, SidebarLineHandler> localeMap;

  public PlayerDependantLocaleSidebar(ScoreboardLibraryImpl scoreboardLibrary, ComponentTranslator componentTranslator, int size) {
    super(scoreboardLibrary, componentTranslator, size);
  }

  @Override
  public @Nullable Locale locale() {
    return null;
  }

  @Override
  protected void forEachSidebar(Consumer<SidebarLineHandler> consumer) {
    if (localeMap != null) {
      for (var value : localeMap.values()) {
        consumer.accept(value);
      }
    }
  }

  @Override
  public @NotNull Collection<Player> players() {
    return playerMap == null ? Set.of():playerMap.keySet();
  }

  @Override
  protected SidebarLineHandler addPlayer0(Player player) {
    var sidebar = playerMap == null ? null:playerMap.get(player);
    if (sidebar != null) {
      return null;
    }

    var locale = scoreboardLibrary().localeProvider.locale(player);

    if (localeMap == null)
      synchronized (lock) {
        if (localeMap == null) localeMap = new ConcurrentHashMap<>(4, 0.75f, 2);
      }

    sidebar = localeMap.get(locale);
    if (sidebar != null) {
      playerMap.put(player, sidebar);
      return sidebar;
    }

    sidebar = new SidebarLineHandler(this, locale);

    localeMap.put(locale, sidebar);

    if (playerMap == null)
      synchronized (lock) {
        if (playerMap == null) playerMap = new ConcurrentHashMap<>(4, 0.75f, 2);
      }

    playerMap.put(player, sidebar);

    return sidebar;
  }

  @Override
  protected SidebarLineHandler removePlayer0(Player player) {
    if (playerMap == null) return null;

    var lineHandler = playerMap.remove(player);
    if (lineHandler == null) return null;

    if (!lineHandler.hasPlayers() && localeMap != null) {
      localeMap.remove(lineHandler.locale());
    }

    return lineHandler;
  }

  @Override
  public void close() {
    super.close();

    if (closed) {
      playerMap = null;
      localeMap = null;
    }
  }
}
