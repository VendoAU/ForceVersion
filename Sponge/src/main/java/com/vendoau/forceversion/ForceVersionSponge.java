package com.vendoau.forceversion;

import com.google.inject.Inject;
import com.viaversion.viaversion.api.Via;
import org.apache.logging.log4j.Logger;
import org.bstats.sponge.Metrics;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.IOException;
import java.nio.file.Path;

@Plugin("forceversion")
public class ForceVersionSponge {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;

    private ConfigManager configManager;

    @Inject
    public ForceVersionSponge(Metrics.Factory metricsFactory) {
        metricsFactory.make(17607);
    }

    @Listener
    public void onServerStart(StartedEngineEvent<Server> event) {
        try {
            configManager = new ConfigManager(configDirectory.toFile(), false);
        } catch (IOException e) {
            logger.error("An error occurred while trying to read the config");
            e.printStackTrace();
        }
    }

    @Listener
    public void onRefreshGame(RefreshGameEvent event) {
        try {
            configManager.loadConfig();
        } catch (ConfigurateException e) {
            logger.error("An error occurred while trying to refresh the config");
            e.printStackTrace();
        }
    }

    @Listener
    public void onJoin(ServerSideConnectionEvent.Join event) {
        final ServerPlayer player = event.player();
        final int version = Via.getAPI().getPlayerVersion(player.uniqueId());
        if (configManager.canJoinServer(version)) return;

        player.kick(configManager.getKickMessage());
    }
}
