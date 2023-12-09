package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.PacketConstructor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Collection;
import java.util.Optional;

public final class PacketAccessors {
  public static final PacketConstructor<ClientboundSetObjectivePacket> OBJECTIVE_PACKET_CONSTRUCTOR =
    ReflectUtil.findEmptyConstructor(ClientboundSetObjectivePacket.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, "d", String.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, net.minecraft.network.chat.Component> OBJECTIVE_VALUE_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, "e", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, ObjectiveCriteria.RenderType> OBJECTIVE_RENDER_TYPE_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, "f", ObjectiveCriteria.RenderType.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, new String[]{"g", "h"}, int.class);

  public static final ConstructorAccessor<ClientboundSetDisplayObjectivePacket> DISPLAY_LEGACY_CONSTRUCTOR =
    ReflectUtil.findConstructor(ClientboundSetDisplayObjectivePacket.class, int.class, Objective.class);
  public static final FieldAccessor<ClientboundSetDisplayObjectivePacket, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findField(ClientboundSetDisplayObjectivePacket.class, "b", String.class);

  public static final ConstructorAccessor<ClientboundSetScorePacket> SCORE_LEGACY_CONSTRUCTOR =
    ReflectUtil.findConstructor(ClientboundSetScorePacket.class, ServerScoreboard.Method.class, String.class, String.class, int.class);

  public static final ConstructorAccessor<ClientboundSetPlayerTeamPacket> TEAM_PACKET_CONSTRUCTOR =
    ReflectUtil.findConstructorOrThrow(ClientboundSetPlayerTeamPacket.class, String.class, int.class, Optional.class, Collection.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, Component> DISPLAY_NAME_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "a", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, net.minecraft.network.chat.Component> PREFIX_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "b", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, net.minecraft.network.chat.Component> SUFFIX_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "c", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, String> NAME_TAG_VISIBILITY_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "d", String.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, String> COLLISION_RULE_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "e", String.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, ChatFormatting> COLOR_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "f", ChatFormatting.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, Integer> OPTIONS_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, "g", int.class);
  @SuppressWarnings("rawtypes")
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket, Collection> ENTRIES_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.class, "j", Collection.class);

  private PacketAccessors() {
  }
}
