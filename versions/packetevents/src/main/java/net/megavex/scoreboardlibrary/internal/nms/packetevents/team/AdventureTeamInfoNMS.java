package net.megavex.scoreboardlibrary.internal.nms.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import java.util.Collection;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.packetevents.NMSImpl;
import org.bukkit.entity.Player;

public class AdventureTeamInfoNMS extends TeamNMS.TeamInfoNMS<Component> {
  private final TeamNMS<PacketWrapper<?>, NMSImpl> teamNMS;
  private final ComponentTranslator componentTranslator;

  public AdventureTeamInfoNMS(TeamNMS<PacketWrapper<?>, NMSImpl> teamNMS, ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
    super(properties);
    this.teamNMS = teamNMS;
    this.componentTranslator = componentTranslator;
  }

  @Override
  public void addEntries(Collection<Player> players, Collection<String> entries) {
    teamNMS.impl.sendPacket(
      players,
      new WrapperPlayServerTeams(
        teamNMS.teamName,
        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
        Optional.empty(),
        entries
      )
    );
  }

  @Override
  public void removeEntries(Collection<Player> players, Collection<String> entries) {
    teamNMS.impl.sendPacket(
      players,
      new WrapperPlayServerTeams(
        teamNMS.teamName,
        WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES,
        Optional.empty(),
        entries
      )
    );
  }

  @Override
  public void createTeam(Collection<Player> players) {
    sendTeamPacket(players, false);
  }

  @Override
  public void updateTeam(Collection<Player> players) {
    sendTeamPacket(players, true);
  }

  private void sendTeamPacket(Collection<Player> players, boolean update) {
    ScoreboardManagerNMS.sendLocalePackets(null, teamNMS.impl, players, locale -> {
      var displayName = componentTranslator.translate(properties.displayName(), locale);
      var prefix = componentTranslator.translate(properties.prefix(), locale);
      var suffix = componentTranslator.translate(properties.suffix(), locale);
      var nameTagVisibility = WrapperPlayServerTeams.NameTagVisibility.values()[properties.nameTagVisibility().ordinal()];
      var collisionRule = WrapperPlayServerTeams.CollisionRule.values()[properties.collisionRule().ordinal()];
      var color = properties.playerColor();
      var optionData = WrapperPlayServerTeams.OptionData.fromValue((byte) properties.packOptions());

      var info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
        displayName,
        prefix,
        suffix,
        nameTagVisibility,
        collisionRule,
        color,
        optionData
      );

      return new WrapperPlayServerTeams(
        teamNMS.teamName,
        update ? WrapperPlayServerTeams.TeamMode.UPDATE:WrapperPlayServerTeams.TeamMode.CREATE,
        Optional.of(info),
        properties.entries()
      );
    });
  }
}

