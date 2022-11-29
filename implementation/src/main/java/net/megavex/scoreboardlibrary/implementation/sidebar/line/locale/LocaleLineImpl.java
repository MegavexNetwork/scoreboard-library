package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.Component.empty;

// Implementation for versions above 1.12.2
class LocaleLineImpl implements LocaleLine<Component> {
  private final GlobalLineInfo info;
  private final LocaleLineHandler handler;
  private final Collection<String> entries;
  private final TeamsPacketAdapter.TeamInfoPacketAdapter<Component> bridge;
  private boolean update = false;

  public LocaleLineImpl(GlobalLineInfo info, LocaleLineHandler handler) {
    this.info = info;
    this.handler = handler;
    this.entries = Set.of(info.player());
    this.bridge = info.bridge.createTeamInfoAdapter(this);
    bridge.updateTeamPackets(entries);
  }

  @Override
  public GlobalLineInfo info() {
    return info;
  }

  @Override
  public void value(Component renderedComponent) {
    update = true;
  }

  @Override
  public void updateTeam() {
    if (!update) {
      return;
    }

    bridge.updateTeamPackets(entries);
    bridge.updateTeam(handler.players(LineType.MODERN));
    update = false;
  }

  @Override
  public void sendScore(Collection<Player> players) {
    handler.sidebar().packetAdapter().score(players, info.objectiveScore, info.player());
  }

  @Override
  public void show(Collection<Player> players) {
    sendScore(players);
    bridge.createTeam(players);
  }

  @Override
  public void hide(Collection<Player> players) {
    handler.sidebar().packetAdapter().removeLine(players, info.player());
    info.bridge.removeTeam(players);
  }

  @Override
  public Collection<String> entries() {
    return entries;
  }

  @Override
  public @NotNull Component displayName() {
    return empty();
  }

  @Override
  public @NotNull Component prefix() {
    return info.value;
  }

  @Override
  public @NotNull Component suffix() {
    return empty();
  }
}
