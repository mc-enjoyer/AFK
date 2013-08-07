package com.github.alesvojta.afk;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import static org.bukkit.Bukkit.*;

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
    Config(AFK plugin) {
        this.cfg = plugin.getConfig();
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Move event.
     *
     * @return Boolean
     */
    boolean onPlayerMove() {
        return cfg.getBoolean("Events.move");
    }

    /**
     * Chat event.
     *
     * @return Boolean
     */
    boolean onPlayerMessage() {
        return cfg.getBoolean("Events.chat");
    }

    /**
     * Server messages color.
     *
     * @return ChatColor
     */
    ChatColor serverMessagesColor() {
        try {
            return ChatColor.valueOf(cfg.getString("Colors.message").toUpperCase());
        } catch (IllegalArgumentException ex) {
            getLogger().warning("[AFK] " + ex.toString());
            return ChatColor.GRAY;
        }
    }

    /**
     * Player List AFK name color.
     *
     * @return ChatColor
     */
    ChatColor playerListColor() {
        try {
            return ChatColor.valueOf(cfg.getString("Colors.player").toUpperCase());
        } catch (IllegalArgumentException ex) {
            getLogger().warning("[AFK] " + ex.toString());
            return ChatColor.GRAY;
        }
    }

    /**
     * Idle Timer.
     *
     * @return Boolean
     */
    boolean idleTimer() {
        return cfg.getBoolean("IdleTimer.enabled");
    }

    /**
     * Number of seconds.
     *
     * @return Integer
     */
    int idleTime() {
        return cfg.getInt("IdleTimer.period");
    }

    /**
     * Server messages.
     *
     * @return Boolean
     */
    boolean serverMessages() {
        return cfg.getBoolean("Messages.enabled");
    }

    /**
     * AFK message. When the Player becomes AFK.
     *
     * @return String
     */
    String toAfk() {
        return cfg.getString("Messages.+afk");
    }

    /**
     * AFK message. When the Player cancels AFK.
     *
     * @return String
     */
    String noAfk() {
        return cfg.getString("Messages.-afk");
    }
}
