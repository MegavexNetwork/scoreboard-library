package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NativeAdventureUtil {
  private NativeAdventureUtil() {
  }

  public static @NotNull Object fromAdventureComponent(@NotNull Component component) {
    return Objects.requireNonNull(PacketAccessors.ADVENTURE_COMPONENT_CONSTRUCTOR).invoke(component);
  }
}
