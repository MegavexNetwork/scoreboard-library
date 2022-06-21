package net.megavex.scoreboardlibrary.api.sidebar;

import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Represents an in-game Sidebar
 */
public interface Sidebar extends HasScoreboardManager, Closeable, Players {

  // Constants

  int MAX_LINES = 15;

  // Main

  /**
   * Gets the {@link ComponentTranslator} of this Sidebar
   *
   * @return Component translator
   */
  @NotNull ComponentTranslator componentTranslator();

  /**
   * Gets the max amount of lines this sidebar can have
   *
   * @return Max line amount
   */
  @Range(from = 1, to = MAX_LINES) int maxLines();

  /**
   * Gets the {@link Locale} which is used to translate {@link net.kyori.adventure.text.TranslatableComponent}s
   *
   * @return Locale
   */
  @Nullable Locale locale();

  // Visibility

  /**
   * Returns the visibility of this Sidebar
   *
   * @return Current visibility of this Sidebar
   */
  boolean visible();

  /**
   * Changes the visibility of this Sidebar
   *
   * @param visible New visibility of this sidebar
   */
  void visible(boolean visible);

  // Lines

  /**
   * Sets a line's value
   *
   * @param line  Line
   * @param value Value
   */
  void line(int line, @Nullable Component value);

  /**
   * Gets a line's value
   *
   * @param line Line
   * @return Value of line
   */
  @Nullable Component line(int line);

  /**
   * @return Current title of this Sidebar
   */
  @NotNull Component title();

  /**
   * Sets the title
   *
   * @param title Title
   */
  void title(@NotNull Component title);
}
