package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.PacketConstructor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Collection;
import java.util.Optional;

public final class PacketAccessors {
  public static final boolean IS_1_20_2_OR_ABOVE, IS_1_20_3_OR_ABOVE, IS_1_20_5_OR_ABOVE;

  public static final Class<?> OBJECTIVE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket");
  public static final PacketConstructor<Object> OBJECTIVE_CONSTRUCTOR =
    ReflectUtil.getEmptyConstructorUnchecked(OBJECTIVE_CLASS);
  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, String.class);
  public static final FieldAccessor<Object, net.minecraft.network.chat.Component> OBJECTIVE_VALUE_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<Object, ObjectiveCriteria.RenderType> OBJECTIVE_RENDER_TYPE_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, ObjectiveCriteria.RenderType.class);
  // Optional<NumberFormat> for 1.20.5+, NumberFormat for below
  public static final FieldAccessor<Object, Object> OBJECTIVE_NUMBER_FORMAT_FIELD;
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, int.class);

  public static final Class<?> DISPLAY_OBJECTIVE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket");
  public static final ConstructorAccessor<?> DISPLAY_1_20_1_CONSTRUCTOR =
    ReflectUtil.findOptionalConstructor(DISPLAY_OBJECTIVE_CLASS, int.class, Objective.class);
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findFieldUnchecked(DISPLAY_OBJECTIVE_CLASS, 0, String.class);

  public static final ConstructorAccessor<Object> SCORE_1_20_3_CONSTRUCTOR;
  public static final ConstructorAccessor<Object> SCORE_1_20_2_CONSTRUCTOR;
  public static final ConstructorAccessor<Object> RESET_SCORE_CONSTRUCTOR;

  public static final Class<?> TEAM_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket");
  public static final Class<?> TEAM_PARAMETERS_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters");
  public static final ConstructorAccessor<Object> TEAM_PACKET_CONSTRUCTOR =
    ReflectUtil.findConstructorUnchecked(TEAM_CLASS, String.class, int.class, Optional.class, Collection.class);
  public static final PacketConstructor<Object> PARAMETERS_CONSTRUCTOR =
    ReflectUtil.getEmptyConstructorUnchecked(TEAM_PARAMETERS_CLASS);
  public static final FieldAccessor<Object, Component> DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 0, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<Object, net.minecraft.network.chat.Component> PREFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 1, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<Object, net.minecraft.network.chat.Component> SUFFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 2, net.minecraft.network.chat.Component.class);
  public static final FieldAccessor<Object, String> NAME_TAG_VISIBILITY_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 0, String.class);
  public static final FieldAccessor<Object, String> COLLISION_RULE_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 1, String.class);
  public static final FieldAccessor<Object, ChatFormatting> COLOR_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 0, ChatFormatting.class);
  public static final FieldAccessor<Object, Integer> OPTIONS_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_CLASS, 0, int.class);

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

    Class<?> scoreClass = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetScorePacket");

    if (is1_20_5OrAbove) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, Optional.class);
      SCORE_1_20_2_CONSTRUCTOR = null;
      SCORE_1_20_3_CONSTRUCTOR = null;
      RESET_SCORE_CONSTRUCTOR = null;
    } else if (is1_20_3OrAbove) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, NumberFormat.class);
      SCORE_1_20_3_CONSTRUCTOR = ReflectUtil.findConstructorUnchecked(scoreClass, String.class, String.class, int.class, Component.class, NumberFormat.class);
      SCORE_1_20_2_CONSTRUCTOR = null;

      Class<?> resetScoreClass = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundResetScorePacket");
      RESET_SCORE_CONSTRUCTOR = ReflectUtil.findConstructorUnchecked(resetScoreClass, String.class, String.class);
    } else {
      OBJECTIVE_NUMBER_FORMAT_FIELD = null;
      SCORE_1_20_3_CONSTRUCTOR = null;
      SCORE_1_20_2_CONSTRUCTOR = ReflectUtil.findConstructorUnchecked(scoreClass, ServerScoreboard.Method.class, String.class, String.class, int.class);
      RESET_SCORE_CONSTRUCTOR = null;
    }
  }

  private PacketAccessors() {
  }
}
