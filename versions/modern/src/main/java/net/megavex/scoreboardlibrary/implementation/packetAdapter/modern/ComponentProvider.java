package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.RegistryUtil;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Locale;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public final class ComponentProvider {
  private static final MethodHandle FROM_JSON_METHOD;
  public static final boolean IS_NATIVE_ADVENTURE;

  static {
    boolean isNativeAdventure = false;
    try {
      Class.forName("io.papermc.paper.adventure.PaperAdventure");

      // Hide from relocation checkers
      String notRelocatedPackage = "net.ky".concat("ori.adventure.text");

      // The native adventure optimisations only work when the adventure library isn't relocated
      if (Component.class.getPackage().getName().equals(notRelocatedPackage)) {
        isNativeAdventure = true;
      }
    } catch (ClassNotFoundException ignored) {
    }

    IS_NATIVE_ADVENTURE = isNativeAdventure;
    if (isNativeAdventure){
      FROM_JSON_METHOD = null;
    } else {
      MethodHandle handle = null;
      for (Method method : Serializer.class.getMethods()) {
        if (method.getReturnType() == MutableComponent.class &&
          method.getParameterCount() >= 1 &&
          method.getParameterCount() <= 2 &&
          method.getParameterTypes()[0] == JsonElement.class
        ) {
          try {
            handle = MethodHandles.lookup().unreflect(method);
            break;
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      }

      if (handle == null) {
        throw new ExceptionInInitializerError("failed to find chat component fromJson method");
      }

      FROM_JSON_METHOD = handle;
    }
  }

  private ComponentProvider() {
  }

  public static net.minecraft.network.chat.@NotNull Component fromAdventure(@NotNull Component adventure, @Nullable Locale locale) {
    if (FROM_JSON_METHOD == null) {
      return NativeAdventureUtil.fromAdventureComponent(adventure);
    }

    Component translated = adventure;
    if (locale != null) {
      translated = GlobalTranslator.render(adventure, locale);
    }

    JsonElement json = gson().serializeToTree(translated);
    Object[] args;
    if (PacketAccessors.IS_1_20_5_OR_ABOVE) {
      args = new Object[]{json, RegistryUtil.MINECRAFT_REGISTRY};
    } else {
      args = new Object[]{json};
    }

    try {
      return (net.minecraft.network.chat.Component) FROM_JSON_METHOD.invokeWithArguments(args);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
