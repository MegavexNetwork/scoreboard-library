package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtil;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;

public class TeamsPacketAdapterImpl extends TeamsPacketAdapter<Packet<PacketListenerPlayOut>, PacketAdapterImpl> {
  private static final Field teamNameField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "a"),
    teamDisplayNameField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "b"),
    teamPrefixField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "c"),
    teamSuffixField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "d"),
    teamNameTagVisibilityField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "e"),
    teamEntriesField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "g"),
    teamModeField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "h"),
    teamRulesField = UnsafeUtil.getField(PacketPlayOutScoreboardTeam.class, "i");

  private PacketPlayOutScoreboardTeam removePacket;

  public TeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = new PacketPlayOutScoreboardTeam();
      UnsafeUtil.setField(teamNameField, removePacket, teamName());
      UnsafeUtil.UNSAFE.putInt(removePacket, UnsafeUtil.UNSAFE.objectFieldOffset(teamModeField), MODE_REMOVE);
    }

    packetAdapter().sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamsPacketAdapter.TeamDisplayPacketAdapter<Component> createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new AdventureTeamDisplayPacketAdapter(properties);
  }

  @Override
  public @NotNull TeamsPacketAdapter.TeamDisplayPacketAdapter<String> createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyTeamDisplayPacketAdapter(properties);
  }

  private abstract class AbstractTeamDisplayPacketAdapter<C> extends TeamDisplayPacketAdapter<C> {
    public AbstractTeamDisplayPacketAdapter(ImmutableTeamProperties<C> properties) {
      super(properties);
    }

    @Override
    public void addEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      sendTeamEntryPacket(players, entries, MODE_ADD_ENTRIES);
    }

    @Override
    public void removeEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      sendTeamEntryPacket(players, entries, MODE_REMOVE_ENTRIES);
    }

    @Override
    public void createTeam(@NotNull Collection<Player> players) {
      sendTeamPacket(players, false);
    }

    @Override
    public void updateTeam(@NotNull Collection<Player> players) {
      sendTeamPacket(players, true);
    }

    private void sendTeamEntryPacket(Collection<Player> players, Collection<String> entries, int action) {
      PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
      UnsafeUtil.setField(teamNameField, packet, teamName());
      UnsafeUtil.UNSAFE.putInt(packet, UnsafeUtil.UNSAFE.objectFieldOffset(teamModeField), action);
      UnsafeUtil.setField(teamEntriesField, packet, entries);
      packetAdapter().sendPacket(players, packet);
    }

    private void sendTeamPacket(Collection<Player> players, boolean update) {
      LocalePacketUtil.sendLocalePackets(packetAdapter().localeProvider, null, packetAdapter(), players, locale -> {
        String displayName = limitLegacyText(toLegacy(properties.displayName(), locale), TeamsPacketAdapter.LEGACY_CHARACTER_LIMIT);
        String prefix = limitLegacyText(toLegacy(properties.prefix(), locale), TeamsPacketAdapter.LEGACY_CHARACTER_LIMIT);
        String suffix = limitLegacyText(toLegacy(properties.suffix(), locale), TeamsPacketAdapter.LEGACY_CHARACTER_LIMIT);

        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        UnsafeUtil.setField(teamNameField, packet, teamName());
        UnsafeUtil.UNSAFE.putInt(packet, UnsafeUtil.UNSAFE.objectFieldOffset(teamModeField), update ? MODE_UPDATE : MODE_CREATE);
        UnsafeUtil.setField(teamDisplayNameField, packet, displayName);
        UnsafeUtil.setField(teamPrefixField, packet, prefix);
        UnsafeUtil.setField(teamSuffixField, packet, suffix);
        UnsafeUtil.setField(teamNameTagVisibilityField, packet, properties.nameTagVisibility().key());
        UnsafeUtil.UNSAFE.putInt(packet, UnsafeUtil.UNSAFE.objectFieldOffset(teamRulesField), properties.packOptions());
        if (!update) {
          UnsafeUtil.setField(teamEntriesField, packet, properties.entries());
        }

        return packet;
      });
    }

    protected abstract String toLegacy(C component, Locale locale);
  }

  private class AdventureTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<Component> {
    public AdventureTeamDisplayPacketAdapter(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    protected String toLegacy(Component component, Locale locale) {
      return LegacyFormatUtil.serialize(component, locale);
    }
  }

  private class LegacyTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<String> {

    public LegacyTeamDisplayPacketAdapter(ImmutableTeamProperties<String> properties) {
      super(properties);
    }

    @Override
    protected String toLegacy(String component, Locale locale) {
      return component;
    }
  }
}
