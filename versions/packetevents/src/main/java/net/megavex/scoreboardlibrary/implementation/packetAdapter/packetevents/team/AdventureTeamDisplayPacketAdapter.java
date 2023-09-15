package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AdventureTeamDisplayPacketAdapter extends TeamsPacketAdapter.TeamDisplayPacketAdapter<Component> {
  private final TeamsPacketAdapter<PacketWrapper<?>, PacketAdapterImpl> packetAdapter;

  public AdventureTeamDisplayPacketAdapter(TeamsPacketAdapter<PacketWrapper<?>, PacketAdapterImpl> packetAdapter, ImmutableTeamProperties<Component> properties) {
    super(properties);
    this.packetAdapter = packetAdapter;
  }

  @Override
  public void addEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    packetAdapter.packetAdapter().sendPacket(
      players,
      new WrapperPlayServerTeams(
        packetAdapter.teamName(),
        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entries
      )
    );
  }

  @Override
  public void removeEntries(@NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    packetAdapter.packetAdapter().sendPacket(
      players,
      new WrapperPlayServerTeams(
        packetAdapter.teamName(),
        WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entries
      )
    );
  }

  @Override
  public void createTeam(@NotNull Collection<Player> players) {
    sendTeamPacket(players, false);
  }

  @Override
  public void updateTeam(@NotNull Collection<Player> players) {
    sendTeamPacket(players, true);
  }

  private void sendTeamPacket(Collection<Player> players, boolean update) {
    LocalePacketUtil.sendLocalePackets(packetAdapter.packetAdapter().localeProvider, null, packetAdapter.packetAdapter(), players, locale -> {
      Component displayName = GlobalTranslator.render(properties.displayName(), locale);
      Component prefix = GlobalTranslator.render(properties.prefix(), locale);
      Component suffix = GlobalTranslator.render(properties.suffix(), locale);
      WrapperPlayServerTeams.NameTagVisibility nameTagVisibility = WrapperPlayServerTeams.NameTagVisibility.values()[properties.nameTagVisibility().ordinal()];
      WrapperPlayServerTeams.CollisionRule collisionRule = WrapperPlayServerTeams.CollisionRule.values()[properties.collisionRule().ordinal()];
      NamedTextColor color = properties.playerColor() != null ? properties.playerColor() : NamedTextColor.WHITE;
      WrapperPlayServerTeams.OptionData optionData = WrapperPlayServerTeams.OptionData.fromValue((byte) properties.packOptions());

      WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
        displayName,
        prefix,
        suffix,
        nameTagVisibility,
        collisionRule,
        color,
        optionData
      );

      return new WrapperPlayServerTeams(
        packetAdapter.teamName(),
        update ? WrapperPlayServerTeams.TeamMode.UPDATE : WrapperPlayServerTeams.TeamMode.CREATE,
        info,
        properties.entries()
      );
    });
  }
}

