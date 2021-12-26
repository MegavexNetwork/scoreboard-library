package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;

public class PaperTeamNMSImpl extends AbstractTeamNMSImpl {

    public PaperTeamNMSImpl(NMSImpl impl, String teamName) {
        super(impl, teamName);
    }

    @Override
    public TeamInfoNMS<Component> createTeamInfoNMS(ImmutableTeamProperties<Component> properties) {
        return new TeamInfoNMSImpl(properties);
    }

    private class TeamInfoNMSImpl extends AbstractTeamNMSImpl.TeamInfoNMSImpl {

        final ClientboundSetPlayerTeamPacket.Parameters parameters = parametersConstructor.invoke();
        protected final ClientboundSetPlayerTeamPacket createPacket = createTeamsPacket(TeamNMS.MODE_CREATE, teamName, parameters, null);
        protected final ClientboundSetPlayerTeamPacket updatePacket = createTeamsPacket(TeamNMS.MODE_UPDATE, teamName, parameters, null);
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
                UnsafeUtilities.setField(displayNameField, parameters, NativeAdventureUtil.fromAdventureComponent(properties.displayName()));
                displayName = properties.displayName();
            }

            if (properties.prefix() != prefix) {
                UnsafeUtilities.setField(prefixField, parameters, NativeAdventureUtil.fromAdventureComponent(properties.prefix()));
                prefix = properties.prefix();
            }

            if (properties.suffix() != suffix) {
                UnsafeUtilities.setField(suffixField, parameters, NativeAdventureUtil.fromAdventureComponent(properties.suffix()));
                suffix = properties.suffix();
            }
        }
    }
}
