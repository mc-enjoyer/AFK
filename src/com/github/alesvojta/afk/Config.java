package com.github.alesvojta.afk;

import org.bukkit.configuration.Configuration;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
class Config {

    private final Configuration cfg;

    /**
     * Constructor initializes variables and saves the config file.
     *
     * @param plugin Plugin
     */
    protected Config(AFK plugin) {
        this.cfg = plugin.getConfig();
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Move event.
     *
     * @return Boolean
     */
    protected boolean onPlayerMove() {
        return cfg.getBoolean("Events.move");
    }

    /**
     * Chat event.
     *
     * @return Boolean
     */
    protected boolean onPlayerMessage() {
        return cfg.getBoolean("Events.chat");
    }

    /**
     * Server messages color.
     *
     * @return String
     */
    protected String serverMessagesColor() {
        return cfg.getString("Colors.message").toUpperCase();
    }

    /**
     * Player List AFK name color.
     *
     * @return String
     */
    protected String playerListColor() {
        return cfg.getString("Colors.player").toUpperCase();
    }

    /**
     * Idle Timer.
     *
     * @return Boolean
     */
    protected boolean idleTimer() {
        return cfg.getBoolean("IdleTimer.enabled");
    }

    /**
     * Number of seconds.
     *
     * @return Integer
     */
    protected int idleTime() {
        return cfg.getInt("IdleTimer.period");
    }

    /**
     * Server messages.
     *
     * @return Boolean
     */
    protected boolean serverMessages() {
        return cfg.getBoolean("Messages.enabled");
    }

    /**
     * AFK message. When the Player become AFK.
     *
     * @return String
     */
    protected String toAfk() {
        return cfg.getString("Messages.+afk");
    }

    /**
     * AFK message. When the Player cancel AFK.
     *
     * @return String
     */
    protected String noAfk() {
        return cfg.getString("Messages.-afk");
    }
}