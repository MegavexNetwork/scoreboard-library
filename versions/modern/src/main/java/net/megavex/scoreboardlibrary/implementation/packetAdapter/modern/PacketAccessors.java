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
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Collection;
import java.util.Optional;

public final class PacketAccessors {
  public static final PacketConstructor<ClientboundSetObjectivePacket> OBJECTIVE_PACKET_CONSTRUCTOR =
    ReflectUtil.findPacketConstructor(ClientboundSetObjectivePacket.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetObjectivePacket.class, "d", String.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, net.minecraft.network.chat.Component> OBJECTIVE_DISPLAY_NAME_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetObjectivePacket.class, "e", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, ObjectiveCriteria.RenderType> OBJECTIVE_RENDER_TYPE_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetObjectivePacket.class, "f", ObjectiveCriteria.RenderType.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetObjectivePacket.class, "g", int.class);

  public static final FieldAccessor<ClientboundSetDisplayObjectivePacket, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.fieldAccessor(ClientboundSetDisplayObjectivePacket.class, "b", String.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, String> SET_OBJECTIVE_NAME =
    ReflectUtil.fieldAccessor(ClientboundSetObjectivePacket.class, "d", String.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, Integer> SET_OBJECTIVE_MODE =
    ReflectUtil.fieldAccessor(ClientboundSetObjectivePacket.class, "g", int.class);

  public static final ConstructorAccessor<ClientboundSetPlayerTeamPacket> TEAM_PACKET_CONSTRUCTOR =
    ReflectUtil.constructorAccessor(ClientboundSetPlayerTeamPacket.class, String.class, int.class, Optional.class, Collection.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, Component> DISPLAY_NAME_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "a", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, net.minecraft.network.chat.Component> PREFIX_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "b", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, net.minecraft.network.chat.Component> SUFFIX_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "c", net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, String> NAME_TAG_VISIBILITY_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "d", String.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, String> COLLISION_RULE_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "e", String.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, ChatFormatting> COLOR_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "f", ChatFormatting.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, Integer> OPTIONS_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.Parameters.class, "g", int.class);
  @SuppressWarnings("rawtypes") // how tf do i fix this
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket, Collection> ENTRIES_FIELD =
    ReflectUtil.fieldAccessor(ClientboundSetPlayerTeamPacket.class, "j", Collection.class);

  private PacketAccessors() {
  }
}
