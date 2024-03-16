package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

// NOTE: ProtocolLib already has these utilities (AdventureComponentConverter)
// but they cannot be used as adventure is potentially relocated
public final class ComponentConversions {
  private static final GsonComponentSerializer SERIALIZER;

  static {
    if (MinecraftVersion.NETHER_UPDATE.atOrAbove()) {
      SERIALIZER = GsonComponentSerializer.gson();
    } else {
      SERIALIZER = GsonComponentSerializer.colorDownsamplingGson();
    }
  }

  public static @NotNull WrappedChatComponent wrapAdventure(@NotNull Component component) {
    return WrappedChatComponent.fromJson(SERIALIZER.serialize(component));
  }
}
