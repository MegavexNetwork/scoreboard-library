package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class PacketAccessors {
  public static final Class<?> OBJECTIVE_CLASS =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardObjective"));

  public static final Class<?> HEALTH_DISPLAY_CLASS =
    RandomUtils.getOptionalClass(RandomUtils.server("IScoreboardCriteria$EnumScoreboardHealthDisplay"));
  public static final Object HEALTH_DISPLAY_INTEGER = HEALTH_DISPLAY_CLASS != null ? getEnumInstance(HEALTH_DISPLAY_CLASS, "INTEGER") : null;
  public static final Object HEALTH_DISPLAY_HEARTS = HEALTH_DISPLAY_CLASS != null ? getEnumInstance(HEALTH_DISPLAY_CLASS, "HEARTS") : null;

  public static final Class<?> DISPLAY_OBJECTIVE_CLASS =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardDisplayObjective"));

  public static final Class<?> SCORE_CLASS =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardScore"));


  public static final Class<?> TEAM_CLASS =
    RandomUtils.getClassOrThrow(RandomUtils.server("PacketPlayOutScoreboardTeam"));

  public static final ConstructorAccessor<?> OBJECTIVE_CONSTRUCTOR = ReflectUtil.findConstructor(PacketAccessors.OBJECTIVE_CLASS).get();
  public static final ConstructorAccessor<?> DISPLAY_OBJECTIVE_CONSTRUCTOR = ReflectUtil.findConstructor(PacketAccessors.DISPLAY_OBJECTIVE_CLASS).get();
  public static final ConstructorAccessor<?> SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(PacketAccessors.SCORE_CLASS, String.class).get();

  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, String.class);
  public static final FieldAccessor<Object, String> OBJECTIVE_DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 1, String.class);
  public static final FieldAccessor<Object, Object> OBJECTIVE_HEALTH_DISPLAY_FIELD =
    HEALTH_DISPLAY_CLASS != null ? ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, HEALTH_DISPLAY_CLASS) : null;
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, int.class);

  public static final FieldAccessor<Object, Integer> DISPLAY_OBJECTIVE_POSITION =
    ReflectUtil.findFieldUnchecked(DISPLAY_OBJECTIVE_CLASS, 0, int.class);
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findFieldUnchecked(DISPLAY_OBJECTIVE_CLASS, 0, String.class);

  public static final FieldAccessor<Object, String> SCORE_OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(SCORE_CLASS, 1, String.class);
  public static final FieldAccessor<Object, Integer> SCORE_VALUE_FIELD =
    ReflectUtil.findFieldUnchecked(SCORE_CLASS, 0, int.class);

  // 1.8+
  public static final Class<?> SCORE_ACTION_CLASS =
    RandomUtils.getOptionalClass(RandomUtils.server("PacketPlayOutScoreboardScore$EnumScoreboardAction"));
  public static final FieldAccessor<Object, Object> SCORE_ACTION_FIELD_1_8 =
    SCORE_ACTION_CLASS != null ? ReflectUtil.findFieldUnchecked(SCORE_CLASS, 0, SCORE_ACTION_CLASS) : null;
  public static final Object SCORE_ACTION_v1_8_CHANGE = SCORE_ACTION_FIELD_1_8 != null ? getEnumInstance(SCORE_ACTION_CLASS, "CHANGE") : null;

  public static final FieldAccessor<Object, Integer> SCORE_ACTION_FIELD_1_7 =
    SCORE_ACTION_CLASS == null ? ReflectUtil.findFieldUnchecked(SCORE_CLASS, 1, int.class) : null;

  public static final FieldAccessor<Object, String> TEAM_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 0, String.class);
  public static final FieldAccessor<Object, String> TEAM_DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 1, String.class);
  public static final FieldAccessor<Object, String> TEAM_PREFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 2, String.class);
  public static final FieldAccessor<Object, String> TEAM_SUFFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 3, String.class);
  public static final FieldAccessor<Object, String> TEAM_NAME_TAG_VISIBILITY_FIELD =
    HEALTH_DISPLAY_CLASS != null ? ReflectUtil.findFieldUnchecked(TEAM_CLASS, 4, String.class) : null;
  public static final FieldAccessor<Object, Integer> TEAM_COLOR_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 0, int.class);
  @SuppressWarnings("rawtypes")
  public static final FieldAccessor<Object, Collection> TEAM_ENTRIES_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 0, Collection.class);
  public static final FieldAccessor<Object, Integer> TEAM_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 1, int.class);
  public static final FieldAccessor<Object, Integer> TEAM_RULES_FIELD =
    HEALTH_DISPLAY_CLASS != null ? ReflectUtil.findFieldUnchecked(TEAM_CLASS, 2, int.class) : null;

  private PacketAccessors() {
  }

  private static @NotNull Object getEnumInstance(@NotNull Class<?> c, @NotNull String name) {
    //noinspection unchecked,rawtypes
    return Enum.valueOf((Class<? extends Enum>) c, name);
  }
}
