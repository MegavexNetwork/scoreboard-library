package net.megavex.scoreboardlibrary.internal.nms.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.entity.Player;


import static net.megavex.scoreboardlibrary.internal.nms.base.util.LegacyFormatUtil.limitLegacyText;

public class TeamNMSImpl extends TeamNMS<Packet<?>, NMSImpl> {
  private static final Field teamNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "a"),
    teamDisplayNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "b"),
    teamPrefixField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "c"),
    teamSuffixField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "d"),
    teamNameTagVisibilityField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "e"),
    teamEntriesField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "g"),
    teamModeField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "h"),
    teamRulesField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "i");

  protected PacketPlayOutScoreboardTeam removePacket;

  public TeamNMSImpl(NMSImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public void removeTeam(Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = new PacketPlayOutScoreboardTeam();
      UnsafeUtilities.setField(teamNameField, removePacket, teamName);
      UnsafeUtilities.UNSAFE.putInt(removePacket, UnsafeUtilities.UNSAFE.objectFieldOffset(teamModeField), MODE_REMOVE);
    }

    impl.sendPacket(players, removePacket);
  }

  @Override
  public TeamNMS.TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
    return new AdventureTeamInfoNMS(properties, componentTranslator);
  }

  @Override
  public TeamInfoNMS<String> createLegacyTeamInfoNMS(ImmutableTeamProperties<String> properties) {
    return new LegacyTeamInfoNMS(properties);
  }

  private abstract class AbstractTeamInfoNMS<C> extends TeamNMS.TeamInfoNMS<C> {
    public AbstractTeamInfoNMS(ImmutableTeamProperties<C> properties) {
      super(properties);
    }

    @Override
    public void addEntries(Collection<Player> players, Collection<String> entries) {
      teamEntry(players, entries, MODE_ADD_ENTRIES);
    }

    @Override
    public void removeEntries(Collection<Player> players, Collection<String> entries) {
      teamEntry(players, entries, MODE_REMOVE_ENTRIES);
    }

    protected void teamEntry(Collection<Player> players, Collection<String> entries, int action) {
      var packet = new PacketPlayOutScoreboardTeam();
      UnsafeUtilities.setField(teamNameField, packet, teamName);
      UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(teamModeField), action);
      UnsafeUtilities.setField(teamEntriesField, packet, entries);
      impl.sendPacket(players, packet);
    }

    @Override
    public void createTeam(Collection<Player> players) {
      sendTeamPacket(false, players);
    }

    @Override
    public void updateTeam(Collection<Player> players) {
      sendTeamPacket(true, players);
    }

    private void sendTeamPacket(boolean update, Collection<Player> players) {
      ScoreboardManagerNMS.sendLocalePackets(null, impl, players, locale -> {
        var displayName = limitLegacyText(toLegacy(properties.displayName(), locale), TeamNMS.LEGACY_CHARACTER_LIMIT);
        var prefix = limitLegacyText(toLegacy(properties.prefix(), locale), TeamNMS.LEGACY_CHARACTER_LIMIT);
        var suffix = limitLegacyText(toLegacy(properties.suffix(), locale), TeamNMS.LEGACY_CHARACTER_LIMIT);

        var packet = new PacketPlayOutScoreboardTeam();
        UnsafeUtilities.setField(teamNameField, packet, teamName);
        UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(teamModeField), update ? MODE_UPDATE:MODE_CREATE);
        UnsafeUtilities.setField(teamDisplayNameField, packet, displayName);
        UnsafeUtilities.setField(teamPrefixField, packet, prefix);
        UnsafeUtilities.setField(teamSuffixField, packet, suffix);
        UnsafeUtilities.setField(teamNameTagVisibilityField, packet, properties.nameTagVisibility().key());
        UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(teamRulesField), properties.packOptions());
        if (!update) {
          UnsafeUtilities.setField(teamEntriesField, packet, properties.entries());
        }

        return packet;
      });
    }

    protected abstract String toLegacy(C component, Locale locale);
  }

  private class AdventureTeamInfoNMS extends AbstractTeamInfoNMS<Component> {

    private final ComponentTranslator componentTranslator;

    public AdventureTeamInfoNMS(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
      super(properties);
      this.componentTranslator = componentTranslator;
    }

    @Override
    protected String toLegacy(Component component, Locale locale) {
      return LegacyFormatUtil.serialize(componentTranslator, component, locale);
    }
  }

  private class LegacyTeamInfoNMS extends AbstractTeamInfoNMS<String> {

    public LegacyTeamInfoNMS(ImmutableTeamProperties<String> properties) {
      super(properties);
    }

    @Override
    protected String toLegacy(String component, Locale locale) {
      return component;
    }
  }
}
