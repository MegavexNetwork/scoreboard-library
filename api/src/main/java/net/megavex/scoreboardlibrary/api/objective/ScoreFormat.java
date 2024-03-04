package net.megavex.scoreboardlibrary.api.objective;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @since Minecraft 1.20.3
 */
@ApiStatus.NonExtendable
public interface ScoreFormat {
  static @NotNull Blank blank() {
    return Blank.INSTANCE;
  }

  static @NotNull Fixed fixed(@NotNull Component content) {
    Preconditions.checkNotNull(content);
    return new Fixed(content);
  }

  static @NotNull Styled styled(@NotNull Style style) {
    Preconditions.checkNotNull(style);
    return new Styled(style);
  }

  class Blank implements ScoreFormat {
    private static final Blank INSTANCE = new Blank();

    private Blank() {
    }

    @Override
    public String toString() {
      return "Blank";
    }
  }

  class Fixed implements ScoreFormat {
    private final Component content;

    public Fixed(@NotNull Component content) {
      this.content = content;
    }

    public @NotNull Component content() {
      return content;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Fixed that = (Fixed) o;
      return content.equals(that.content);
    }

    @Override
    public int hashCode() {
      return content.hashCode();
    }

    @Override
    public String toString() {
      return "Fixed{content=" + content + "}";
    }
  }

  class Styled implements ScoreFormat {
    private final Style style;

    public Styled(@NotNull Style style) {
      this.style = style;
    }

    public @NotNull Style style() {
      return style;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Styled that = (Styled) o;
      return style.equals(that.style);
    }

    @Override
    public int hashCode() {
      return style.hashCode();
    }

    @Override
    public String toString() {
      return "Styled{style=" + style + "}";
    }
  }
}
