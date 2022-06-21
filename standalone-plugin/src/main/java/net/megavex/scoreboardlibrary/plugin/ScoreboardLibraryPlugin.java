package net.megavex.scoreboardlibrary.plugin;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.ScoreboardLibraryImplementation;
import net.megavex.scoreboardlibrary.exception.ScoreboardLibraryLoadException;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardLibraryPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    try {
      ScoreboardLibraryImplementation.init();
    } catch (ScoreboardLibraryLoadException e) {
      getLogger().log(Level.SEVERE, "Couldn't load ScoreboardLibrary", e);
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    ScoreboardLibraryImplementation.close();
  }
}
