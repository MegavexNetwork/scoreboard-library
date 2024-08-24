package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections;

import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ColorNameTest {
  @Test
  void colorNameTest() {
    Class<Object> enumChatFormatClass = RandomUtils.getServerClass("EnumChatFormat");

    for (String key : NamedTextColor.NAMES.keys()) {
      assertNotNull(RandomUtils.invokeStaticMethod(
        enumChatFormatClass,
        "b",
        new Object[]{key},
        new Class<?>[]{String.class}
      ));
    }
  }
}
