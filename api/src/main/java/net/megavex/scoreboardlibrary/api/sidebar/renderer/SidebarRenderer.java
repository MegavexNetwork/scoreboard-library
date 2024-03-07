package net.megavex.scoreboardlibrary.api.sidebar.renderer;

import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.sidebar.drawable.LineDrawable;
import org.bukkit.entity.Player;

public interface SidebarRenderer extends TitleRenderer, SidebarComponent {
  default SidebarRenderer join(TitleRenderer titleRenderer, SidebarComponent linesRenderer) {
    return new SidebarRenderer() {
      @Override
      public void draw(Player player, LineDrawable drawable) {
        linesRenderer.draw(player, drawable);
      }

      @Override
      public ComponentLike renderTitle(Player player) {
        return titleRenderer.renderTitle(player);
      }
    };
  }
}
