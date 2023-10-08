package net.megavex.scoreboardlibrary.implementation.packetAdapter.packetevents.team;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LegacyTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<String> {
  public LegacyTeamDisplayPacketAdapter(@NotNull PacketSender<PacketWrapper<?>> sender, @NotNull String teamName, @NotNull ImmutableTeamProperties<String> properties) {
    super(sender, teamName, properties);
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    LocalePacketUtil.sendLocalePackets(
      sender,
      players,
      locale -> new WrapperPlayServerTeamsLegacy(
        teamName,
        properties,
        mode(packetType)
      )
    );
  }
}

