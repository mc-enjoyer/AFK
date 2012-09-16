package com.github.alesvojta.afk;

import org.bukkit.configuration.Configuration;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
public class Config {

    private final Configuration cfg;

    /**
     * Konstruktor vytvoří konfigurační soubory.
     *
     * @param plugin Plugin
     */
    public Config(AFK plugin) {
        this.cfg = plugin.getConfig();
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Událost pohybu.
     *
     * @return Boolean
     */
    public boolean onPlayerMove() {
        return cfg.getBoolean("Events.move");
    }

    /**
     * Událost chatu.
     *
     * @return Boolean
     */
    public boolean onPlayerMessage() {
        return cfg.getBoolean("Events.chat");
    }

    /**
     * Barva zpráv serveru.
     *
     * @return String
     */
    public String serverMessagesColor() {
        return cfg.getString("Colors.message").toUpperCase();
    }

    /**
     * Barva jména v seznamu hráčů.
     *
     * @return String
     */
    public String playerListColor() {
        return cfg.getString("Colors.player").toUpperCase();
    }

    /**
     * Počítadlo nečinnosti.
     *
     * @return Boolean
     */
    public boolean idleTimer() {
        return cfg.getBoolean("IdleTimer.enabled");
    }

    /**
     * Počet sekund.
     *
     * @return Integer
     */
    public int idleTime() {
        return cfg.getInt("IdleTimer.period");
    }

    /**
     * Zprávy serveru.
     *
     * @return Boolean
     */
    public boolean serverMessages() {
        return cfg.getBoolean("Messages.enabled");
    }

    /**
     * AFK zpráva. Vypíše zprávu, pokud je hráč AFK.
     *
     * @return String
     */
    public String toAfk() {
        return cfg.getString("Messages.+afk");
    }

    /**
     * AFK zpráva. Vypíše zprávu, pokud se hráč vrátí ke hře.
     *
     * @return String
     */
    public String noAfk() {
        return cfg.getString("Messages.-afk");
    }
}