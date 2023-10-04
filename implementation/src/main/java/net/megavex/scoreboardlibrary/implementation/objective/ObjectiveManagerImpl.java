package net.megavex.scoreboardlibrary.implementation.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivesPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.player.PlayerDisplayable;
import net.megavex.scoreboardlibrary.implementation.player.ScoreboardLibraryPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectiveManagerImpl implements ObjectiveManager, PlayerDisplayable {
  private final ScoreboardLibraryImpl library;
  private final Map<String, ScoreboardObjectiveImpl> objectives = new ConcurrentHashMap<>();
  private final Map<ObjectiveDisplaySlot, ScoreboardObjectiveImpl> displaySlots = new HashMap<>();

  private final Set<Player> players = CollectionProvider.set(8);
  private final Set<Player> displayingPlayers = CollectionProvider.set(8);
  private final Queue<ObjectiveManagerTask> taskQueue = new ConcurrentLinkedQueue<>();
  private boolean closed;

  public ObjectiveManagerImpl(@NotNull ScoreboardLibraryImpl library) {
    this.library = library;
  }

  @Override
  public @NotNull ScoreboardObjective create(@NotNull String name) {
    return objectives.computeIfAbsent(name, i -> {
      ObjectivesPacketAdapter<?, ?> packetAdapter = library.packetAdapter().createObjectiveAdapter(name);
      ScoreboardObjectiveImpl objective = new ScoreboardObjectiveImpl(packetAdapter, taskQueue, name);
      taskQueue.add(new ObjectiveManagerTask.AddObjective(objective));
      return objective;
    });
  }

  @Override
  public void remove(@NotNull ScoreboardObjective objective) {
    if (!(objective instanceof ScoreboardObjectiveImpl)) {
      return;
    }

    ScoreboardObjectiveImpl impl = (ScoreboardObjectiveImpl) objective;
    if (objectives.values().remove(impl)) {
      taskQueue.add(new ObjectiveManagerTask.RemoveObjective(impl));
    }
  }

  @Override
  public void display(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjective objective) {
    if (!(objective instanceof ScoreboardObjectiveImpl)) {
      throw new IllegalArgumentException("Invalid objective implementation");
    }

    ScoreboardObjectiveImpl impl = (ScoreboardObjectiveImpl) objective;
    if (objectives.get(impl.name()) != impl) {
      throw new IllegalArgumentException("Objective is from a different manager");
    }

    displaySlots.put(displaySlot, impl);
    taskQueue.add(new ObjectiveManagerTask.UpdateDisplaySlot(displaySlot, impl));
  }

  @Override
  public @NotNull Collection<Player> players() {
    return Collections.unmodifiableSet(players);
  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    if (!players.add(player)) {
      return false;
    }

    taskQueue.add(new ObjectiveManagerTask.AddPlayer(player));
    return true;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    if (!players.remove(player)) {
      return false;
    }

    taskQueue.add(new ObjectiveManagerTask.RemovePlayer(player));
    return true;
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(ObjectiveManagerTask.Close.INSTANCE);
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public void tick() {
    while (true) {
      ObjectiveManagerTask task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof ObjectiveManagerTask.Close) {
        library.objectiveManagers().remove(this);
        // TODO: hide objectives
        for (Player player : players) {
          Objects.requireNonNull(library.getPlayer(player)).objectiveManagerQueue().remove(this);
        }
      } else if (task instanceof ObjectiveManagerTask.AddPlayer) {
        Player player = ((ObjectiveManagerTask.AddPlayer) task).player();
        @NotNull ScoreboardLibraryPlayer slPlayer = library.getOrCreatePlayer(player);
        slPlayer.objectiveManagerQueue().add(this);
      } else if (task instanceof ObjectiveManagerTask.RemovePlayer) {
        Player player = ((ObjectiveManagerTask.RemovePlayer) task).player();
        // TODO: hide objectives
        Objects.requireNonNull(library.getPlayer(player)).objectiveManagerQueue().remove(this);
      } else if (task instanceof ObjectiveManagerTask.ReloadPlayer) {
        Player player = ((ObjectiveManagerTask.ReloadPlayer) task).player();
        // TODO: resend objective data
      } else if (task instanceof ObjectiveManagerTask.AddObjective) {
        ScoreboardObjectiveImpl objective = ((ObjectiveManagerTask.AddObjective) task).objective();
        // TODO: show to players
      } else if (task instanceof ObjectiveManagerTask.RemoveObjective) {
        ScoreboardObjectiveImpl objective = ((ObjectiveManagerTask.RemoveObjective) task).objective();
        // TODO: hide from players
      } else if (task instanceof ObjectiveManagerTask.UpdateScore) {
        ScoreboardObjectiveImpl objective = ((ObjectiveManagerTask.UpdateScore) task).objective();
        // TODO: resend data to players
      } else if (task instanceof ObjectiveManagerTask.UpdateDisplaySlot) {
        ObjectiveManagerTask.UpdateDisplaySlot updateDisplaySlotTask = (ObjectiveManagerTask.UpdateDisplaySlot) task;
        ObjectiveDisplaySlot slot = updateDisplaySlotTask.displaySlot();
        ScoreboardObjectiveImpl objective = updateDisplaySlotTask.objective();
        // TODO: send change to players
      }
    }
  }

  @Override
  public void show(@NotNull Player player) {
    displayingPlayers.add(player);
  }
}
