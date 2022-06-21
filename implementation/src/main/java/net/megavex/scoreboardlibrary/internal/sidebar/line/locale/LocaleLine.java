package net.megavex.scoreboardlibrary.internal.sidebar.line.locale;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.sidebar.line.GlobalLineInfo;
import org.bukkit.entity.Player;

public interface LocaleLine<C> extends ImmutableTeamProperties<C> {
  GlobalLineInfo info();

  void value(Component renderedComponent);

  void updateTeam();

  void sendScore(Collection<Player> players);

  void show(Collection<Player> players);

  void hide(Collection<Player> players);
}
