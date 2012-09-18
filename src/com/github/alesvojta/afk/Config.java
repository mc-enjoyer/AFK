package com.github.alesvojta.afk;

import org.bukkit.configuration.Configuration;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
public class Config {

    private final Configuration cfg;

    /**
     * Constructor initializes variables and saves the config file.
     *
     * @param plugin Plugin
     */
    public Config(AFK plugin) {
        this.cfg = plugin.getConfig();
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Move event.
     *
     * @return Boolean
     */
    public boolean onPlayerMove() {
        return cfg.getBoolean("Events.move");
    }

    /**
     * Chat event.
     *
     * @return Boolean
     */
    public boolean onPlayerMessage() {
        return cfg.getBoolean("Events.chat");
    }

    /**
     * Server messages color.
     *
     * @return String
     */
    public String serverMessagesColor() {
        return cfg.getString("Colors.message").toUpperCase();
    }

    /**
     * Player List AFK name color.
     *
     * @return String
     */
    public String playerListColor() {
        return cfg.getString("Colors.player").toUpperCase();
    }

    /**
     * Idle Timer.
     *
     * @return Boolean
     */
    public boolean idleTimer() {
        return cfg.getBoolean("IdleTimer.enabled");
    }

    /**
     * Number of seconds.
     *
     * @return Integer
     */
    public int idleTime() {
        return cfg.getInt("IdleTimer.period");
    }

    /**
     * Server messages.
     *
     * @return Boolean
     */
    public boolean serverMessages() {
        return cfg.getBoolean("Messages.enabled");
    }

    /**
     * AFK message. When the Player become AFK.
     *
     * @return String
     */
    public String toAfk() {
        return cfg.getString("Messages.+afk");
    }

    /**
     * AFK message. When the Player cancel AFK.
     *
     * @return String
     */
    public String noAfk() {
        return cfg.getString("Messages.-afk");
    }
}