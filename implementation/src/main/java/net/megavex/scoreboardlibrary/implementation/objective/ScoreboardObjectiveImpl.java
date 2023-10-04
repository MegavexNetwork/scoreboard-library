package net.megavex.scoreboardlibrary.implementation.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivesPacketAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static net.kyori.adventure.text.Component.empty;

public class ScoreboardObjectiveImpl implements ScoreboardObjective {
  private final ObjectivesPacketAdapter<?, ?> packetAdapter;
  private final Queue<ObjectiveManagerTask> taskQueue;
  private final String name;

  private final Map<String, Integer> scores = new HashMap<>();
  private Component value = empty();
  private ObjectiveRenderType renderType = ObjectiveRenderType.INTEGER;

  public ScoreboardObjectiveImpl(@NotNull ObjectivesPacketAdapter<?, ?> packetAdapter, @NotNull Queue<ObjectiveManagerTask> taskQueue, @NotNull String name) {
    this.packetAdapter = packetAdapter;
    this.taskQueue = taskQueue;
    this.name = name;
  }

  public ObjectivesPacketAdapter<?, ?> packetAdapter() {
    return packetAdapter;
  }

  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull Component value() {
    return value;
  }

  @Override
  public void value(@NotNull Component value) {
    if (!this.value.equals(value)) {
      this.value = value;
      taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
    }
  }

  @Override
  public @NotNull ObjectiveRenderType renderType() {
    return renderType;
  }

  @Override
  public void renderType(@NotNull ObjectiveRenderType renderType) {
    if (this.renderType != renderType) {
      this.renderType = renderType;
      taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
    }
  }

  @Override
  public int score(@NotNull String entry) {
    return scores.get(entry);
  }

  @Override
  public void score(@NotNull String entry, int score) {
    scores.put(entry, score);
    taskQueue.add(new ObjectiveManagerTask.UpdateScore(this, entry, score));
  }

  @Override
  public void removeScore(@NotNull String entry) {
    scores.remove(entry);
  }
}
