package com.github.alesvojta.afk;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 */
class Events implements Listener {

    private final AFK plugin;
    private final HashMap<Player, Integer> task;

    /**
     * Konstruktor inicializuje proměnné a zavede registr událostí.
     *
     * @param plugin Plugin
     */
    public Events(AFK plugin) {
        this.task = new HashMap<Player, Integer>();
        this.plugin = plugin;
    }

    /**
     * Pokud se hráč pohne o 1 celý blok, je mu zrušen AFK status.
     *
     * @param evt Událost pohybu
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent evt) {
        if (plugin.afkPlayerMap.containsKey(evt.getPlayer()) && plugin.cfg.onPlayerMove()) {
            int movX = evt.getFrom().getBlockX() - evt.getTo().getBlockX();
            int movZ = evt.getFrom().getBlockZ() - evt.getTo().getBlockZ();

            if (Math.abs(movX) > 0 || Math.abs(movZ) > 0) {
                plugin.cancelAFK(evt.getPlayer());
            }
        }
    }

    /**
     * Pokud hráč použije chat, je mu zrušen AFK status.
     *
     * @param evt Událost chatu
     */
    @EventHandler
    private void onPlayerMessage(AsyncPlayerChatEvent evt) {
        if (plugin.afkPlayerMap.containsKey(evt.getPlayer()) && plugin.cfg.onPlayerMessage()) {
            plugin.cancelAFK(evt.getPlayer());
        }
    }

    /**
     * Pokud se hráč odpojí ze hry, je mu automaticky rušen AFK status i
     * počítadlo nečinnosti.
     *
     * @param evt Událost odpojení
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent evt) {
        plugin.afkPlayerMap.remove(evt.getPlayer());
        if (plugin.cfg.idleTimer()) {
            plugin.getServer().getScheduler().cancelTask(task.get(evt.getPlayer()));
            task.remove(evt.getPlayer());
        }
    }

    /**
     * Pokud se hráč připojí ke hře, nastavuje se mu automaticky počítadlo
     * nečinnosti.
     *
     * @param evt Událost připojení
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent evt) {
        if (plugin.cfg.idleTimer()) {
            int id = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new IdleTimer(evt.getPlayer(), plugin), 20L * plugin.cfg.idleTime(), 20L * plugin.cfg.idleTime());
            task.put(evt.getPlayer(), id);
        }
    }
}
