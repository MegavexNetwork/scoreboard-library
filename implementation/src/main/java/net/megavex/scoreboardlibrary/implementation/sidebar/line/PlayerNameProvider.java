package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class PlayerNameProvider {
  private static final ChatColor[] CHAT_COLORS = ChatColor.values();
  private static final List<String> DEFAULT_NAMES = createLinePlayerNames(Sidebar.MAX_LINES);

  private PlayerNameProvider() {
  }

  public static @NotNull @Unmodifiable List<String> provideLinePlayerNames(int maxLines) {
    return maxLines <= Sidebar.MAX_LINES ? DEFAULT_NAMES : createLinePlayerNames(maxLines);
  }

  private static @NotNull @Unmodifiable List<String> createLinePlayerNames(int maxLines) {
    List<String> result = new ArrayList<>(maxLines);

    int i = 0;
    while (result.size() < maxLines) {
      for (ChatColor color : CHAT_COLORS) {
        String legacy = color.toString();
        String newStr = i == 0 ? legacy : result.get(i - 1) + legacy;

        if (!result.contains(newStr)) {
          result.add(newStr);
        }
      }
      i++;
    }

    return Collections.unmodifiableList(result);
  }
}
