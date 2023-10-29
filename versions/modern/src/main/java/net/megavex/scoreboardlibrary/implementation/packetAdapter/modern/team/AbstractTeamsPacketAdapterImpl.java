package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.PacketConstructor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public abstract class AbstractTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  static final PacketConstructor<Parameters> parametersConstructor =
    ReflectUtil.findPacketConstructor(Parameters.class);

  protected final PacketSender<Packet<?>> sender;
  protected final ComponentProvider componentProvider;
  protected final String teamName;
  private ClientboundSetPlayerTeamPacket removePacket;

  public AbstractTeamsPacketAdapterImpl(@NotNull PacketSender<Packet<?>> sender, @NotNull ComponentProvider componentProvider, @NotNull String teamName) {
    this.sender = sender;
    this.componentProvider = componentProvider;
    this.teamName = teamName;
  }

  public static ClientboundSetPlayerTeamPacket createTeamsPacket(
    int method,
    @NotNull String name,
    @Nullable Parameters parameters,
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
    sender.sendPacket(players, removePacket);
  }

  public abstract class TeamDisplayPacketAdapterImpl implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<Component> properties;

    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      this.properties = properties;
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      sender.sendPacket(players, createTeamsPacket(TeamConstants.mode(packetType), teamName, null, entries));
    }

    protected void fillTeamPacket(ClientboundSetPlayerTeamPacket packet, Collection<String> entries) {
      PacketAccessors.ENTRIES_FIELD.set(packet, entries);
    }

    protected void fillParameters(@NotNull Parameters parameters, @UnknownNullability Locale locale) {
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
