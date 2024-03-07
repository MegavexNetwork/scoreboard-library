package net.megavex.scoreboardlibrary.api.sidebar.drawable;

import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;

public interface FormattedLineDrawable extends LineDrawable {
  @Override
  default void drawLine(ComponentLike line) {
    drawLine(line, (ScoreFormat) null);
  }

  default void drawLine(ComponentLike line, ComponentLike scoreText) {
    drawLine(line, ScoreFormat.fixed(scoreText));
  }

  void drawLine(ComponentLike line, ScoreFormat scoreFormat);
}
