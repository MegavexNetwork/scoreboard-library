package net.megavex.scoreboardlibrary.internal.team;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TeamManagerImpl implements TeamManager {

  private static int idCounter = 0;
  public final Map<String, ScoreboardTeamImpl> teams = CollectionProvider.map(5);
  public final AtomicBoolean update = new AtomicBoolean();
  private final ScoreboardManagerImpl scoreboardManager;
  private final ComponentTranslator componentTranslator;
  private final Set<Player> players = CollectionProvider.set(4);
  private final int id = idCounter++;
  private boolean closed;

  public TeamManagerImpl(ScoreboardManagerImpl scoreboardManager, ComponentTranslator componentTranslator) {
    this.scoreboardManager = Objects.requireNonNull(scoreboardManager);
    this.componentTranslator = Objects.requireNonNull(componentTranslator);
  }

  public void update() {
    if (!update.getAndSet(false)) return;

    ImmutableList<ScoreboardTeamImpl> list;
    synchronized (teams) {
      list = ImmutableList.copyOf(teams.values());
    }

    for (ScoreboardTeamImpl team : list) {
      team.update();
    }
  }

  @Override
  public @NotNull ScoreboardManager scoreboardManager() {
    return scoreboardManager;
  }

  @Override
  public @NotNull Collection<Player> players() {
    return Collections.unmodifiableCollection(players);
  }

  @Override
  public @NotNull ComponentTranslator componentTranslator() {
    return componentTranslator;
  }

  @Override
  public @NotNull Collection<ScoreboardTeam> teams() {
    synchronized (teams) {
      return Collections.unmodifiableCollection(teams.values());
    }
  }

  @Override
  public @Nullable ScoreboardTeamImpl team(@NotNull String name) {
    checkDestroyed();
    checkTeamName(name);

    synchronized (teams) {
      return teams.get(name);
    }
  }

  @Override
  public boolean teamExists(String name) {
    return team(name) != null;
  }

  @Override
  public @NotNull ScoreboardTeam createIfAbsent(@NotNull String name, @Nullable BiFunction<Player, ScoreboardTeam, TeamInfo> teamInfoFunction) {
    checkDestroyed();
    ScoreboardTeamImpl team = team(name);
    if (team != null)
      return team;
    team = new ScoreboardTeamImpl(this, name);
    synchronized (teams) {
      teams.put(name, team);
    }

    for (Player player : players) {
      team.teamInfo(player, teamInfoFunction == null ? null : teamInfoFunction.apply(player, team));
    }
    return team;
  }

  @Override
  public boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction) {
    checkDestroyed();

    checkPlayer(player);
    if (players.add(player)) {
      ScoreboardManagerProviderImpl.instance().teamManagerMap.put(player, this);
      for (ScoreboardTeamImpl team : teams.values()) {
        TeamInfoImpl info = teamInfoFunction == null ? team.globalInfo() : (TeamInfoImpl) teamInfoFunction.apply(team);
        info = info == null ? team.globalInfo() : info;

        Collection<Player> singleton = Collections.singletonList(player);
        info.assign(team);
        info.addPlayers(singleton);
        info.nms.createTeam(singleton);
      }
      return true;
    }
    return false;
  }

  @Override
  public @NotNull Collection<Player> addPlayers(@NotNull Collection<Player> players, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction) {
    checkDestroyed();
    Preconditions.checkNotNull(players, "Players cannot be null");

    List<Player> filteredPlayers = new ArrayList<>(players.size());
    for (Player player : players) {
      checkPlayer(player);
      if (this.players.add(player)) {
        filteredPlayers.add(player);
        ScoreboardManagerProviderImpl.instance().teamManagerMap.put(player, this);
      }
    }

    if (!filteredPlayers.isEmpty()) {
      for (ScoreboardTeamImpl team : teams.values()) {
        TeamInfoImpl info = teamInfoFunction == null ? team.globalInfo() : (TeamInfoImpl) teamInfoFunction.apply(team);
        info = info == null ? team.globalInfo() : info;

        info.assign(team);
        info.players.addAll(players);
        info.nms.createTeam(filteredPlayers);
      }
    }
    return filteredPlayers;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    checkDestroyed();
    checkPlayer(player);

    if (players.remove(player)) {
      Collection<Player> singleton = Collections.singleton(player);
      for (ScoreboardTeamImpl team : teams.values()) {
        TeamInfoImpl info = team.getTeamInfo(player, false, true);
        if (info != null) {
          info.players.remove(player);
          team.nms.removeTeam(singleton);
        }
      }
      ScoreboardManagerProviderImpl.instance().teamManagerMap.remove(player);
      return true;
    }
    return false;
  }

  @Override
  public void removePlayers(@NotNull Collection<Player> players) {
    checkDestroyed();
    Preconditions.checkNotNull(players);

    List<Player> filteredPlayers = CollectionProvider.list(players.size());
    for (Player player : players) {
      if (!this.players.remove(player)) continue;
      ScoreboardManagerProviderImpl.instance().teamManagerMap.remove(player);
      for (ScoreboardTeamImpl team : teams.values()) {
        TeamInfoImpl info = team.getTeamInfo(player, false, true);
        if (info != null) {
          info.players.remove(player);
          filteredPlayers.add(player);
        }
      }
    }
    for (ScoreboardTeamImpl team : teams.values()) {
      team.nms.removeTeam(filteredPlayers);
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public void close() {
    if (!closed) {
      ImmutableList.copyOf(teams.values()).forEach(ScoreboardTeam::close);
      if (scoreboardManager.teamManagers != null) {
        scoreboardManager.teamManagers.remove(this);
      }

      for (Player player : players) {
        ScoreboardManagerProviderImpl.instance().teamManagerMap.remove(player);
      }
      players.clear(); // Prevent a memory leak

      closed = true;
    }
  }

  @Override
  public String toString() {
    return "TeamManagerImpl{" +
      "teams=" + teams +
      ", scoreboardManager=" + scoreboardManager +
      ", players=" + players +
      ", closed=" + closed +
      '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(scoreboardManager, id);
  }

  protected void checkDestroyed() {
    Preconditions.checkState(!closed, "Team manager is closed");
  }

  protected void checkTeamName(String name) {
    Preconditions.checkNotNull(name, "Team name cannot be null");
    Preconditions.checkArgument(!name.isEmpty(), "Team name cannot be empty");
  }

  protected void checkPlayer(Player player) {
    Preconditions.checkNotNull(player, "Player cannot be null");
    TeamManagerImpl teamManager = ScoreboardManagerProviderImpl.instance().teamManager(player);
    if (teamManager != this && teamManager != null)
      throw new IllegalArgumentException("Player already has a TeamManager which is owned by " + teamManager.scoreboardManager().plugin().getName());
  }
}
