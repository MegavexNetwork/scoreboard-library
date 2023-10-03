package net.megavex.scoreboardlibrary.implementation.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivesPacketAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.Component.empty;

public class ScoreboardObjectiveImpl implements ScoreboardObjective {
  private final ObjectivesPacketAdapter<?, ?> packetAdapter;
  private final String name;

  private final Map<String, Integer> scores = new HashMap<>();
  private Component value = empty();
  private ObjectiveRenderType renderType = ObjectiveRenderType.INTEGER;

  public ScoreboardObjectiveImpl(@NotNull ObjectiveManagerImpl manager, @NotNull String name) {
    this.packetAdapter = manager.library().packetAdapter().createObjectiveAdapter(name);
    this.name = name;
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
    this.value = value;
  }

  @Override
  public @NotNull ObjectiveRenderType renderType() {
    return renderType;
  }

  @Override
  public void renderType(@NotNull ObjectiveRenderType renderType) {
    this.renderType = renderType;
  }

  @Override
  public int score(@NotNull String entry) {
    return scores.get(entry);
  }

  @Override
  public void score(@NotNull String entry, int score) {
    if (score == 0) {
      scores.remove(entry);
    } else {
      scores.put(entry, score);
    }
  }
}
