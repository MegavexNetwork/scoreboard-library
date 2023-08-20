package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.PacketConstructor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTeamsPacketAdapterImpl extends TeamsPacketAdapter<Packet<?>, PacketAdapterImpl> {
  static final PacketConstructor<Parameters> parametersConstructor =
    ReflectUtil.findPacketConstructor(Parameters.class);

  protected ClientboundSetPlayerTeamPacket removePacket;

  AbstractTeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
    super(impl, teamName);
  }

  public static ClientboundSetPlayerTeamPacket createTeamsPacket(
    int method,
    String name,
    Parameters parameters,
    Collection<String> entries
  ) {
    return PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(name, method, Optional.ofNullable(parameters), entries == null ? Collections.emptyList() : entries);
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = createTeamsPacket(MODE_REMOVE, teamName(), null, null);
    }
    packetAdapter().sendPacket(players, removePacket);
  }

  abstract class TeamDisplayPacketAdapterImpl extends TeamDisplayPacketAdapter<Component> {
    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void addEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      teamEntry(players, entries, MODE_ADD_ENTRIES);
    }

    @Override
    public void removeEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      teamEntry(players, entries, MODE_REMOVE_ENTRIES);
    }

    private void teamEntry(Collection<Player> players, Collection<String> entries, int action) {
      packetAdapter().sendPacket(players, createTeamsPacket(action, teamName(), null, entries));
    }

    protected void fillTeamPacket(ClientboundSetPlayerTeamPacket packet, Collection<String> entries) {
      if (packet.getPlayers() != entries) {
        PacketAccessors.ENTRIES_FIELD.set(packet, entries);
      }
    }

    protected void fillParameters(Parameters parameters, Locale locale) {
      String nameTagVisibilityKey = properties.nameTagVisibility().key();
      if (!Objects.equals(parameters.getNametagVisibility(), nameTagVisibilityKey)) {
        PacketAccessors.NAME_TAG_VISIBILITY_FIELD.set(parameters, nameTagVisibilityKey);
      }

      String collisionRuleKey = properties.collisionRule().key();
      if (!Objects.equals(parameters.getCollisionRule(), collisionRuleKey)) {
        PacketAccessors.COLLISION_RULE_FIELD.set(parameters, collisionRuleKey);
      }

      char legacyChar = LegacyFormatUtil.getChar(properties.playerColor());
      if (parameters.getColor() == null || parameters.getColor().getChar() != legacyChar) {
        PacketAccessors.COLOR_FIELD.set(parameters, ChatFormatting.getByCode(legacyChar));
      }

      int options = properties.packOptions();
      if (parameters.getOptions() != options) {
        PacketAccessors.OPTIONS_FIELD.set(parameters, options);
      }
    }
  }
}
