package net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.team;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.base.util.LocalePacketUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.NMSImpl;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;

public class TeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public TeamsPacketAdapterImpl(NMSImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public TeamInfoPacketAdapter<Component> createTeamInfoAdapter(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
    return new TeamInfoPacketAdapterImpl(properties, componentTranslator);
  }

  private class TeamInfoPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamInfoPacketAdapterImpl {
    private final ComponentTranslator componentTranslator;

    public TeamInfoPacketAdapterImpl(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
      super(properties);
      this.componentTranslator = componentTranslator;
    }

    @Override
    public void createTeam(Collection<Player> players) {
      sendTeamPacket(players, true);
    }

    @Override
    public void updateTeam(Collection<Player> players) {
      sendTeamPacket(players, false);
    }

    private void sendTeamPacket(Collection<Player> players, boolean create) {
      LocalePacketUtilities.sendLocalePackets(impl.localeProvider, null, impl, players, locale -> {
        var parameters = parametersConstructor.invoke();
        fillParameters(parameters, locale);
        return createTeamsPacket(create ? MODE_CREATE:MODE_UPDATE, teamName, parameters, properties.entries());
      });
    }

    @Override
    protected void fillParameters(ClientboundSetPlayerTeamPacket.Parameters parameters, Locale locale) {
      super.fillParameters(parameters, locale);

      var vanilla = impl.fromAdventure(properties.displayName(), locale, componentTranslator);
      UnsafeUtilities.setField(displayNameField, parameters, vanilla);

      vanilla = impl.fromAdventure(properties.prefix(), locale, componentTranslator);
      UnsafeUtilities.setField(prefixField, parameters, vanilla);

      vanilla = impl.fromAdventure(properties.suffix(), locale, componentTranslator);
      UnsafeUtilities.setField(suffixField, parameters, vanilla);
    }
  }
}
