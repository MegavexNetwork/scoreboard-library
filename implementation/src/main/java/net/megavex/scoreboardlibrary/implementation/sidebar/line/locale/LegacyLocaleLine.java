package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

class LegacyLocaleLine implements LocaleLine<String> {
  private final GlobalLineInfo info;
  private final SidebarLineHandler handler;
  private final TeamsPacketAdapter.TeamInfoPacketAdapter<String> packetAdapter;
  private String player, oldPlayer;
  private String prefix, suffix;
  private String currentValue;

  public LegacyLocaleLine(GlobalLineInfo info, SidebarLineHandler handler) {
    this.info = info;
    this.handler = handler;
    this.player = info.player();
    this.packetAdapter = info.packetAdapter().createLegacyTeamInfoAdapter(this);
    packetAdapter.updateTeamPackets(entries());
  }

  public @NotNull GlobalLineInfo info() {
    return info;
  }

  @Override
  public @NotNull String displayName() {
    return "";
  }

  @Override
  public @NotNull String prefix() {
    return prefix;
  }

  @Override
  public @NotNull String suffix() {
    return suffix;
  }

  @Override
  public @NotNull Collection<String> entries() {
    return Set.of(player);
  }

  @Override
  public void value(@NotNull Component renderedComponent) {
    var legacyValue = legacySection().serialize(renderedComponent);

    oldPlayer = player;

    if (legacyValue.length() <= 16) {
      this.prefix = legacyValue;
      this.suffix = "";

      if (this.currentValue != null && this.currentValue.length() > 32) {
        this.player = info.player();
      }
    } else {
      var color = legacyValue.charAt(15) == ChatColor.COLOR_CHAR;

      var prefixEnd = color ? 15 : 16;
      this.prefix = legacyValue.substring(0, prefixEnd);

      this.player = info.player() + ChatColor.RESET
        + ChatColor.getLastColors(prefix +
        ChatColor.COLOR_CHAR + (color ? legacyValue.charAt(16) : ""));

      var playerEnd = prefixEnd;
      if (legacyValue.length() > 32) {
        var remaining = 16 - player.length();
        assert remaining > 0;

        playerEnd += remaining;
        player += legacyValue.substring(prefixEnd, playerEnd);
      }

      this.suffix = legacyValue.substring(playerEnd + (color ? 2 : 0));
      if (suffix.length() > 16) {
        var newSuffix = suffix.substring(0, 16);
        if (newSuffix.endsWith(String.valueOf(ChatColor.COLOR_CHAR)) &&
          ChatColor.getByChar(suffix.charAt(16)) != null) {
          newSuffix = newSuffix.substring(0, 15);
        }

        suffix = newSuffix;
      }
    }

    currentValue = legacyValue;

    if (oldPlayer.equals(player)) {
      oldPlayer = null;
    }
  }

  @Override
  public void updateTeam() {
    var players = handler.players();
    if (oldPlayer != null) {
      packetAdapter.removeEntries(players, Set.of(oldPlayer));
      handler.localeLineHandler().sidebar().packetAdapter().removeLine(players, oldPlayer);
      oldPlayer = null;

      var entries = entries();
      packetAdapter.updateTeamPackets(entries);
      packetAdapter.addEntries(players, entries);
      handler.localeLineHandler().sidebar().packetAdapter().score(players, info.objectiveScore(), player);
    }

    packetAdapter.updateTeam(players);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players) {
    if (oldPlayer != null) {
      handler.localeLineHandler().sidebar().packetAdapter().removeLine(players, oldPlayer);
    }

    handler.localeLineHandler().sidebar().packetAdapter().score(players, info.objectiveScore(), player);
  }

  @Override
  public void show(@NotNull Collection<Player> players) {
    sendScore(players);
    packetAdapter.createTeam(players);
  }

  @Override
  public void hide(@NotNull Collection<Player> players) {
    if (oldPlayer != null) {
      handler.localeLineHandler().sidebar().packetAdapter().removeLine(players, oldPlayer);
    }

    handler.localeLineHandler().sidebar().packetAdapter().removeLine(players, player);
    info.packetAdapter().removeTeam(players);
  }
}

