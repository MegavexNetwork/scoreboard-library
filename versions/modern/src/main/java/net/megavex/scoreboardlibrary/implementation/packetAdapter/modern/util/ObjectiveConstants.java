package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ObjectiveConstants {
  public static final int MODE_CREATE = 0;
  public static final int MODE_REMOVE = 1;
  public static final int MODE_UPDATE = 2;

  private ObjectiveConstants() {
  }

  public static int displaySlotIndex(@NotNull ObjectiveDisplaySlot slot) {
    if (slot instanceof ObjectiveDisplaySlot.PlayerList) {
      return 0;
    }

    if (slot instanceof ObjectiveDisplaySlot.Sidebar) {
      return 1;
    }

    if (slot instanceof ObjectiveDisplaySlot.BelowName) {
      return 2;
    }

    NamedTextColor color = ((ObjectiveDisplaySlot.TeamSidebar) slot).teamColor();
    char legacyChar = LegacyFormatUtil.getChar(color);
    ChatFormatting formatting = Objects.requireNonNull(ChatFormatting.getByCode(legacyChar));
    return 3 + formatting.getId();
  }
}
