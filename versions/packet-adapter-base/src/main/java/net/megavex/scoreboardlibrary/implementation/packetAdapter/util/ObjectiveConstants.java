package net.megavex.scoreboardlibrary.implementation.packetAdapter.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public final class ObjectiveConstants {
  public static final int MODE_CREATE = 0;
  public static final int MODE_REMOVE = 1;
  public static final int MODE_UPDATE = 2;
  public static final int LEGACY_VALUE_CHAR_LIMIT = 32;

  private ObjectiveConstants() {
  }

  public static int mode(ObjectivePacketAdapter.ObjectivePacketType packetType) {
    switch (packetType) {
      case CREATE:
        return MODE_CREATE;
      case UPDATE:
        return MODE_UPDATE;
      default:
        throw new IllegalStateException();
    }
  }

  public static int displaySlotIndex(@NotNull ObjectiveDisplaySlot slot, boolean isLegacy) {
    if (slot instanceof ObjectiveDisplaySlot.PlayerList) {
      return 0;
    }

    if (slot instanceof ObjectiveDisplaySlot.Sidebar) {
      return 1;
    }

    if (slot instanceof ObjectiveDisplaySlot.BelowName) {
      return 2;
    }

    // Colored sidebar slots don't exist in legacy versions
    if (isLegacy) {
      return 1;
    }

    NamedTextColor color = ((ObjectiveDisplaySlot.TeamSidebar) slot).teamColor();
    char legacyChar = LegacyFormatUtil.getChar(color);
    ChatColor chatColor = ChatColor.getByChar(legacyChar);
    return 3 + chatColor.ordinal();
  }
}
