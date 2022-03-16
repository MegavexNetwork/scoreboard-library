package net.megavex.scoreboardlibrary.api.sidebar;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Represents an in-game Sidebar
 */
public interface Sidebar extends HasScoreboardManager, Closeable, Players {

  // Constants

  byte MAX_LINES = 15;

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
  byte maxLines();

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
   * @return Visibility
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
  void line(int line, Component value);

  /**
   * Gets a line's value
   *
   * @param line Line
   * @return Value of line
   */
  @Nullable Component line(int line);

  /**
   * Returns the current title
   *
   * @return Title
   */
  @NotNull Component title();

  /**
   * Sets the title
   *
   * @param title Title
   */
  void title(@NotNull Component title);
}
