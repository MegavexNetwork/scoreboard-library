package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import com.google.gson.JsonElement;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MethodAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.Locale;
import java.util.Optional;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public final class ScoreFormatConverter {
  private static final MethodAccessor RESULT_UNWRAP_METHOD;
  private static final Object STYLE_CODEC;
  private static final MethodAccessor CODEC_PARSE;
  private static final Object JSON_OPS;
  private static final Object BLANK;
  private static final ConstructorAccessor<?> STYLED_CONSTRUCTOR;
  private static final ConstructorAccessor<?> FIXED_CONSTRUCTOR;

  static {
    try {
      Class<?> dataResultClass = ReflectUtil.getClassOrThrow("com.mojang.serialization.DataResult");
      RESULT_UNWRAP_METHOD = ReflectUtil.findMethod(dataResultClass, "result", false, MethodType.methodType(Optional.class));

      Class<?> styleSerializerClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Style$Serializer");
      STYLE_CODEC = styleSerializerClass.getField("CODEC").get(null);

      Class<?> jsonOpsClass = ReflectUtil.getClassOrThrow("com.mojang.serialization.JsonOps");
      JSON_OPS = jsonOpsClass.getField("INSTANCE").get(null);

      Class<?> codecClass = ReflectUtil.getClassOrThrow("com.mojang.serialization.Codec");
      Class<?> dynamicOpsClass = ReflectUtil.getClassOrThrow("com.mojang.serialization.DynamicOps");
      CODEC_PARSE = ReflectUtil.findMethod(codecClass, "parse", false, MethodType.methodType(dataResultClass, dynamicOpsClass, Object.class));

      Class<?> blankFormatClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.BlankFormat");
      BLANK = blankFormatClass.getField("INSTANCE").get(null);

      Class<?> styledFormatClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.StyledFormat");
      STYLED_CONSTRUCTOR = ReflectUtil.findConstructor(styledFormatClass, PacketAccessors.STYLE_CLASS);

      Class<?> fixedFormatClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.FixedFormat");
      FIXED_CONSTRUCTOR = ReflectUtil.findConstructor(fixedFormatClass, PacketAccessors.COMPONENT_CLASS);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private ScoreFormatConverter() {
  }

  public static @Nullable Object convert(@Nullable Locale locale, @Nullable ScoreFormat format) {
    if (format == null || !PacketAccessors.IS_1_20_3_OR_ABOVE) {
      return null;
    }

    if (format == ScoreFormat.blank()) {
      return BLANK;
    } else if (format instanceof ScoreFormat.Styled) {
      JsonElement json = gson().serializer().toJsonTree(((ScoreFormat.Styled) format).style());
      Object result = CODEC_PARSE.invoke(STYLE_CODEC, JSON_OPS, json);
      //noinspection rawtypes,OptionalGetWithoutIsPresent
      Object style = ((Optional) RESULT_UNWRAP_METHOD.invoke(result)).get();
      return STYLED_CONSTRUCTOR.invoke(style);
    } else if (format instanceof ScoreFormat.Fixed) {
      return FIXED_CONSTRUCTOR.invoke(ModernComponentProvider.fromAdventure(((ScoreFormat.Fixed) format).content(), locale));
    } else {
      throw new IllegalArgumentException("Invalid score format: " + format);
    }
  }
}
