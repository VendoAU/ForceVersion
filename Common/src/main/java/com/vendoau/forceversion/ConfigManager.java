package com.vendoau.forceversion;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final boolean proxy;

    private final YamlConfigurationLoader configLoader;

    private Component messagePrefix;
    private ServerInfo global;
    private final List<ServerInfo> infoList = new ArrayList<>();

    public ConfigManager(File dataDirectory, boolean proxy) throws IOException {
        this.proxy = proxy;

        final File configFile = new File(dataDirectory, "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            Files.copy(ConfigManager.class.getResourceAsStream("/config.yml"), configFile.toPath());
        }

        configLoader = YamlConfigurationLoader.builder().file(configFile).build();
        loadConfig();
    }

    public void loadConfig() throws ConfigurateException {
        final CommentedConfigurationNode config = configLoader.load();

        messagePrefix = MiniMessage.miniMessage().deserialize(config.node("messagePrefix").getString());
        global = getServerInfo(config.node("global"));

        if (!proxy) return;
        infoList.clear();
        for (CommentedConfigurationNode server : config.node("servers").childrenMap().values()) {
            infoList.add(getServerInfo(server));
        }
    }

    private ServerInfo getServerInfo(CommentedConfigurationNode node) throws SerializationException {
        final String server = String.valueOf(node.key());
        final List<Integer> versionWhitelist = node.node("versionWhitelist").getList(Integer.class);
        final List<Integer> versionBlacklist = node.node("versionBlacklist").getList(Integer.class);

        final String kickMessage = node.node("kickMessage").getString();
        final Component kickMessageComponent;
        if (kickMessage == null) {
            if (global == null) {
                kickMessageComponent = Component.empty();
            } else {
                kickMessageComponent = global.kickMessage();
            }
        } else {
            kickMessageComponent = MiniMessage.miniMessage().deserialize(kickMessage);
        }

        return new ServerInfo(server, versionWhitelist, versionBlacklist, kickMessageComponent);
    }

    public Component getKickMessage(@Nullable String server) {
        final ServerInfo info = getInfo(server);
        return messagePrefix.append(info.kickMessage());
    }

    public Component getKickMessage() {
        return getKickMessage(null);
    }

    public boolean canJoinServer(@Nullable String server, int protocol) {
        final ServerInfo info = getInfo(server);
        final List<Integer> whitelist = info.whitelist();
        final List<Integer> blacklist = info.blacklist();

        if (whitelist.isEmpty()) {
            if (blacklist.isEmpty()) return true;
            return !blacklist.contains(protocol);
        }
        return whitelist.contains(protocol);
    }

    public boolean canJoinServer(int protocol) {
        return canJoinServer(null, protocol);
    }

    private ServerInfo getInfo(@Nullable String server) {
        if (!proxy || server == null) return global;
        for (ServerInfo info : infoList) {
            if (info.getName().equalsIgnoreCase(server)) {
                return info;
            }
        }
        return global;
    }
}
