package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib;

import com.comphenix.protocol.wrappers.WrappedNumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class ScoreFormatConverter {
  private ScoreFormatConverter() {
  }

  public static @NotNull WrappedNumberFormat convert(@NotNull Locale locale, @NotNull ScoreFormat format) {
    if (format == ScoreFormat.blank()) {
      return WrappedNumberFormat.blank();
    } else if (format instanceof ScoreFormat.Styled) {
      Style style = ((ScoreFormat.Styled) format).style();
      return WrappedNumberFormat.styled(ComponentConversions.wrapAdventureStyle(style));
    } else if (format instanceof ScoreFormat.Fixed) {
      Component content = GlobalTranslator.render(((ScoreFormat.Fixed) format).content(), locale);
      return WrappedNumberFormat.fixed(ComponentConversions.wrapAdventureComponent(content));
    } else {
      throw new IllegalArgumentException("Invalid score format: " + format);
    }
  }
}
