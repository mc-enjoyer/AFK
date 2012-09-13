package com.github.alesvojta.AFK;

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
public class Events implements Listener {

    private Events thiz = this;
    private AFK plugin;
    private HashMap<Player, Integer> task;

    /**
     * Konstruktor inicializuje proměnné a zavede registr událostí.
     *
     * @param plugin {com.github.alesvojta.AFK.AFK} Plugin
     */
    public Events(AFK plugin) {
        this.task = new HashMap();
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(thiz, plugin);
    }

    /**
     * Pokud se hráč pohne o 1 celý blok, je mu zrušen AFK status.
     *
     * @param evt {org.bukkit.event.player.PlayerMoveEvent} Událost pohybu
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
     * @param evt {org.bukkit.event.player.AsyncPlayerChatEvent} Událost chatu
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
     * @param evt {org.bukkit.event.player.PlayerQuitEvent} Událost odpojení
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent evt) {
        plugin.afkPlayerMap.remove(evt.getPlayer());
        if (plugin.cfg.idleTimer()) {
            plugin.getServer().getScheduler().cancelTask((Integer) task.get(evt.getPlayer()).intValue());
            task.remove(evt.getPlayer());
        }
    }

    /**
     * Pokud se hráč připojí ke hře, nastavuje se mu automaticky počítadlo
     * nečinnosti.
     *
     * @param evt {org.bukkit.event.player.PlayerJoinEvent} Událost připojení
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent evt) {
        if (plugin.cfg.idleTimer()) {
            int id = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new IdleTimer(evt.getPlayer(), plugin), 20L * plugin.cfg.idleTime(), 20L * plugin.cfg.idleTime());
            task.put(evt.getPlayer(), id);
        }
    }
}
