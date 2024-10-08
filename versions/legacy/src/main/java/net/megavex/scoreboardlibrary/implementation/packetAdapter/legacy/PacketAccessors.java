package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.util.Collection;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.RandomUtils.is1_7Minus;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy.RandomUtils.is1_8Plus;

public final class PacketAccessors {
  public static final Class<Object> packetPlayOutScoreboardObjectiveClass =
    RandomUtils.getServerClass("PacketPlayOutScoreboardObjective");
  public static final Class<Object> enumScoreboardHealthDisplayClass =
    is1_8Plus ? RandomUtils.getServerClass("IScoreboardCriteria$EnumScoreboardHealthDisplay") : null;

  public static final Class<Object> packetPlayOutScoreboardDisplayObjectiveClass =
    RandomUtils.getServerClass("PacketPlayOutScoreboardDisplayObjective");

  public static final Class<Object> packetPlayOutScoreboardScoreClass =
    RandomUtils.getServerClass("PacketPlayOutScoreboardScore");
  public static final Class<Object> enumScoreboardActionClass =
    is1_8Plus ? RandomUtils.getServerClass("PacketPlayOutScoreboardScore$EnumScoreboardAction") : null;

  public static final Class<Object> packetPlayOutScoreboardTeamClass =
    RandomUtils.getServerClass("PacketPlayOutScoreboardTeam");


  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardObjectiveClass, 0, String.class);
  public static final FieldAccessor<Object, String> OBJECTIVE_DISPLAY_NAME_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardObjectiveClass, 1, String.class);
  public static final FieldAccessor<Object, Object> OBJECTIVE_HEALTH_DISPLAY_FIELD =
    is1_8Plus ? ReflectUtil.findField(packetPlayOutScoreboardObjectiveClass, 0, enumScoreboardHealthDisplayClass) : null;
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardObjectiveClass, 0, int.class);

  public static final FieldAccessor<Object, Integer> DISPLAY_OBJECTIVE_POSITION =
    ReflectUtil.findField(packetPlayOutScoreboardDisplayObjectiveClass, 0, int.class);
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findField(packetPlayOutScoreboardDisplayObjectiveClass, 0, String.class);

  public static final FieldAccessor<Object, String> SCORE_OBJECTIVE_NAME_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardScoreClass, 1, String.class);
  public static final FieldAccessor<Object, Integer> SCORE_VALUE_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardScoreClass, 0, int.class);

  public static final FieldAccessor<Object, Object> SCORE_ACTION_FIELD_1_8 =
    is1_8Plus ? ReflectUtil.findField(packetPlayOutScoreboardScoreClass, 0, enumScoreboardActionClass) : null;
  public static final FieldAccessor<Object, Integer> SCORE_ACTION_FIELD_1_7 =
    is1_7Minus ? ReflectUtil.findField(packetPlayOutScoreboardScoreClass, 1, int.class) : null;

  public static final FieldAccessor<Object, String> TEAM_NAME_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 0, String.class);
  public static final FieldAccessor<Object, String> TEAM_DISPLAY_NAME_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 1, String.class);
  public static final FieldAccessor<Object, String> TEAM_PREFIX_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 2, String.class);
  public static final FieldAccessor<Object, String> TEAM_SUFFIX_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 3, String.class);
  public static final FieldAccessor<Object, String> TEAM_NAME_TAG_VISIBILITY_FIELD =
    is1_8Plus ? ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 4, String.class) : null;
  public static final FieldAccessor<Object, Integer> TEAM_COLOR_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 0, int.class);
  @SuppressWarnings("rawtypes")
  public static final FieldAccessor<Object, Collection> TEAM_ENTRIES_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 0, Collection.class);
  public static final FieldAccessor<Object, Integer> TEAM_MODE_FIELD =
    ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 1, int.class);
  public static final FieldAccessor<Object, Integer> TEAM_RULES_FIELD =
    is1_8Plus ? ReflectUtil.findField(packetPlayOutScoreboardTeamClass, 2, int.class) : null;

  private PacketAccessors() {
  }
}
