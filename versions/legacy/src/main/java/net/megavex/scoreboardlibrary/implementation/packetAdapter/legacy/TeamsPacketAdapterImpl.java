package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

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
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.OtherAccessors.ENUM_CHAT_FORMAT_B;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.OtherAccessors.ENUM_CHAT_FORMAT_B_STATIC;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.PacketAccessors.TEAM_CLASS;

public class TeamsPacketAdapterImpl implements TeamsPacketAdapter {

  private static final ConstructorAccessor<?> TEAM_CONSTRUCTOR = ReflectUtil.findConstructor(TEAM_CLASS).get();
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
      removePacket = TEAM_CONSTRUCTOR.invoke();
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
      Object packet = TEAM_CONSTRUCTOR.invoke();
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

          Object packet = TEAM_CONSTRUCTOR.invoke();
          PacketAccessors.TEAM_NAME_FIELD.set(packet, teamName);
          PacketAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
          PacketAccessors.TEAM_DISPLAY_NAME_FIELD.set(packet, displayName);
          PacketAccessors.TEAM_PREFIX_FIELD.set(packet, prefix);
          PacketAccessors.TEAM_SUFFIX_FIELD.set(packet, suffix);
          if (PacketAccessors.TEAM_NAME_TAG_VISIBILITY_FIELD != null) {
            PacketAccessors.TEAM_NAME_TAG_VISIBILITY_FIELD.set(packet, properties.nameTagVisibility().key());
          }

          NamedTextColor color = properties.playerColor();
          if (color != null) {
            String name = NamedTextColor.NAMES.key(color);
            // NOTE: UNTESTED FOR NOW !
            // Note 2: very high chance this changes between versions. Should check if possible.
            // Original code: `Integer teamColorField = Objects.requireNonNull(EnumChatFormat.b(name)).b()`
            Object enumChatFormatInstance = Objects.requireNonNull(RandomUtils.invokeStaticMethod(
              ENUM_CHAT_FORMAT_B_STATIC,
              new Object[]{name}
            ));

            Integer teamColorField = (Integer)RandomUtils.invokeMethod(
              enumChatFormatInstance,
              ENUM_CHAT_FORMAT_B,
              null
            );
            System.out.println("If you see this, I did my job properly (temporary)");

            PacketAccessors.TEAM_COLOR_FIELD.set(packet, teamColorField);
          }

          if (PacketAccessors.TEAM_RULES_FIELD != null) {
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
