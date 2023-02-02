package com.vendoau.forceversion;

import net.kyori.adventure.text.Component;

import java.util.List;

public class ServerInfo {

    private final String name;
    private final List<Integer> versionWhitelist;
    private final List<Integer> versionBlacklist;
    private final Component kickMessage;

    public ServerInfo(String name, List<Integer> versionWhitelist, List<Integer> versionBlacklist, Component kickMessage) {
        this.name = name;
        this.versionWhitelist = versionWhitelist;
        this.versionBlacklist = versionBlacklist;
        this.kickMessage = kickMessage;
    }

    public String getName() {
        return name;
    }

    public List<Integer> whitelist() {
        return versionWhitelist;
    }

    public List<Integer> blacklist() {
        return versionBlacklist;
    }

    public Component kickMessage() {
        return kickMessage;
    }
}
