package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R2.team;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R2.PacketAdapterImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities.getField;

public abstract class AbstractTeamsPacketAdapterImpl extends TeamsPacketAdapter<Packet<?>, PacketAdapterImpl> {
  protected static final Field displayNameField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "a"),
    prefixField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "b"),
    suffixField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "c"),
    nameTagVisibilityField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "d"),
    collisionRuleField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "e"),
    colorField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "f"),
    optionsField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "g"),
    entriesField = getField(ClientboundSetPlayerTeamPacket.class, "j");
  private static final Constructor<ClientboundSetPlayerTeamPacket> teamPacketConstructor;

  static {
    try {
      teamPacketConstructor = ClientboundSetPlayerTeamPacket.class.getDeclaredConstructor(String.class, int.class, Optional.class, Collection.class);
      teamPacketConstructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  protected ClientboundSetPlayerTeamPacket removePacket;

  AbstractTeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
    super(impl, teamName);
  }

  public static ClientboundSetPlayerTeamPacket createTeamsPacket(
    int method,
    String name,
    ClientboundSetPlayerTeamPacket.Parameters parameters,
    Collection<String> entries
  ) {
    try {
      return teamPacketConstructor.newInstance(name, method, Optional.ofNullable(parameters), entries == null ? List.of() : entries);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = createTeamsPacket(MODE_REMOVE, teamName(), null, null);
    }
    packetAdapter().sendPacket(players, removePacket);
  }

  abstract class TeamInfoPacketAdapterImpl extends TeamInfoPacketAdapter<Component> {
    static final UnsafeUtilities.PacketConstructor<ClientboundSetPlayerTeamPacket.Parameters> parametersConstructor =
      UnsafeUtilities.findPacketConstructor(ClientboundSetPlayerTeamPacket.Parameters.class, MethodHandles.lookup());

    public TeamInfoPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
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
        UnsafeUtilities.setField(entriesField, packet, entries);
      }
    }

    protected void fillParameters(ClientboundSetPlayerTeamPacket.Parameters parameters, Locale locale) {
      var nameTagVisibilityKey = properties.nameTagVisibility().key();
      if (!Objects.equals(parameters.getNametagVisibility(), nameTagVisibilityKey)) {
        UnsafeUtilities.setField(nameTagVisibilityField, parameters, nameTagVisibilityKey);
      }

      var collisionRuleKey = properties.collisionRule().key();
      if (!Objects.equals(parameters.getCollisionRule(), collisionRuleKey)) {
        UnsafeUtilities.setField(collisionRuleField, parameters, collisionRuleKey);
      }

      var c = LegacyFormatUtil.getChar(properties.playerColor());
      if (parameters.getColor() == null || parameters.getColor().code != c) {
        UnsafeUtilities.setField(colorField, parameters, ChatFormatting.getByCode(c));
      }

      var options = properties.packOptions();
      if (parameters.getOptions() != options) {
        UnsafeUtilities.UNSAFE.putInt(parameters, UnsafeUtilities.UNSAFE.objectFieldOffset(optionsField), options);
      }
    }
  }
}
