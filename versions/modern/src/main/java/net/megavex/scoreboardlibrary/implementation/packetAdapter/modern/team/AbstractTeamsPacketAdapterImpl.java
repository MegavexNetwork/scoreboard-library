package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.minecraft.ChatFormatting;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

public abstract class AbstractTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  protected final String teamName;
  private Object removePacket;

  public AbstractTeamsPacketAdapterImpl(@NotNull String teamName) {
    this.teamName = teamName;
  }

  public static Object createTeamsPacket(
    int method,
    @NotNull String name,
    @Nullable Object parameters,
    @Nullable Collection<String> entries
  ) {
    return PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
      name,
      method,
      Optional.ofNullable(parameters),
      entries == null ? Collections.emptyList() : entries
    );
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = createTeamsPacket(TeamConstants.MODE_REMOVE, teamName, null, null);
    }
    ModernPacketSender.INSTANCE.sendPacket(players, removePacket);
  }

  public abstract class TeamDisplayPacketAdapterImpl implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<Component> properties;

    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      this.properties = properties;
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      ModernPacketSender.INSTANCE.sendPacket(players, createTeamsPacket(TeamConstants.mode(packetType), teamName, null, entries));
    }

    protected void fillParameters(Object parameters, @UnknownNullability Locale locale) {
      String nameTagVisibilityKey = properties.nameTagVisibility().key();
      PacketAccessors.NAME_TAG_VISIBILITY_FIELD.set(parameters, nameTagVisibilityKey);

      String collisionRuleKey = properties.collisionRule().key();
      PacketAccessors.COLLISION_RULE_FIELD.set(parameters, collisionRuleKey);

      char legacyChar = LegacyFormatUtil.getChar(properties.playerColor());
      PacketAccessors.COLOR_FIELD.set(parameters, ChatFormatting.getByCode(legacyChar));

      int options = properties.packOptions();
      PacketAccessors.OPTIONS_FIELD.set(parameters, options);
    }
  }
}
