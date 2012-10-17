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
    protected Events(AFK plugin) {
        this.taskMap = new HashMap<String, Integer>();
        this.plugin = plugin;
    }

    /**
     * Adds new Idle Timer to taskMap.
     *
     * @param playerName Player
     * @param id         Id
     */
    private void addTask(String playerName, int id) {
        this.taskMap.put(playerName, id);
    }

    /**
     * Checks if taskMap contains player.
     *
     * @param playerName Player
     * @return Boolean
     */
    private boolean hasTask(String playerName) {
        return this.taskMap.containsKey(playerName);
    }

    /**
     * Removes Player from taskMap.
     *
     * @param playerName Player
     */
    private void removePlayer(String playerName) {
        this.taskMap.remove(playerName);
    }

    /**
     * Returns Task ID.
     *
     * @param playerName Player
     * @return Integer
     */
    private int getTaskId(String playerName) {
        return this.taskMap.get(playerName);
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
        if (AFK.isPlayerAfk(event.getPlayer().getName()) && plugin.getCfg().onPlayerMessage()) {
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
        String playerName = event.getPlayer().getName();

        AFK.removePlayerFromAfkMap(playerName);
        AFK.removePlayerFromTimeMap(playerName);
        if (plugin.getCfg().idleTimer() && hasTask(playerName)) {
            plugin.getServer().getScheduler().cancelTask(getTaskId(playerName));
            removePlayer(playerName);
        }
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
        if (plugin.getCfg().idleTimer() && hasTask(playerName)) {
            plugin.getServer().getScheduler().cancelTask(getTaskId(playerName));
            removePlayer(playerName);
        }
    }

    /**
     * When Player logs in, it automatically sets Idle Timer.
     *
     * @param event Player join event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (plugin.getCfg().idleTimer() && !hasTask(playerName)) {
            int id = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new IdleTimer(playerName, plugin), 20L * plugin.getCfg().idleTime(), 20L * plugin.getCfg().idleTime());
            addTask(playerName, id);
        }
    }
}
