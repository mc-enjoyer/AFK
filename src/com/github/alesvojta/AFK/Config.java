package com.github.alesvojta.AFK;

import org.bukkit.configuration.Configuration;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
public class Config {

    private Configuration cfg;

    /**
     * Konstruktor vytvoří konfigurační soubory.
     *
     * @param plugin {com.github.alesvojta.AFK.AFK} Plugin
     */
    public Config(AFK plugin) {
        this.cfg = plugin.getConfig();
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {boolean} Při události pohybu
     */
    public boolean onPlayerMove() {
        return cfg.getBoolean("onPlayerMove");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {boolean} Při události chatu
     */
    public boolean onPlayerMessage() {
        return cfg.getBoolean("onPlayerMessage");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {boolean} Zprávy serveru
     */
    public boolean serverMessages() {
        return cfg.getBoolean("serverMessages");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {boolean} Počítadlo nečinnosti
     */
    public boolean idleTimer() {
        return cfg.getBoolean("idleTimer");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {Integer} Počet vteřin
     */
    public int idleTime() {
        return cfg.getInt("idleTime");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {String} Text +AFK zpráva
     */
    public String toAfk() {
        return cfg.getString("toAfk");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {String} Text -AFK zpráva
     */
    public String fromAfk() {
        return cfg.getString("fromAfk");
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {String} Barva jmen v PlayerListu
     */
    public String playerListColor() {
        return cfg.getString("playerColor").toUpperCase();
    }

    /**
     * Vrací hodnotu z konfiguračního souboru.
     *
     * @return {String} Barva zpráv serveru
     */
    public String serverMessagesColor() {
        return cfg.getString("messageColor").toUpperCase();
    }
}
