package net.megavex.scoreboardlibrary.implementation.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static net.kyori.adventure.text.Component.empty;

public class ScoreboardObjectiveImpl implements ScoreboardObjective {
  private final ObjectivePacketAdapter<?, ?> packetAdapter;
  private final Queue<ObjectiveManagerTask> taskQueue;
  private final String name;

  private final Map<String, Integer> scores = new HashMap<>();
  private Component value = empty();
  private ObjectiveRenderType renderType = ObjectiveRenderType.INTEGER;
  private boolean closed;

  public ScoreboardObjectiveImpl(@NotNull ObjectivePacketAdapter<?, ?> packetAdapter, @NotNull Queue<ObjectiveManagerTask> taskQueue, @NotNull String name) {
    this.packetAdapter = packetAdapter;
    this.taskQueue = taskQueue;
    this.name = name;
  }

  public ObjectivePacketAdapter<?, ?> packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Map<String, Integer> scores() {
    return scores;
  }

  public @NotNull String name() {
    return name;
  }

  public void close() {
    closed = true;
  }

  @Override
  public @NotNull Component value() {
    return value;
  }

  @Override
  public @NotNull ScoreboardObjective value(@NotNull Component value) {
    if (!this.value.equals(value)) {
      this.value = value;
      if (!closed) {
        taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
      }
    }
    return this;
  }

  @Override
  public @NotNull ObjectiveRenderType renderType() {
    return renderType;
  }

  @Override
  public @NotNull ScoreboardObjective renderType(@NotNull ObjectiveRenderType renderType) {
    if (this.renderType != renderType) {
      this.renderType = renderType;
      if (!closed) {
        taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
      }
    }
    return this;
  }

  @Override
  public Integer score(@NotNull String entry) {
    return scores.get(entry);
  }

  @Override
  public @NotNull ScoreboardObjective score(@NotNull String entry, int score) {
    Integer oldScore = scores.put(entry, score);
    if (!closed && (oldScore == null || score != oldScore)) {
      taskQueue.add(new ObjectiveManagerTask.UpdateScore(this, entry, score));
    }
    return this;
  }

  @Override
  public @NotNull ScoreboardObjective removeScore(@NotNull String entry) {
    if (scores.remove(entry) != null && !closed) {
      taskQueue.add(new ObjectiveManagerTask.UpdateScore(this, entry, null));
    }
    return this;
  }

  public void sendProperties(@NotNull Collection<Player> players, @NotNull ObjectivePacketAdapter.ObjectivePacketType packetType) {
    packetAdapter.sendProperties(players, packetType, value, renderType, true);
  }
}
