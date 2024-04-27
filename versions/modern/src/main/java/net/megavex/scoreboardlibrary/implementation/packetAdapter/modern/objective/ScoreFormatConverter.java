package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public final class ScoreFormatConverter {
  private ScoreFormatConverter() {
  }

  public static @Nullable Object convert(@NotNull ComponentProvider componentProvider, @Nullable Locale locale, @Nullable ScoreFormat format) {
    if (format == null || !PacketAccessors.IS_1_20_3_OR_ABOVE) {
      return null;
    }

    if (format == ScoreFormat.blank()) {
      return BlankFormat.INSTANCE;
    } else if (format instanceof ScoreFormat.Styled) {
      JsonElement json = gson().serializer().toJsonTree(((ScoreFormat.Styled) format).style());
      DataResult<Style> result = Style.Serializer.CODEC.parse(JsonOps.INSTANCE, json);
      // Style style = Util.getOrThrow(result, RuntimeException::new);
      Style style = result.getOrThrow(RuntimeException::new);
      return new StyledFormat(style);
    } else if (format instanceof ScoreFormat.Fixed) {
      return new FixedFormat(componentProvider.fromAdventure(((ScoreFormat.Fixed) format).content(), locale));
    } else {
      throw new IllegalArgumentException("Invalid score format: " + format);
    }
  }
}
