package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static com.github.retrooper.packetevents.protocol.score.ScoreFormat.*;

public final class ScoreFormatConverter {
  private ScoreFormatConverter() {
  }

  public static @Nullable com.github.retrooper.packetevents.protocol.score.ScoreFormat convert(@NotNull Locale locale, @Nullable ScoreFormat format) {
    if (format == null) {
      return null;
    }

    if (format == ScoreFormat.blank()) {
      return blankScore();
    } else if (format instanceof ScoreFormat.Styled) {
      return styledScore(((ScoreFormat.Styled) format).style());
    } else if (format instanceof ScoreFormat.Fixed) {
      return fixedScore(GlobalTranslator.render(((ScoreFormat.Fixed) format).content(), locale));
    } else {
      throw new IllegalArgumentException("Invalid score format: " + format);
    }
  }
}
