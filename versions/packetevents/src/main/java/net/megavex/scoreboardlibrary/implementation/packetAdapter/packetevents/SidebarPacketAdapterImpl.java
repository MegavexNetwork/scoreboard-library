package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.ObjectiveMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtilities;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.Component.empty;

public class SidebarPacketAdapterImpl extends SidebarPacketAdapter<PacketWrapper<?>, PacketAdapterImpl> {
  private final WrapperPlayServerScoreboardObjective createPacket, updatePacket;

  public SidebarPacketAdapterImpl(PacketAdapterImpl impl, Sidebar sidebar) {
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
  public void updateTitle(@NotNull Component displayName) {
    var locale = sidebar().locale();
    if (locale != null) {
      var translatedDisplayName = GlobalTranslator.render(displayName, locale);
      createPacket.setDisplayName(translatedDisplayName);
      updatePacket.setDisplayName(translatedDisplayName);
    }
  }

  @Override
  public void sendObjectivePacket(@NotNull Collection<Player> players, @NotNull ObjectivePacket type) {
    if (sidebar().locale() != null) {
      packetAdapter().sendPacket(players, type == ObjectivePacket.CREATE ? createPacket : updatePacket);
    } else {
      LocalePacketUtilities.sendLocalePackets(
        packetAdapter().localeProvider,
        sidebar().locale(),
        packetAdapter(),
        players,
        locale -> createObjectivePacket(
          type == ObjectivePacket.CREATE ? ObjectiveMode.CREATE : ObjectiveMode.UPDATE,
          sidebar().title(),
          locale
        )
      );
    }
  }

  @Override
  public void removeLine(@NotNull Collection<Player> players, @NotNull String line) {
    packetAdapter().sendPacket(
      players,
      new WrapperPlayServerUpdateScore(
        line,
        WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
        packetAdapter().objectiveName,
        Optional.empty()
      )
    );
  }

  @Override
  public void score(@NotNull Collection<Player> players, int score, @NotNull String line) {
    packetAdapter().sendPacket(
      players,
      new WrapperPlayServerUpdateScore(
        line,
        WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
        packetAdapter().objectiveName,
        Optional.of(score)
      )
    );
  }

  private WrapperPlayServerScoreboardObjective createObjectivePacket(ObjectiveMode mode, Component displayName, Locale locale) {
    return new WrapperPlayServerScoreboardObjective(
      packetAdapter().objectiveName,
      mode,
      GlobalTranslator.render(displayName, locale),
      WrapperPlayServerScoreboardObjective.RenderType.INTEGER
    );
  }
}
