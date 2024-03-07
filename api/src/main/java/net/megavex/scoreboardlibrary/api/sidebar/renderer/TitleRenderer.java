package net.megavex.scoreboardlibrary.api.sidebar.renderer;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;

public interface TitleRenderer {
  ComponentLike renderTitle(Player player);
}
