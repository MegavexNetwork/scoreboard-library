package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ColorNameTest {
  @Test
  void colorNameTest() {
    for (String key : NamedTextColor.NAMES.keys()) {
      assertNotNull(EnumChatFormat.b(key));
    }
  }
}
