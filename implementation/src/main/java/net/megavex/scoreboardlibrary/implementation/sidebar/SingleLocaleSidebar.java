package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleLocaleSidebar extends AbstractSidebar {
  private final Locale locale;
  private final LocaleLineHandler sidebar;
  private final Set<Player> players;

  public SingleLocaleSidebar(@NotNull ScoreboardLibraryImpl scoreboardLibrary, int size, Locale locale) {
    super(scoreboardLibrary, size);
    this.locale = locale;
    this.sidebar = new LocaleLineHandler(this, locale());
    this.players = CollectionProvider.set(1);
  }

  @Override
  public @NotNull Collection<Player> players() {
    return closed() ? Set.of() : Collections.unmodifiableCollection(players);
  }

  @Override
  protected @Nullable LocaleLineHandler addPlayer0(@NotNull Player player) {
    if (!players.add(player)) return null;

    return sidebar;
  }

  @Override
  protected @Nullable LocaleLineHandler removePlayer0(@NotNull Player player) {
    if (players == null || !players.remove(player)) return null;

    return sidebar;
  }

  @Override
  public @Nullable Locale locale() {
    return locale;
  }

  @Override
  protected void forEachSidebar(@NotNull Consumer<LocaleLineHandler> consumer) {
    consumer.accept(sidebar);
  }
}
