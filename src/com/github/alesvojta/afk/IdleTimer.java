package com.github.alesvojta.afk;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
class IdleTimer implements Runnable {

    private final AFK plugin;

    /**
     * Constructor initialize variables.
     * It calls updatePlayer() which updates Players location.
     *
     * @param plugin AFK Plugin
     */
    IdleTimer(AFK plugin) {
        this.plugin = plugin;
    }

    /**
     * If Player stands on one place for specified amount of time, it sets the player AFK.
     */
    @Override
    public void run() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (this.plugin.getLocationMap().containsKey(player.getName())) {
                if (this.plugin.getLocationMap().get(player.getName()).equals(player.getLocation()) && !AFK.isPlayerAfk(player.getName())) {
                    long idleTime = this.plugin.getCfg().idleTime() * 20;
                    player.setPlayerTime(-idleTime, true);
                    //this.plugin.becomeAfk(player.getName());
                    this.plugin.afk(player.getName(), false);
                }
            }
            this.plugin.getLocationMap().put(player.getName(), player.getLocation());
        }
    }
}
