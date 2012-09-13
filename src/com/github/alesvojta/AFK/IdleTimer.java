package com.github.alesvojta.AFK;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
public class IdleTimer implements Runnable {

    private Player player;
    private AFK plugin;
    private Location lastLocation;

    public IdleTimer(Player player, AFK plugin) {
        this.player = player;
        this.plugin = plugin;
        updatePlayer();
    }

    /**
     * Pokud hráč stojí na jednom místě po určenou dobu (config/idleTime)
     * nastaví se mu AFK status.
     */
    @Override
    public void run() {
        if (player.getLocation().equals(lastLocation) && !plugin.afkPlayerMap.containsKey(player)) {
            plugin.becomeAFK(player);
            return;
        }
        updatePlayer();
    }

    /**
     * Aktualizuje hráčovu pozici.
     */
    private void updatePlayer() {
        lastLocation = player.getLocation();
    }
}
