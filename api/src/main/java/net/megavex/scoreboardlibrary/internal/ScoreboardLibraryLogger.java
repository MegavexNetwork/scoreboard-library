package net.megavex.scoreboardlibrary.internal;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ScoreboardLibraryLogger {

  public static final boolean DEBUG;

  static {
    String property = System.getProperty(ScoreboardLibrary.NAMESPACE + ".debug");
    if (property != null) {
      DEBUG = property.equalsIgnoreCase("true");
      logMessage("Debug enabled.");
    } else {
      DEBUG = false;
    }
  }

  public static void logMessage(String message) {
    if (DEBUG) {
      ScoreboardManagerProvider provider = ScoreboardManagerProvider.instance();
      ScoreboardManagerProvider
        .loaderPlugin()
        .getLogger()
        .info("[ScoreboardManager] " + message);
    }
  }
}
