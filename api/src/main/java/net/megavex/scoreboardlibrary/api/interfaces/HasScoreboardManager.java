package net.megavex.scoreboardlibrary.api.interfaces;

import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public interface HasScoreboardManager {
    @NotNull ScoreboardManager scoreboardManager();
}
