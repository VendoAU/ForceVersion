package com.vendoau.forceversion;

import com.viaversion.viaversion.api.Via;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.bstats.bungeecord.Metrics;

import java.io.IOException;
import java.util.UUID;

public class ForceVersionBungee extends Plugin implements Listener {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        new Metrics(this, 17614);
        try {
            configManager = new ConfigManager(getDataFolder(), true);
            getProxy().getPluginManager().registerListener(this, this);
        } catch (IOException e) {
            getLogger().severe("An error occurred while trying to read the config");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        final UUID uuid = event.getConnection().getUniqueId();
        final int version = Via.getAPI().getPlayerVersion(uuid);
        if (configManager.canJoinServer(version)) return;

        event.setCancelled(true);
        final BaseComponent[] cancelReason = BungeeComponentSerializer.get().serialize(configManager.getKickMessage());
        event.setCancelReason(cancelReason);
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
        final String server = event.getTarget().getName();
        if (configManager.canJoinServer(server, version)) return;

        final BaseComponent[] message = BungeeComponentSerializer.get().serialize(configManager.getKickMessage(server));
        if (player.isConnected()) {
            player.sendMessage(message);
        } else {
            player.disconnect(message);
        }
        event.setCancelled(true);
    }
}
