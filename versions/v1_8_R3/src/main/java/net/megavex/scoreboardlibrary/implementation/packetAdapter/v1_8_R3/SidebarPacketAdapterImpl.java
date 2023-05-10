package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.Component.empty;

public class SidebarPacketAdapterImpl extends SidebarPacketAdapter<Packet<PacketListenerPlayOut>, PacketAdapterImpl> {
  private static final Field objectiveNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "a"),
    objectiveDisplayNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "b"),
    objectiveHealthDisplayField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "c"),
    scoreNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "a"),
    scoreObjectiveNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "b"),
    scoreScoreField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "c"),
    scoreActionField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "d");

  private final PacketPlayOutScoreboardObjective createPacket, updatePacket;

  SidebarPacketAdapterImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);

    var locale = sidebar.locale();
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
    var locale = sidebar().locale();
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
      LocalePacketUtilities.sendLocalePackets(packetAdapter().localeProvider, sidebar().locale(), packetAdapter(), players, locale -> {
        var packet = new PacketPlayOutScoreboardObjective();
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
    var packet = new PacketPlayOutScoreboardScore();
    UnsafeUtilities.setField(scoreNameField, packet, line);
    UnsafeUtilities.setField(scoreObjectiveNameField, packet, packetAdapter().objectiveName);
    UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(scoreScoreField), score);
    UnsafeUtilities.setField(scoreActionField, packet, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
    packetAdapter().sendPacket(players, packet);
  }

  private void createObjectivePacket(PacketPlayOutScoreboardObjective packet, int mode, Component displayName, Locale locale) {
    UnsafeUtilities.setField(objectiveNameField, packet, packetAdapter().objectiveName);
    UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(PacketAdapterImpl.objectiveModeField), mode);
    UnsafeUtilities.setField(objectiveHealthDisplayField, packet, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    updateDisplayName(packet, displayName, locale);
  }

  private void updateDisplayName(PacketPlayOutScoreboardObjective packet, Component displayName, Locale locale) {
    var value = LegacyFormatUtil.serialize(displayName, locale);
    UnsafeUtilities.setField(objectiveDisplayNameField, packet, value);
  }
}
