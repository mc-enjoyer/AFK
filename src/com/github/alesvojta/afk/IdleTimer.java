package com.github.alesvojta.afk;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
class IdleTimer implements Runnable {

    private final AFK plugin;
    private HashMap<String, Location> locationMap;

    /**
     * Constructor initialize variables.
     * It calls updatePlayer() which updates Players location.
     *
     * @param plugin AFK Plugin
     */
    protected IdleTimer(AFK plugin) {
        this.plugin = plugin;
        this.locationMap = new HashMap<String, Location>();
    }

    /**
     * If Player stands on one place for specified amount of time, it sets the player AFK.
     */
    @Override
    public void run() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (this.locationMap.containsKey(player.getName())) {
                if (plugin.getLocationMap().get(player.getName()).equals(player.getLocation()) && !AFK.isPlayerAfk(player.getName())) {
                    long idleTime = plugin.getCfg().idleTime() * 20;
                    player.setPlayerTime(-idleTime, true);
                    plugin.becomeAfk(player.getName());
                }
            }
            plugin.getLocationMap().put(player.getName(), player.getLocation());
        }
    }
}
