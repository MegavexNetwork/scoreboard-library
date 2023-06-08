package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_20_R1.team;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_20_R1.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_20_R1.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperTeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public PaperTeamsPacketAdapterImpl(PacketAdapterImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public @NotNull TeamsPacketAdapter.TeamDisplayPacketAdapter<Component> createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    final ClientboundSetPlayerTeamPacket.Parameters parameters = parametersConstructor.invoke();
    protected final ClientboundSetPlayerTeamPacket createPacket = createTeamsPacket(MODE_CREATE, teamName(), parameters, null);
    protected final ClientboundSetPlayerTeamPacket updatePacket = createTeamsPacket(MODE_UPDATE, teamName(), parameters, null);
    private Component displayName, prefix, suffix;

    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void updateTeamPackets(@NotNull Collection<String> entries) {
      fillParameters(parameters, null);
      fillTeamPacket(createPacket, entries);
      fillTeamPacket(updatePacket, entries);
    }

    @Override
    public void createTeam(@NotNull Collection<Player> players) {
      packetAdapter().sendPacket(players, createPacket);
    }

    @Override
    public void updateTeam(@NotNull Collection<Player> players) {
      packetAdapter().sendPacket(players, updatePacket);
    }

    @Override
    protected void fillParameters(ClientboundSetPlayerTeamPacket.Parameters parameters, Locale locale) {
      super.fillParameters(parameters, locale);

      if (properties.displayName() != displayName) {
        var vanilla = NativeAdventureUtil.fromAdventureComponent(properties.displayName());
        UnsafeUtilities.setField(displayNameField, parameters, vanilla);
        displayName = properties.displayName();
      }

      if (properties.prefix() != prefix) {
        var vanilla = NativeAdventureUtil.fromAdventureComponent(properties.prefix());
        UnsafeUtilities.setField(prefixField, parameters, vanilla);
        prefix = properties.prefix();
      }

      if (properties.suffix() != suffix) {
        var vanilla = NativeAdventureUtil.fromAdventureComponent(properties.suffix());
        UnsafeUtilities.setField(suffixField, parameters, vanilla);
        suffix = properties.suffix();
      }
    }
  }
}
