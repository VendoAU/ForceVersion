package com.vendoau.forceversion;

import com.viaversion.viaversion.api.Via;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ForceVersionBukkit extends JavaPlugin implements Listener {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        try {
            configManager = new ConfigManager(getDataFolder(), false);
            getServer().getPluginManager().registerEvents(this, this);
            new Metrics(this, 17606);
        } catch (IOException e) {
            getLogger().severe("An error occurred while trying to read the config");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
        if (configManager.canJoinServer(version)) return;

        final String message = BukkitComponentSerializer.legacy().serialize(configManager.getKickMessage());
        player.kickPlayer(message);
    }

    // TODO: Modify ServerListPingEvent version
    // As far as I can tell this is not really possible to do yet
}
