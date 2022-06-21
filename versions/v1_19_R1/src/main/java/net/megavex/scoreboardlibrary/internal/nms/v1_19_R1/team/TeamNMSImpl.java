package net.megavex.scoreboardlibrary.internal.nms.v1_19_R1.team;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_19_R1.NMSImpl;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;

public class TeamNMSImpl extends AbstractTeamNMSImpl {
  public TeamNMSImpl(NMSImpl impl, String teamName) {
    super(impl, teamName);
  }

  @Override
  public TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
    return new TeamInfoNMSImpl(properties, componentTranslator);
  }

  private class TeamInfoNMSImpl extends AbstractTeamNMSImpl.TeamInfoNMSImpl {

    private final ComponentTranslator componentTranslator;

    public TeamInfoNMSImpl(ImmutableTeamProperties<Component> properties, ComponentTranslator componentTranslator) {
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
      ScoreboardManagerNMS.sendLocalePackets(null, impl, players, locale -> {
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
