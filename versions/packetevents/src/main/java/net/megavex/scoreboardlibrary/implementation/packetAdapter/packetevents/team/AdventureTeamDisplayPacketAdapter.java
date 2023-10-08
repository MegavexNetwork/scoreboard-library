package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AdventureTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<Component> {
  public AdventureTeamDisplayPacketAdapter(@NotNull PacketSender<PacketWrapper<?>> sender, @NotNull String teamName, @NotNull ImmutableTeamProperties<Component> properties) {
    super(sender, teamName, properties);
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    LocalePacketUtil.sendLocalePackets(
      sender,
      players,
      locale -> {
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
          teamName,
          mode(packetType),
          info,
          properties.entries()
        );
      });
  }
}

