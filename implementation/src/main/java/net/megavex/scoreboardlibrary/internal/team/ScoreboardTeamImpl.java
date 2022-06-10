package net.megavex.scoreboardlibrary.internal.team;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScoreboardTeamImpl implements ScoreboardTeam {

  public final TeamManagerImpl teamManager;
  public final String name;
  public final TeamNMS<?, ?> nms;
  public Collection<TeamInfoImpl> infos;
  public boolean closed;
  short idCounter = 0;
  private TeamInfoImpl globalInfo;

  public ScoreboardTeamImpl(TeamManagerImpl teamManager, String name) {
    this.teamManager = teamManager;
    this.name = name;
    this.nms = ScoreboardManagerNMS.INSTANCE.createTeamNMS(name);
  }

  public Collection<TeamInfoImpl> teamInfos() {
    if (infos == null) {
      infos = CollectionProvider.set(1);
    }

    return infos;
  }

  public void update() {
    if (infos == null) return;
    for (var teamInfo : infos) {
      teamInfo.update();
    }
  }

  @Override
  public @NotNull TeamInfoImpl globalInfo() {
    if (globalInfo == null) {
      globalInfo = new TeamInfoImpl();
      globalInfo.assign(this);
      infos.add(globalInfo);
    }

    return globalInfo;
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull TeamManager teamManager() {
    return teamManager;
  }

  @Override
  public @NotNull TeamInfoImpl teamInfo(Player player) {
    return getTeamInfo(player, true, false);
  }

  public TeamInfoImpl getTeamInfo(Player player, boolean check, boolean nullable) {
    checkDestroyed();
    if (check) {
      checkPlayer(player);
    }

    for (var teamInfo : infos) {
      if (teamInfo.players.contains(player)) {
        return teamInfo;
      }
    }

    if (nullable) {
      return null;
    }

    globalInfo().players.add(player);
    return globalInfo;
  }

  @Override
  public @NotNull TeamInfoImpl teamInfo(Player player, @Nullable TeamInfo teamInfo) {
    checkDestroyed();
    checkPlayer(player);

    var impl = teamInfo == null ? globalInfo() : (TeamInfoImpl) teamInfo;

    var oldInfo = getTeamInfo(player, true, true);
    if (oldInfo != null) {
      if (oldInfo == impl)
        return impl;
      oldInfo.players.remove(player);
    }

    if (impl.team() != this)
      impl.unassign();
    impl.assign(this);

    var singleton = Set.of(player);
    impl.addPlayers(singleton);
    if (oldInfo != null) {
      impl.nms.updateTeam(singleton);
      TeamInfoImpl.syncEntries(singleton, oldInfo, impl);
    } else {
      impl.nms.createTeam(singleton);
    }

    return impl;
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }

    if (infos != null) {
      infos.forEach(info -> {
        if (info != null && info.team() != null) {
          Objects.requireNonNull(info.team()).nms.removeTeam(info.players);
        }
      });
    }

    teamManager.teams.remove(name);
    closed = true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var that = (ScoreboardTeamImpl) o;
    return closed == that.closed &&
      idCounter == that.idCounter &&
      Objects.equals(teamManager, that.teamManager) &&
      Objects.equals(name, that.name) &&
      Objects.equals(nms, that.nms) &&
      Objects.equals(globalInfo, that.globalInfo) &&
      Objects.equals(infos, that.infos);
  }

  @Override
  public String toString() {
    return "ScoreboardTeamImpl{" +
      "name='" + name + '\'' +
      ", destroyed=" + closed +
      '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamManager, name);
  }

  protected void checkDestroyed() {
    Preconditions.checkState(!closed, "Team is closed");
  }

  protected void checkPlayer(Player player) {
    Preconditions.checkNotNull(player, "Player cannot be null");
    Preconditions.checkArgument(teamManager.players().contains(player), "Player is not in fillTeamPacket teamManager");
  }
}
