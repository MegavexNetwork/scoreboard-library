package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections.OtherAccessors.enumChatFormatBMethod;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections.OtherAccessors.enumChatFormatBStaticMethod;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections.PacketAccessors.packetPlayOutScoreboardTeamClass;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacyreflections.RandomUtils.is1_8Plus;

public class TeamsPacketAdapterImpl implements TeamsPacketAdapter {

  private static final ConstructorAccessor<Object> packetPlayOutScoreboardTeamConstructor = ReflectUtil.findConstructorOrThrow(packetPlayOutScoreboardTeamClass);
  private final PacketSender<Object> sender;
  private final String teamName;
  private Object removePacket;

  public TeamsPacketAdapterImpl(@NotNull PacketSender<Object> sender, @NotNull String teamName) {
    this.sender = sender;
    this.teamName = teamName;
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = packetPlayOutScoreboardTeamConstructor.invoke();
      PacketAccessors.TEAM_NAME_FIELD.set(removePacket, teamName);
      PacketAccessors.TEAM_MODE_FIELD.set(removePacket, TeamConstants.MODE_REMOVE);
    }

    sender.sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new AdventureTeamDisplayPacketAdapter(properties);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyTeamDisplayPacketAdapter(properties);
  }

  private abstract class AbstractTeamDisplayPacketAdapter<C> implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<C> properties;

    public AbstractTeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<C> properties) {
      this.properties = properties;
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      Object packet = packetPlayOutScoreboardTeamConstructor.invoke();
      PacketAccessors.TEAM_NAME_FIELD.set(packet, teamName);
      PacketAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
      PacketAccessors.TEAM_ENTRIES_FIELD.set(packet, entries);
      sender.sendPacket(players, packet);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      LocalePacketUtil.sendLocalePackets(
        sender,
        players,
        locale -> {
          String displayName = limitLegacyText(toLegacy(properties.displayName(), locale), TeamConstants.LEGACY_CHAR_LIMIT);
          String prefix = limitLegacyText(toLegacy(properties.prefix(), locale), TeamConstants.LEGACY_CHAR_LIMIT);
          String suffix = limitLegacyText(toLegacy(properties.suffix(), locale), TeamConstants.LEGACY_CHAR_LIMIT);

          Object packet = packetPlayOutScoreboardTeamConstructor.invoke();
          PacketAccessors.TEAM_NAME_FIELD.set(packet, teamName);
          PacketAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
          PacketAccessors.TEAM_DISPLAY_NAME_FIELD.set(packet, displayName);
          PacketAccessors.TEAM_PREFIX_FIELD.set(packet, prefix);
          PacketAccessors.TEAM_SUFFIX_FIELD.set(packet, suffix);
          if (is1_8Plus) {
            PacketAccessors.TEAM_NAME_TAG_VISIBILITY_FIELD.set(packet, properties.nameTagVisibility().key());
          }

          NamedTextColor color = properties.playerColor();
          if (color != null) {
            String name = NamedTextColor.NAMES.key(color);
            // NOTE: UNTESTED FOR NOW !
            // Note 2: very high chance this changes between versions. Should check if possible.
            // Original code: `Integer teamColorField = Objects.requireNonNull(EnumChatFormat.b(name)).b()`
            Object enumChatFormatInstance = Objects.requireNonNull(RandomUtils.invokeStaticMethod(
              enumChatFormatBStaticMethod,
              new Object[]{name}
            ));

            Integer teamColorField = (Integer)RandomUtils.invokeMethod(
              enumChatFormatInstance,
              enumChatFormatBMethod,
              null
            );
            System.out.println("If you see this, I did my job properly (temporary)");

            PacketAccessors.TEAM_COLOR_FIELD.set(packet, teamColorField);
          }

          if (is1_8Plus) {
            PacketAccessors.TEAM_RULES_FIELD.set(packet, properties.packOptions());
          }

          if (packetType == PropertiesPacketType.CREATE) {
            PacketAccessors.TEAM_ENTRIES_FIELD.set(packet, ImmutableList.copyOf(properties.syncedEntries()));
          }

          return packet;
        }
      );
    }

    protected abstract @NotNull String toLegacy(@NotNull C component, @NotNull Locale locale);
  }

  private class AdventureTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<Component> {
    public AdventureTeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    protected @NotNull String toLegacy(@NotNull Component component, @NotNull Locale locale) {
      return LegacyFormatUtil.serialize(component, locale);
    }
  }

  private class LegacyTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<String> {
    public LegacyTeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<String> properties) {
      super(properties);
    }

    @Override
    protected @NotNull String toLegacy(@NotNull String component, @NotNull Locale locale) {
      return component;
    }
  }
}
