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
     * @return {Boolean}
     */
    public boolean onPlayerMove() {
        return cfg.getBoolean("onPlayerMove");
    }

    /**
     * Událost chatu.
     *
     * @return {Boolean}
     */
    public boolean onPlayerMessage() {
        return cfg.getBoolean("onPlayerMessage");
    }

    /**
     * Zprávy serveru.
     *
     * @return {Boolean}
     */
    public boolean serverMessages() {
        return cfg.getBoolean("serverMessages");
    }

    /**
     * Počítadlo nečinnosti.
     *
     * @return {Boolean}
     */
    public boolean idleTimer() {
        return cfg.getBoolean("idleTimer");
    }

    /**
     * Počet sekund.
     *
     * @return {Integer}
     */
    public int idleTime() {
        return cfg.getInt("idleTime");
    }

    /**
     * AFK zpráva. Vypíše zprávu, pokud je hráč AFK.
     *
     * @return {String}
     */
    public String toAfk() {
        return cfg.getString("toAfk");
    }

    /**
     * AFK zpráva. Vypíše zprávu, pokud se hráč vrátí ke hře.
     *
     * @return {String}
     */
    public String fromAfk() {
        return cfg.getString("fromAfk");
    }

    /**
     * Barva jména v seznamu hráčů.
     *
     * @return {String}
     */
    public String playerListColor() {
        return cfg.getString("playerColor").toUpperCase();
    }

    /**
     * Barva zpráv serveru.
     *
     * @return {String}
     */
    public String serverMessagesColor() {
        return cfg.getString("messageColor").toUpperCase();
    }
}
