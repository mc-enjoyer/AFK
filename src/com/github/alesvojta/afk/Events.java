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
    private final HashMap<String, Integer> taskMap;

    /**
     * Constructor initializes variables.
     *
     * @param plugin Plugin
     */
    Events(AFK plugin) {
        this.taskMap = new HashMap<String, Integer>();
        this.plugin = plugin;
    }

    /**
     * Adds new Idle Timer to taskMap.
     *
     * @param event Player Event
     * @param id    Id
     */
    private void addTask(PlayerEvent event, int id) {
        this.taskMap.put(event.getPlayer().getName(), id);
    }

    /**
     * Removes Player from taskMap.
     *
     * @param event Player Event
     */
    private void removePlayer(PlayerEvent event) {
        this.taskMap.remove(event.getPlayer().getName());
    }

    /**
     * Returns Task ID.
     *
     * @param event Player Event
     * @return Integer
     */
    private int getTaskId(PlayerEvent event) {
        return this.taskMap.get(event.getPlayer().getName());
    }

    /**
     * When Player moves about 1 block is his AFK status canceled.
     *
     * @param event Player move event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerMove(PlayerMoveEvent event) {
        if (AFK.isPlayerAfk(event.getPlayer()) && plugin.getCfg().onPlayerMove()) {
            int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
            int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();

            if (Math.abs(movX) > 0 || Math.abs(movZ) > 0) {
                plugin.cancelAFK(event.getPlayer());
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
        if (AFK.isPlayerAfk(event.getPlayer()) && plugin.getCfg().onPlayerMessage()) {
            plugin.cancelAFK(event.getPlayer());
        }
    }

    /**
     * when Player logs out is his AFK status (possibly Idle Timer) canceled.
     *
     * @param event Player quit event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        AFK.removePlayerFromAfkMap(event.getPlayer());
        AFK.removePlayerFromTimeMap(event.getPlayer());
        if (plugin.getCfg().idleTimer()) {
            plugin.getServer().getScheduler().cancelTask(getTaskId(event));
            removePlayer(event);
        }
    }

    /**
     * when is Player kicked is his AFK status (possibly Idle Timer) canceled.
     *
     * @param event Player kick event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerKicked(PlayerKickEvent event) {
        AFK.removePlayerFromAfkMap(event.getPlayer());
        AFK.removePlayerFromTimeMap(event.getPlayer());
        if (plugin.getCfg().idleTimer()) {
            plugin.getServer().getScheduler().cancelTask(getTaskId(event));
            removePlayer(event);
        }
    }

    /**
     * When Player logs in, it automatically sets Idle Timer.
     *
     * @param event Player join event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getCfg().idleTimer()) {
            int id = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new IdleTimer(event.getPlayer(), plugin), 20L * plugin.getCfg().idleTime(), 20L * plugin.getCfg().idleTime());
            addTask(event, id);
        }
    }
}
