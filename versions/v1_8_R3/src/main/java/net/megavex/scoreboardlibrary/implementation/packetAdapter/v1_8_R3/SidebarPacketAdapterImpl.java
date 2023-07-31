package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore.EnumScoreboardAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.Component.empty;

public class SidebarPacketAdapterImpl extends SidebarPacketAdapter<Packet<PacketListenerPlayOut>, PacketAdapterImpl> {
  private final PacketPlayOutScoreboardObjective createPacket, updatePacket;

  SidebarPacketAdapterImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);

    Locale locale = sidebar.locale();
    if (locale != null) {
      this.createPacket = new PacketPlayOutScoreboardObjective();
      this.updatePacket = new PacketPlayOutScoreboardObjective();
      createObjectivePacket(this.createPacket, MODE_CREATE, empty(), locale);
      createObjectivePacket(this.updatePacket, MODE_UPDATE, empty(), locale);
    } else {
      this.createPacket = null;
      this.updatePacket = null;
    }
  }

  @Override
  public void updateTitle(@NotNull Component displayName) {
    Locale locale = sidebar().locale();
    if (locale != null) {
      updateDisplayName(createPacket, displayName, locale);
      updateDisplayName(updatePacket, displayName, locale);
    }
  }

  @Override
  public void sendObjectivePacket(@NotNull Collection<Player> players, @NotNull ObjectivePacket type) {
    if (sidebar().locale() != null) {
      packetAdapter().sendPacket(players, type == ObjectivePacket.CREATE ? createPacket : updatePacket);
    } else {
      LocalePacketUtil.sendLocalePackets(packetAdapter().localeProvider, sidebar().locale(), packetAdapter(), players, locale -> {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        createObjectivePacket(packet, type == ObjectivePacket.CREATE ? MODE_CREATE : MODE_UPDATE, sidebar().title(), locale);
        return packet;
      });
    }
  }

  @Override
  public void removeLine(@NotNull Collection<Player> players, @NotNull String line) {
    packetAdapter().sendPacket(players, new PacketPlayOutScoreboardScore(line));
  }

  @Override
  public void score(@NotNull Collection<Player> players, int score, @NotNull String line) {
    PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
    PacketAccessors.SCORE_NAME_FIELD.set(packet, line);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, packetAdapter().objectiveName);
    PacketAccessors.SCORE_VALUE_FIELD.set(packet, score);
    PacketAccessors.SCORE_ACTION_FIELD.set(packet, EnumScoreboardAction.CHANGE);
    packetAdapter().sendPacket(players, packet);
  }

  private void createObjectivePacket(PacketPlayOutScoreboardObjective packet, int mode, Component displayName, Locale locale) {
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, packetAdapter().objectiveName);
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, mode);
    PacketAccessors.OBJECTIVE_HEALTH_DISPLAY_FIELD.set(packet, EnumScoreboardHealthDisplay.INTEGER);
    updateDisplayName(packet, displayName, locale);
  }

  private void updateDisplayName(PacketPlayOutScoreboardObjective packet, Component displayName, Locale locale) {
    String value = LegacyFormatUtil.limitLegacyText(LegacyFormatUtil.serialize(displayName, locale), LEGACY_TITLE_CHARACTER_LIMIT);
    PacketAccessors.OBJECTIVE_DISPLAY_NAME_FIELD.set(packet, value);
  }
}
