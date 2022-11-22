package net.megavex.scoreboardlibrary.internal.nms.packetevents;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.ObjectiveMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.SidebarNMS;
import org.bukkit.entity.Player;


import static net.kyori.adventure.text.Component.empty;

public class SidebarNMSImpl extends SidebarNMS<PacketWrapper<?>, NMSImpl> {
  private final WrapperPlayServerScoreboardObjective createPacket, updatePacket;

  public SidebarNMSImpl(NMSImpl impl, Sidebar sidebar) {
    super(impl, sidebar);

    var locale = sidebar.locale();
    if (locale != null) {
      this.createPacket = createObjectivePacket(ObjectiveMode.CREATE, empty(), locale);
      this.updatePacket = createObjectivePacket(ObjectiveMode.UPDATE, empty(), locale);
    } else {
      this.createPacket = null;
      this.updatePacket = null;
    }
  }

  @Override
  public void updateTitle(Component displayName) {
    var locale = sidebar.locale();
    if (locale != null) {
      var translatedDisplayName = sidebar.componentTranslator().translate(displayName, locale);
      createPacket.setDisplayName(translatedDisplayName);
      updatePacket.setDisplayName(translatedDisplayName);
    }
  }

  @Override
  protected void sendObjectivePacket(Collection<Player> players, boolean create) {
    if (sidebar.locale() != null) {
      impl.sendPacket(players, create ? createPacket:updatePacket);
    } else {
      ScoreboardManagerNMS.sendLocalePackets(
        sidebar.locale(),
        impl,
        players,
        locale -> createObjectivePacket(
          create ? ObjectiveMode.CREATE:ObjectiveMode.UPDATE,
          sidebar.title(),
          locale
        )
      );
    }
  }

  @Override
  public void removeLine(Collection<Player> players, String line) {
    impl.sendPacket(
      players,
      new WrapperPlayServerUpdateScore(
        line,
        WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
        impl.objectiveName,
        Optional.empty()
      )
    );
  }

  @Override
  public void score(Collection<Player> players, int score, String line) {
    impl.sendPacket(
      players,
      new WrapperPlayServerUpdateScore(
        line,
        WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
        impl.objectiveName,
        Optional.of(score)
      )
    );
  }

  private WrapperPlayServerScoreboardObjective createObjectivePacket(ObjectiveMode mode, Component displayName, Locale locale) {
    return new WrapperPlayServerScoreboardObjective(
      impl.objectiveName,
      mode,
      sidebar.componentTranslator().translate(displayName, locale),
      WrapperPlayServerScoreboardObjective.RenderType.INTEGER
    );
  }
}
