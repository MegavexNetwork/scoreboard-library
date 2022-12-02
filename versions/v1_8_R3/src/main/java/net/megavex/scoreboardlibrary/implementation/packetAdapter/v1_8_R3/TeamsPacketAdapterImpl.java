package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.entity.Player;


import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;

public class TeamsPacketAdapterImpl extends TeamsPacketAdapter<Packet<PacketListenerPlayOut>, PacketAdapterImpl> {
  private static final Field teamNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "a"),
    teamDisplayNameField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "b"),
    teamPrefixField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "c"),
    teamSuffixField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "d"),
    teamNameTagVisibilityField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "e"),
    teamEntriesField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "g"),
    teamModeField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "h"),
    teamRulesField = UnsafeUtilities.getField(PacketPlayOutScoreboardTeam.class, "i");

  private PacketPlayOutScoreboardTeam removePacket;

  public TeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
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
  public TeamInfoPacketAdapter<Component> createTeamInfoAdapter(ImmutableTeamProperties<Component> properties) {
    return new AdventureTeamInfoPacketAdapter(properties);
  }

  @Override
  public TeamInfoPacketAdapter<String> createLegacyTeamInfoAdapter(ImmutableTeamProperties<String> properties) {
    return new LegacyTeamInfoPacketAdapter(properties);
  }

  private abstract class AbstractTeamInfoPacketAdapter<C> extends TeamInfoPacketAdapter<C> {
    public AbstractTeamInfoPacketAdapter(ImmutableTeamProperties<C> properties) {
      super(properties);
    }

    @Override
    public void addEntries(Collection<Player> players, Collection<String> entries) {
      sendTeamEntryPacket(players, entries, MODE_ADD_ENTRIES);
    }

    @Override
    public void removeEntries(Collection<Player> players, Collection<String> entries) {
      sendTeamEntryPacket(players, entries, MODE_REMOVE_ENTRIES);
    }

    @Override
    public void createTeam(Collection<Player> players) {
      sendTeamPacket(players, false);
    }

    @Override
    public void updateTeam(Collection<Player> players) {
      sendTeamPacket(players, true);
    }

    private void sendTeamEntryPacket(Collection<Player> players, Collection<String> entries, int action) {
      var packet = new PacketPlayOutScoreboardTeam();
      UnsafeUtilities.setField(teamNameField, packet, teamName);
      UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(teamModeField), action);
      UnsafeUtilities.setField(teamEntriesField, packet, entries);
      impl.sendPacket(players, packet);
    }

    private void sendTeamPacket(Collection<Player> players, boolean update) {
      LocalePacketUtilities.sendLocalePackets(impl.localeProvider, null, impl, players, locale -> {
        var displayName = limitLegacyText(toLegacy(properties.displayName(), locale), TeamsPacketAdapter.LEGACY_CHARACTER_LIMIT);
        var prefix = limitLegacyText(toLegacy(properties.prefix(), locale), TeamsPacketAdapter.LEGACY_CHARACTER_LIMIT);
        var suffix = limitLegacyText(toLegacy(properties.suffix(), locale), TeamsPacketAdapter.LEGACY_CHARACTER_LIMIT);

        var packet = new PacketPlayOutScoreboardTeam();
        UnsafeUtilities.setField(teamNameField, packet, teamName);
        UnsafeUtilities.UNSAFE.putInt(packet, UnsafeUtilities.UNSAFE.objectFieldOffset(teamModeField), update ? MODE_UPDATE : MODE_CREATE);
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

  private class AdventureTeamInfoPacketAdapter extends AbstractTeamInfoPacketAdapter<Component> {
    public AdventureTeamInfoPacketAdapter(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    protected String toLegacy(Component component, Locale locale) {
      return LegacyFormatUtil.serialize(component, locale);
    }
  }

  private class LegacyTeamInfoPacketAdapter extends AbstractTeamInfoPacketAdapter<String> {

    public LegacyTeamInfoPacketAdapter(ImmutableTeamProperties<String> properties) {
      super(properties);
    }

    @Override
    protected String toLegacy(String component, Locale locale) {
      return component;
    }
  }
}
