package net.megavex.scoreboardlibrary.implementation.nms.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.nms.base.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.nms.base.util.LocalePacketUtilities;
import net.megavex.scoreboardlibrary.implementation.nms.base.util.UnsafeUtilities;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.entity.Player;


import static net.kyori.adventure.text.Component.empty;

public class SidebarPacketAdapterImpl extends SidebarPacketAdapter<Packet<?>, NMSImpl> {
  private static final Field objectiveNameField,
    objectiveDisplayNameField,
    objectiveHealthDisplayField,
    scoreNameField,
    scoreObjectiveNameField,
    scoreScoreField,
    scoreActionField;

  static {
    objectiveNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "a");
    objectiveDisplayNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "b");
    objectiveHealthDisplayField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "c");
    scoreNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "a");
    scoreObjectiveNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "b");
    scoreScoreField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "c");
    scoreActionField = UnsafeUtilities.getField(PacketPlayOutScoreboardScore.class, "d");
  }

  private final PacketPlayOutScoreboardObjective createPacket, updatePacket;

  SidebarPacketAdapterImpl(NMSImpl impl, Sidebar sidebar) {
    super(impl, sidebar);

    var locale = sidebar.locale();
    if (locale != null) {
      this.createPacket = new PacketPlayOutScoreboardObjective();
      this.updatePacket = new PacketPlayOutScoreboardObjective();
      createObjectivePacket(this.createPacket, 0, empty(), locale);
      createObjectivePacket(this.updatePacket, 2, empty(), locale);
    } else {
      this.createPacket = null;
      this.updatePacket = null;
    }
  }

  @Override
  public void updateTitle(Component displayName) {
    var locale = sidebar.locale();
    if (locale != null) {
      updateDisplayName(createPacket, displayName, locale);
      updateDisplayName(updatePacket, displayName, locale);
    }
  }

  @Override
  protected void sendObjectivePacket(Collection<Player> players, boolean create) {
    if (sidebar.locale() != null) {
      impl.sendPacket(players, create ? createPacket:updatePacket);
    } else {
      LocalePacketUtilities.sendLocalePackets(impl.localeProvider, sidebar.locale(), impl, players, locale -> {
        var packet = new PacketPlayOutScoreboardObjective();
        createObjectivePacket(packet, create ? 0:2, sidebar.title(), locale);
        return packet;
      });
    }
  }

  @Override
  public void removeLine(Collection<Player> players, String line) {
    impl.sendPacket(players, new PacketPlayOutScoreboardScore(line));
  }

  @Override
  public void score(Collection<Player> players, int score, String line) {
    var packet = new PacketPlayOutScoreboardScore();
    UnsafeUtilities.setField(scoreNameField, packet, line);
    UnsafeUtilities.setField(scoreObjectiveNameField, packet, impl.objectiveName);
    UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(scoreScoreField), score);
    UnsafeUtilities.setField(scoreActionField, packet, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
    impl.sendPacket(players, packet);
  }

  private void createObjectivePacket(PacketPlayOutScoreboardObjective packet, int mode, Component displayName, Locale locale) {
    UnsafeUtilities.setField(objectiveNameField, packet, impl.objectiveName);
    UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(NMSImpl.objectiveModeField), mode);
    UnsafeUtilities.setField(objectiveHealthDisplayField, packet, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    updateDisplayName(packet, displayName, locale);
  }

  private void updateDisplayName(PacketPlayOutScoreboardObjective packet, Component displayName, Locale locale) {
    var value = LegacyFormatUtil.serialize(sidebar.componentTranslator(), displayName, locale);
    UnsafeUtilities.setField(objectiveDisplayNameField, packet, value);
  }
}
