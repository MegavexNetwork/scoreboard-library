package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;

public final class NativeAdventureUtil {
  private NativeAdventureUtil() {
  }

  public static AdventureComponent fromAdventureComponent(Component component) {
    return new AdventureComponent(component);
  }
}
