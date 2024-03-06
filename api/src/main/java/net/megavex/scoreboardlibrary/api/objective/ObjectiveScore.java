package net.megavex.scoreboardlibrary.api.objective;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an objective score.
 */
public final class ObjectiveScore {
  private final int value;
  private final Component display;
  private final ScoreFormat format;

  public ObjectiveScore(int value, @Nullable Component display, @Nullable ScoreFormat format) {
    this.value = value;
    this.display = display;
    this.format = format;
  }

  /**
   * @return score value
   */
  public int value() {
    return value;
  }

  /**
   * Gets the score display.
   *
   * @return score display
   * @since Minecraft 1.20.3
   */
  public @Nullable Component display() {
    return display;
  }

  /**
   * Gets the score format, which determines how the scores are rendered in clients.
   *
   * @return score format
   * @since Minecraft 1.20.3
   */
  public @Nullable ScoreFormat format() {
    return format;
  }

  @Override
  public String toString() {
    return "ObjectiveScore{" +
      "value=" + value +
      ", display=" + display +
      ", format=" + format +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ObjectiveScore that = (ObjectiveScore) o;

    if (value != that.value) return false;
    if (!Objects.equals(display, that.display)) return false;
    return Objects.equals(format, that.format);
  }

  @Override
  public int hashCode() {
    int result = value;
    result = 31 * result + (display != null ? display.hashCode() : 0);
    result = 31 * result + (format != null ? format.hashCode() : 0);
    return result;
  }
}
