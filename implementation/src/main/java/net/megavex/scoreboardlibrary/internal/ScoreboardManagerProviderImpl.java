package net.megavex.scoreboardlibrary.internal;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.internal.team.TeamInfoImpl;
import net.megavex.scoreboardlibrary.internal.team.TeamManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManagerProviderImpl extends ScoreboardManagerProvider {

    private static ScoreboardManagerProviderImpl instance;
    public final Map<Player, AbstractSidebar> sidebarMap = new ConcurrentHashMap<>();
    public final Map<Player, TeamManagerImpl> teamManagerMap = new ConcurrentHashMap<>();
    public final Map<JavaPlugin, ScoreboardManager> scoreboardManagerMap = CollectionProvider.map(1);

    private ScoreboardManagerProviderImpl() {
    }

    public static ScoreboardManagerProviderImpl instance() {
        return instance;
    }

    public static void instance(ScoreboardManagerProviderImpl instance) {
        ScoreboardManagerProviderImpl.instance = instance;
    }

    public static synchronized void init() {
        Preconditions.checkState(instance == null && ScoreboardManagerProvider.instance() == null);
        ScoreboardManagerProviderImpl provider = new ScoreboardManagerProviderImpl();
        instance = provider;
        ScoreboardManagerProvider.instance(provider);
    }

    @Override
    @NotNull
    public TeamInfo teamInfo(Collection<String> entries) {
        TeamInfoImpl impl = new TeamInfoImpl();
        if (entries != null) {
            impl.entries.addAll(entries);
        }

        return impl;
    }

    @Override
    @NotNull
    public ScoreboardManager scoreboardManager(JavaPlugin plugin) {
        return scoreboardManagerMap.computeIfAbsent(plugin, name -> new ScoreboardManagerImpl(plugin));
    }

    @Override
    public Sidebar sidebar(Player player) {
        return sidebarMap.get(player);
    }

    @Override
    public TeamManagerImpl teamManager(Player player) {
        return teamManagerMap.get(player);
    }
}
