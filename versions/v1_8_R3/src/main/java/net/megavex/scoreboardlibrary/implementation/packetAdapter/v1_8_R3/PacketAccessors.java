package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.server.v1_8_R3.*;

import java.util.Collection;

public final class PacketAccessors {
  public static final FieldAccessor<PacketPlayOutScoreboardObjective, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardObjective.class, "a", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardObjective, String> OBJECTIVE_DISPLAY_NAME_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardObjective.class, "b", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardObjective, IScoreboardCriteria.EnumScoreboardHealthDisplay> OBJECTIVE_HEALTH_DISPLAY_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardObjective.class, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.class);
  public static final FieldAccessor<PacketPlayOutScoreboardObjective, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardObjective.class, "d", int.class);

  public static final FieldAccessor<PacketPlayOutScoreboardDisplayObjective, Integer> DISPLAY_OBJECTIVE_POSITION =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardDisplayObjective.class, "a", int.class);
  public static final FieldAccessor<PacketPlayOutScoreboardDisplayObjective, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardDisplayObjective.class, "b", String.class);

  public static final FieldAccessor<PacketPlayOutScoreboardScore, String> SCORE_OBJECTIVE_NAME_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardScore.class, "b", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardScore, Integer> SCORE_VALUE_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardScore.class, "c", int.class);
  public static final FieldAccessor<PacketPlayOutScoreboardScore, PacketPlayOutScoreboardScore.EnumScoreboardAction> SCORE_ACTION_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardScore.class, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.class);

  public static final FieldAccessor<PacketPlayOutScoreboardTeam, String> TEAM_NAME_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "a", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, String> TEAM_DISPLAY_NAME_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "b", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, String> TEAM_PREFIX_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "c", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, String> TEAM_SUFFIX_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "d", String.class);
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, String> TEAM_NAME_TAG_VISIBILITY_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "e", String.class);
  @SuppressWarnings("rawtypes")
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, Collection> TEAM_ENTRIES_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "g", Collection.class);
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, Integer> TEAM_MODE_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "h", int.class);
  public static final FieldAccessor<PacketPlayOutScoreboardTeam, Integer> TEAM_RULES_FIELD =
    ReflectUtil.fieldAccessor(PacketPlayOutScoreboardTeam.class, "i", int.class);

  private PacketAccessors() {
  }
}
