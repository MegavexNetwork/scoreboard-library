package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.util.Collection;

public final class PacketAccessors {
  public static final Class<?> packetPlayOutScoreboardObjectiveClass =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardObjective"));
  public static final Class<?> enumScoreboardHealthDisplayClass =
    RandomUtils.getOptionalClass(RandomUtils.server("IScoreboardCriteria$EnumScoreboardHealthDisplay"));

  public static final Class<?> packetPlayOutScoreboardDisplayObjectiveClass =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardDisplayObjective"));

  public static final Class<?> packetPlayOutScoreboardScoreClass =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardScore"));
  public static final Class<?> enumScoreboardActionClass =
    RandomUtils.getOptionalClass(RandomUtils.server("PacketPlayOutScoreboardScore$EnumScoreboardAction"));

  public static final Class<?> packetPlayOutScoreboardTeamClass =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardTeam"));


  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardObjectiveClass, 0, String.class);
  public static final FieldAccessor<Object, String> OBJECTIVE_DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardObjectiveClass, 1, String.class);
  public static final FieldAccessor<Object, Object> OBJECTIVE_HEALTH_DISPLAY_FIELD =
    enumScoreboardHealthDisplayClass != null ? ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardObjectiveClass, 0, enumScoreboardHealthDisplayClass) : null;
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardObjectiveClass, 0, int.class);

  public static final FieldAccessor<Object, Integer> DISPLAY_OBJECTIVE_POSITION =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardDisplayObjectiveClass, 0, int.class);
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardDisplayObjectiveClass, 0, String.class);

  public static final FieldAccessor<Object, String> SCORE_OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardScoreClass, 1, String.class);
  public static final FieldAccessor<Object, Integer> SCORE_VALUE_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardScoreClass, 0, int.class);

  public static final FieldAccessor<Object, Object> SCORE_ACTION_FIELD_1_8 =
    enumScoreboardActionClass != null ? ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardScoreClass, 0, enumScoreboardActionClass) : null;
  public static final FieldAccessor<Object, Integer> SCORE_ACTION_FIELD_1_7 =
    enumScoreboardActionClass == null ? ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardScoreClass, 1, int.class) : null;

  public static final FieldAccessor<Object, String> TEAM_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 0, String.class);
  public static final FieldAccessor<Object, String> TEAM_DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 1, String.class);
  public static final FieldAccessor<Object, String> TEAM_PREFIX_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 2, String.class);
  public static final FieldAccessor<Object, String> TEAM_SUFFIX_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 3, String.class);
  public static final FieldAccessor<Object, String> TEAM_NAME_TAG_VISIBILITY_FIELD =
    enumScoreboardHealthDisplayClass != null ? ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 4, String.class) : null;
  public static final FieldAccessor<Object, Integer> TEAM_COLOR_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 0, int.class);
  @SuppressWarnings("rawtypes")
  public static final FieldAccessor<Object, Collection> TEAM_ENTRIES_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 0, Collection.class);
  public static final FieldAccessor<Object, Integer> TEAM_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 1, int.class);
  public static final FieldAccessor<Object, Integer> TEAM_RULES_FIELD =
    enumScoreboardHealthDisplayClass != null ? ReflectUtil.findFieldUnchecked(packetPlayOutScoreboardTeamClass, 2, int.class) : null;

  private PacketAccessors() {
  }
}
