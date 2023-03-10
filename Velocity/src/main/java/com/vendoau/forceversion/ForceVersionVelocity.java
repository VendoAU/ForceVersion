package com.vendoau.forceversion;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import org.bstats.velocity.Metrics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "forceversion",
        name = "ForceVersion",
        description = "${description}",
        version = "${version}",
        authors = "VendoAU",
        url = "${website}"
)
public class ForceVersionVelocity {

    private ConfigManager configManager;

    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    @Inject
    public ForceVersionVelocity(Logger logger, Metrics.Factory metricsFactory, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        metricsFactory.make(this, 17613);
        try {
            configManager = new ConfigManager(dataDirectory.toFile(), true);
        } catch (IOException e) {
            logger.severe("An error occurred while trying to read the config");
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent event) {
        final int version = event.getConnection().getProtocolVersion().getProtocol();
        if (configManager.canJoinServer(version)) return;

        event.setResult(PreLoginEvent.PreLoginComponentResult.denied(configManager.getKickMessage()));
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        final Player player = event.getPlayer();
        final String server = event.getOriginalServer().getServerInfo().getName();
        final int version = player.getProtocolVersion().getProtocol();
        if (configManager.canJoinServer(server, version)) return;

        event.setResult(ServerPreConnectEvent.ServerResult.denied());
        final Component message = configManager.getKickMessage(server);
        if (player.isActive()) {
            player.sendMessage(message);
        } else {
            player.disconnect(message);
        }
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        final int version = event.getConnection().getProtocolVersion().getProtocol();
        if (configManager.canJoinServer(version)) return;

        final ServerPing ping = event.getPing();
        final ServerPing.Builder builder = ping.asBuilder();

        final int serverPingVersion = configManager.getServerPingVersion();
        final String serverPingVersionName = configManager.getServerPingVersionName();
        if (serverPingVersionName == null) {
            builder.version(new ServerPing.Version(serverPingVersion, ping.getVersion().getName()));
        } else {
            builder.version(new ServerPing.Version(serverPingVersion, serverPingVersionName));
        }
        event.setPing(builder.build());
    }
}