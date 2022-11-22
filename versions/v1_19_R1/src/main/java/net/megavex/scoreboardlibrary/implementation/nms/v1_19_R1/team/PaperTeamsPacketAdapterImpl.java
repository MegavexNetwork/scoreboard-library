package net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.team;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.implementation.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.nms.base.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.NMSImpl;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;

public class PaperTeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public PaperTeamsPacketAdapterImpl(NMSImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
    return new TeamInfoNMSImpl(properties);
  }

  private class TeamInfoNMSImpl extends AbstractTeamsPacketAdapterImpl.TeamInfoNMSImpl {
    final ClientboundSetPlayerTeamPacket.Parameters parameters = parametersConstructor.invoke();
    protected final ClientboundSetPlayerTeamPacket createPacket = createTeamsPacket(TeamsPacketAdapter.MODE_CREATE, teamName, parameters, null);
    protected final ClientboundSetPlayerTeamPacket updatePacket = createTeamsPacket(TeamsPacketAdapter.MODE_UPDATE, teamName, parameters, null);
    private Component displayName, prefix, suffix;

    public TeamInfoNMSImpl(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void updateTeamPackets(Collection<String> entries) {
      fillParameters(parameters, null);
      fillTeamPacket(createPacket, entries);
      fillTeamPacket(updatePacket, entries);
    }

    @Override
    public void createTeam(Collection<Player> players) {
      impl.sendPacket(players, createPacket);
    }

    @Override
    public void updateTeam(Collection<Player> players) {
      impl.sendPacket(players, updatePacket);
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
