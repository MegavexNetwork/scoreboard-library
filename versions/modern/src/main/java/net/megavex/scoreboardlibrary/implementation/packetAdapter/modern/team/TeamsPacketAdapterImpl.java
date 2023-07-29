package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterImpl;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public TeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public @NotNull TeamsPacketAdapter.TeamDisplayPacketAdapter<Component> createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void createTeam(@NotNull Collection<Player> players) {
      sendTeamPacket(players, true);
    }

    @Override
    public void updateTeam(@NotNull Collection<Player> players) {
      sendTeamPacket(players, false);
    }

    private void sendTeamPacket(Collection<Player> players, boolean create) {
      LocalePacketUtil.sendLocalePackets(packetAdapter().localeProvider, null, packetAdapter(), players, locale -> {
        var parameters = parametersConstructor.invoke();
        fillParameters(parameters, locale);
        return createTeamsPacket(create ? MODE_CREATE : MODE_UPDATE, teamName(), parameters, properties.entries());
      });
    }

    @Override
    protected void fillParameters(ClientboundSetPlayerTeamPacket.Parameters parameters, Locale locale) {
      super.fillParameters(parameters, locale);

      var vanilla = packetAdapter().fromAdventure(properties.displayName(), locale);
      UnsafeUtil.setField(displayNameField, parameters, vanilla);

      vanilla = packetAdapter().fromAdventure(properties.prefix(), locale);
      UnsafeUtil.setField(prefixField, parameters, vanilla);

      vanilla = packetAdapter().fromAdventure(properties.suffix(), locale);
      UnsafeUtil.setField(suffixField, parameters, vanilla);
    }
  }
}
