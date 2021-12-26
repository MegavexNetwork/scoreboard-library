package net.megavex.scoreboardlibrary.internal.sidebar;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.util.SidebarUtilities;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.SidebarNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.CollectionProvider;
import net.megavex.scoreboardlibrary.internal.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.internal.sidebar.line.SidebarLineHandler;
import net.megavex.scoreboardlibrary.internal.sidebar.line.locale.LineType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.empty;

public abstract class AbstractSidebar implements Sidebar {

    public final GlobalLineInfo[] lines;
    private final ScoreboardManager scoreboardManager;
    protected Map<Player, SidebarLineHandler> playerMap = CollectionProvider.map(1);
    private Component title = empty();
    private boolean closed, visible, visibilityChanged;

    // Internal
    private boolean updateTitle, updateLines;
    private SidebarNMS<?, ?> nms;

    public AbstractSidebar(ScoreboardManager scoreboardManager, int size) {
        this.scoreboardManager = scoreboardManager;
        SidebarUtilities.checkLineBounds(size);
        this.lines = new GlobalLineInfo[size];
    }

    protected abstract void forEachSidebar(Consumer<SidebarLineHandler> consumer);

    protected abstract SidebarLineHandler lineHandler(Player player);

    protected void updateScores() {
        byte size = 0;
        for (GlobalLineInfo line : lines) {
            if (line != null && line.value() != null) {
                size++;
            }
        }

        byte i = 0;
        for (GlobalLineInfo line : lines) {
            if (line != null && line.value() != null) {
                byte oldScore = line.objectiveScore();
                line.objectiveScore((byte) (size - i - 1));
                if (line.objectiveScore() != oldScore) {
                    line.updateScore(true);
                }
                i++;
            }
        }
    }

    @Override
    public ScoreboardManager scoreboardManager() {
        return scoreboardManager;
    }

    @Override
    public byte maxLines() {
        return (byte) lines.length;
    }

    public void update() {
        if (visibilityChanged) {
            if (playerMap != null) {
                if (visible) {
                    sidebarBridge().create(playerMap.keySet());
                    forEachSidebar(SidebarLineHandler::show);
                    ScoreboardManagerNMS.INSTANCE.displaySidebar(playerMap.keySet());
                } else {
                    forEachSidebar(SidebarLineHandler::hide);
                    ScoreboardManagerNMS.INSTANCE.removeSidebar(playerMap.keySet());
                }
            }
            visibilityChanged = false;
        }

        if (updateTitle) {
            updateTitle = false;
            sidebarBridge().updateTitle(title);
            nms.update(playerMap.keySet());
        }

        if (updateLines) {
            updateScores();

            for (GlobalLineInfo line : lines) {
                if (line == null || !line.update()) continue;
                line.updateTeams(true);


                updateScores();

                forEachSidebar(s -> {
                    if (line.value() != null) {
                        Component rendered = GlobalTranslator.render(line.value(), s.locale());
                        s.setLine(line.line(), rendered);
                    } else {
                        s.setLine(line.line(), null);
                    }
                });
            }

            updateLines = false;
            forEachSidebar(SidebarLineHandler::update);
        }
    }

    @Override
    public Set<Player> players() {
        if (closed || playerMap == null) return Collections.emptySet();
        return playerMap.keySet();
    }

    @Override
    public boolean addPlayer(Player player) {
        checkClosed();
        checkPlayer(player);
        if (playerMap().containsKey(player)) return false;

        SidebarLineHandler sidebar = lineHandler(player);
        playerMap.put(player, sidebar);
        LineType lineType = LineType.getType(player);
        sidebar.playersInit(lineType).add(player);

        if (visible) {
            var singleton = Collections.singleton(player);
            sidebarBridge().create(singleton);
            sidebar.show(singleton, lineType);
            ScoreboardManagerNMS.INSTANCE.displaySidebar(singleton);
        }

        ScoreboardManagerProviderImpl.instance().sidebarMap.put(player, this);
        return true;
    }

    @Override
    public boolean removePlayer(Player player) {
        checkClosed();

        SidebarLineHandler sidebar;
        if (playerMap == null ||
                (sidebar = playerMap.remove(player)) == null) {
            return false;
        }

        LineType lineType = LineType.getType(player);
        ScoreboardManagerProviderImpl.instance().sidebarMap.remove(player, this);
        if (visible) {
            var singleton = Collections.singleton(player);
            sidebar.hide(singleton, lineType);
            ScoreboardManagerNMS.INSTANCE.removeSidebar(singleton);
        }

        sidebar.players(lineType).remove(player);
        return true;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void visible(boolean visible) {
        checkClosed();

        if (this.visible != visible) {
            this.visible = visible;
            this.visibilityChanged = !visibilityChanged;
        }
    }

    @Override
    public Component title() {
        return title;
    }

    @Override
    public void line(int line, Component value) {
        checkClosed();
        checkLine(line);

        Component component = line(line);
        if (Objects.equals(value, component)) return;

        GlobalLineInfo lineInfo = getLineInfo(line);
        lineInfo.value(value);
        lineInfo.update(true);
        updateLines = true;
    }

    @Override
    public @Nullable Component line(int line) {
        GlobalLineInfo info = lines[line];
        return info == null ? null : info.value();
    }

    public GlobalLineInfo getLineInfo(int line) {
        GlobalLineInfo info = lines[line];
        if (info == null) {
            info = new GlobalLineInfo((byte) line);
            lines[line] = info;
        }
        return info;
    }

    @Override
    public void title(Component title) {
        checkClosed();
        Preconditions.checkNotNull(title);

        if (!this.title.equals(title)) {
            this.title = title;
            updateTitle = true;
        }
    }

    @Override
    public void close() {
        if (playerMap != null) {
            visible(false);
            playerMap = null;
        }
        closed = true;
    }

    @Override
    public boolean closed() {
        return closed;
    }

    public SidebarNMS<?, ?> sidebarBridge() {
        if (nms == null) nms = ScoreboardManagerNMS.INSTANCE.createSidebarNMS(this);
        return nms;
    }

    public Map<Player, SidebarLineHandler> playerMap() {
        if (playerMap == null) playerMap = CollectionProvider.map(1);
        return playerMap;
    }

    private void checkPlayer(Player player) {
        // TODO: remove this restriction
        Sidebar sidebar = ScoreboardManagerProviderImpl.instance().sidebar(player);
        if (sidebar != null && sidebar != this)
            throw new IllegalArgumentException("Player " + player.getName() + " already has a sidebar");
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Sidebar is closed");
        }
    }

    private void checkLine(int line) {
        if (line > lines.length || line < 0) {
            throw new IndexOutOfBoundsException("Invalid line " + line);
        }
    }
}
