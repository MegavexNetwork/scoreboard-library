package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

import static net.kyori.adventure.text.Component.empty;

// Implementation for versions above 1.12.2
class ModernLocaleLine implements LocaleLine<Component> {
  private final GlobalLineInfo info;
  private final SidebarLineHandler handler;
  private final Collection<String> entries;
  private final TeamsPacketAdapter.TeamDisplayPacketAdapter<Component> packetAdapter;

  public ModernLocaleLine(GlobalLineInfo info, SidebarLineHandler handler) {
    this.info = info;
    this.handler = handler;
    this.entries = Collections.singleton(info.player());
    this.packetAdapter = info.packetAdapter().createTeamDisplayAdapter(this);
    packetAdapter.updateTeamPackets(entries);
  }

  @Override
  public @NotNull GlobalLineInfo info() {
    return info;
  }

  @Override
  public void value(@NotNull Component renderedComponent) {
  }

  @Override
  public void updateTeam() {
    packetAdapter.updateTeamPackets(entries);
    packetAdapter.updateTeam(handler.players());
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players) {
    handler.localeLineHandler().sidebar().packetAdapter().sendScore(players, info.player(), info.objectiveScore());
  }

  @Override
  public void show(@NotNull Collection<Player> players) {
    sendScore(players);
    packetAdapter.createTeam(players);
  }

  @Override
  public void hide(@NotNull Collection<Player> players) {
    handler.localeLineHandler().sidebar().packetAdapter().removeScore(players, info.player());
    info.packetAdapter().removeTeam(players);
  }

  @Override
  public @NotNull Collection<String> entries() {
    return entries;
  }

  @Override
  public @NotNull Component displayName() {
    return empty();
  }

  @Override
  public @NotNull Component prefix() {
    Component value = info.value();
    return value == null ? empty() : value;
  }

  @Override
  public @NotNull Component suffix() {
    return empty();
  }
}
