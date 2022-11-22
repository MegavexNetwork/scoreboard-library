package net.megavex.scoreboardlibrary.internal.nms.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.packetevents.NMSImpl;
import org.bukkit.entity.Player;

public class TeamNMSImpl extends TeamNMS<PacketWrapper<?>, NMSImpl> {
  private WrapperPlayServerTeams removePacket;

  public TeamNMSImpl(NMSImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public void removeTeam(Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = new WrapperPlayServerTeams(
        teamName,
        WrapperPlayServerTeams.TeamMode.REMOVE,
        Optional.empty()
      );
    }

    impl.sendPacket(players, removePacket);
  }

  @Override
  public TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
    return new AdventureTeamInfoNMS(this, properties, componentTranslator);
  }

  @Override
  public TeamInfoNMS<String> createLegacyTeamInfoNMS(ImmutableTeamProperties<String> properties) {
    return new LegacyTeamInfoNMS(this, properties);
  }
}
