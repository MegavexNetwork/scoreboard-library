package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.ScoreboardManagerNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;

public class TeamNMSImpl extends AbstractTeamNMSImpl {

    public TeamNMSImpl(NMSImpl impl, String teamName) {
        super(impl, teamName);
    }

    @Override
    public TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties) {
        return new TeamInfoNMSImpl(properties);
    }

    private class TeamInfoNMSImpl extends AbstractTeamNMSImpl.TeamInfoNMSImpl {

        public TeamInfoNMSImpl(ImmutableTeamProperties<Component> properties) {
            super(properties);
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
            ScoreboardManagerNMS.sendLocaleDependantPackets(null, impl, players, locale -> {
                ClientboundSetPlayerTeamPacket.Parameters parameters = parametersConstructor.invoke();
                fillParameters(parameters, locale);
                return createTeamsPacket(create ? MODE_CREATE : MODE_UPDATE, teamName, parameters, properties.entries());
            });
        }

        @Override
        protected void fillParameters(ClientboundSetPlayerTeamPacket.Parameters parameters, Locale locale) {
            super.fillParameters(parameters, locale);

            net.minecraft.network.chat.Component vanilla = impl.fromAdventure(properties.displayName(), locale);
            UnsafeUtilities.setField(displayNameField, parameters, vanilla);

            vanilla = impl.fromAdventure(properties.prefix(), locale);
            UnsafeUtilities.setField(prefixField, parameters, vanilla);

            vanilla = impl.fromAdventure(properties.suffix(), locale);
            UnsafeUtilities.setField(suffixField, parameters, vanilla);
        }
    }
}
