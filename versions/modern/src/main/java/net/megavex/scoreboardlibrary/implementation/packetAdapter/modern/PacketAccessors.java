package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.PacketConstructor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
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
  static {
    boolean is1_20_2OrAbove = false;
    try {
      Class.forName("net.minecraft.world.scores.DisplaySlot");
      is1_20_2OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_2_OR_ABOVE = is1_20_2OrAbove;

    boolean is1_20_3OrAbove = false;
    try {
      Class.forName("net.minecraft.network.chat.numbers.NumberFormat");
      is1_20_3OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_3_OR_ABOVE = is1_20_3OrAbove;

    boolean is1_20_5OrAbove = false;
    try {
      Class.forName("net.minecraft.network.protocol.common.ClientboundTransferPacket"); // Random 1.20.5 class
      is1_20_5OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_5_OR_ABOVE = is1_20_5OrAbove;

    if (is1_20_5OrAbove) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(ClientboundSetObjectivePacket.class, 0, Optional.class);
      SCORE_1_20_2_CONSTRUCTOR = null;
      SCORE_1_20_3_CONSTRUCTOR = null;
    } else if (is1_20_3OrAbove) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(ClientboundSetObjectivePacket.class, 0, NumberFormat.class);
      SCORE_1_20_3_CONSTRUCTOR = ReflectUtil.findConstructor(ClientboundSetScorePacket.class, String.class, String.class, int.class, Component.class, NumberFormat.class);
      SCORE_1_20_2_CONSTRUCTOR = null;
    } else {
      OBJECTIVE_NUMBER_FORMAT_FIELD = null;
      SCORE_1_20_3_CONSTRUCTOR = null;
      SCORE_1_20_2_CONSTRUCTOR = ReflectUtil.findConstructor(ClientboundSetScorePacket.class, ServerScoreboard.Method.class, String.class, String.class, int.class);
    }
  }

  public static final boolean IS_1_20_2_OR_ABOVE, IS_1_20_3_OR_ABOVE, IS_1_20_5_OR_ABOVE;

  public static final PacketConstructor<ClientboundSetObjectivePacket> OBJECTIVE_PACKET_CONSTRUCTOR =
    ReflectUtil.getEmptyConstructor(ClientboundSetObjectivePacket.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, 0, String.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, net.minecraft.network.chat.Component> OBJECTIVE_VALUE_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, 0, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetObjectivePacket, ObjectiveCriteria.RenderType> OBJECTIVE_RENDER_TYPE_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, 0, ObjectiveCriteria.RenderType.class);
  // Optional<NumberFormat> for 1.20.5+, NumberFormat for below
  public static final FieldAccessor<ClientboundSetObjectivePacket, Object> OBJECTIVE_NUMBER_FORMAT_FIELD;
  public static final FieldAccessor<ClientboundSetObjectivePacket, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findField(ClientboundSetObjectivePacket.class, 0, int.class);

  public static final ConstructorAccessor<ClientboundSetDisplayObjectivePacket> DISPLAY_1_20_1_CONSTRUCTOR =
    ReflectUtil.findOptionalConstructor(ClientboundSetDisplayObjectivePacket.class, int.class, Objective.class);
  public static final FieldAccessor<ClientboundSetDisplayObjectivePacket, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findField(ClientboundSetDisplayObjectivePacket.class, 0, String.class);

  public static final ConstructorAccessor<ClientboundSetScorePacket> SCORE_1_20_3_CONSTRUCTOR;
  public static final ConstructorAccessor<ClientboundSetScorePacket> SCORE_1_20_2_CONSTRUCTOR;

  public static final ConstructorAccessor<ClientboundSetPlayerTeamPacket> TEAM_PACKET_CONSTRUCTOR =
    ReflectUtil.findConstructor(ClientboundSetPlayerTeamPacket.class, String.class, int.class, Optional.class, Collection.class);
  public static final PacketConstructor<ClientboundSetPlayerTeamPacket.Parameters> PARAMETERS_CONSTRUCTOR =
    ReflectUtil.getEmptyConstructor(ClientboundSetPlayerTeamPacket.Parameters.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, Component> DISPLAY_NAME_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 0, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, net.minecraft.network.chat.Component> PREFIX_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 1, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, net.minecraft.network.chat.Component> SUFFIX_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 2, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, String> NAME_TAG_VISIBILITY_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 0, String.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, String> COLLISION_RULE_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 1, String.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, ChatFormatting> COLOR_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 0, ChatFormatting.class);
  public static final FieldAccessor<ClientboundSetPlayerTeamPacket.Parameters, Integer> OPTIONS_FIELD =
    ReflectUtil.findField(ClientboundSetPlayerTeamPacket.Parameters.class, 0, int.class);

  private PacketAccessors() {
  }
}
