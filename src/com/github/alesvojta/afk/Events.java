package com.github.alesvojta.afk;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
class Events implements Listener {

    private final AFK plugin;
    private final HashMap<Player, Integer> task;

    /**
     * Constructor initializes variables.
     *
     * @param plugin Plugin
     */
    public Events(AFK plugin) {
        this.task = new HashMap<Player, Integer>();
        this.plugin = plugin;
    }

    /**
     * When Player moves about 1 block is his AFK status canceled.
     *
     * @param event Player move event
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.afkPlayerMap.containsKey(event.getPlayer()) && plugin.cfg.onPlayerMove()) {
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
    @EventHandler
    private void onPlayerMessage(AsyncPlayerChatEvent event) {
        if (plugin.afkPlayerMap.containsKey(event.getPlayer()) && plugin.cfg.onPlayerMessage()) {
            plugin.cancelAFK(event.getPlayer());
        }
    }

    /**
     * when Player logs out is his AFK status (possibly Idle Timer) canceled.
     *
     * @param event Player quit event
     */
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        plugin.afkPlayerMap.remove(event.getPlayer());
        if (plugin.cfg.idleTimer()) {
            plugin.getServer().getScheduler().cancelTask(task.get(event.getPlayer()));
            task.remove(event.getPlayer());
        }
    }

    /**
     * when is Player kicked is his AFK status (possibly Idle Timer) canceled.
     *
     * @param event Událost vykopnutí
     */
    @EventHandler
    private void onPlayerKicked(PlayerKickEvent event) {
        plugin.afkPlayerMap.remove(event.getPlayer());
        if (plugin.cfg.idleTimer()) {
            plugin.getServer().getScheduler().cancelTask(task.get(event.getPlayer()));
        }
    }

    /**
     * When Player logs in, it automatically sets Idle Timer.
     *
     * @param event Událost připojení
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.cfg.idleTimer()) {
            int id = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new IdleTimer(event.getPlayer(), plugin), 20L * plugin.cfg.idleTime(), 20L * plugin.cfg.idleTime());
            task.put(event.getPlayer(), id);
        }
    }
}
