package com.github.alesvojta.afk;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
class Events implements Listener {

    private final AFK plugin;

    /**
     * Constructor initializes variables.
     *
     * @param plugin Plugin
     */
    Events(AFK plugin) {
        this.plugin = plugin;
    }

    /**
     * When Player moves about 1 block is his AFK status canceled.
     *
     * @param event Player move event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerMove(PlayerMoveEvent event) {
        if (AFK.isPlayerAfk(event.getPlayer().getName()) && plugin.getCfg().onPlayerMove()) {
            int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
            int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();

            if (Math.abs(movX) > 0 || Math.abs(movZ) > 0) {
                plugin.cancelAfk(event.getPlayer().getName());
            }
        }
    }

    /**
     * When Player uses chat is his AFK status canceled.
     *
     * @param event Player chat event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerMessage(AsyncPlayerChatEvent event) {
        if (AFK.isPlayerAfk(event.getPlayer().getName()) && plugin.getCfg().onPlayerMessage()) {
            plugin.cancelAfk(event.getPlayer().getName());
        }
    }

    /**
     * when Player logs out is his AFK status (possibly Idle Timer) canceled.
     *
     * @param event Player quit event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();

        AFK.removePlayerFromAfkMap(playerName);
        AFK.removePlayerFromTimeMap(playerName);
        plugin.getLocationMap().remove(playerName);
    }

    /**
     * when is Player kicked is his AFK status (possibly Idle Timer) canceled.
     *
     * @param event Player kick event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerKicked(PlayerKickEvent event) {
        String playerName = event.getPlayer().getName();

        AFK.removePlayerFromAfkMap(playerName);
        AFK.removePlayerFromTimeMap(playerName);
        plugin.getLocationMap().remove(playerName);
    }
}
