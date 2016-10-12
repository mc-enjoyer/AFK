package com.github.alesvojta.afk;

import com.github.alesvojta.afk.AFK;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class Events implements Listener {

   private final AFK plugin;


   Events(AFK plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   private void onPlayerMove(PlayerMoveEvent event) {
      if(AFK.isPlayerAfk(event.getPlayer().getName()) && this.plugin.getCfg().onPlayerMove()) {
         int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
         int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();
         if(Math.abs(movX) > 0 || Math.abs(movZ) > 0) {
            this.plugin.afk(event.getPlayer().getName(), true, "");
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   private void onPlayerMessage(AsyncPlayerChatEvent event) {
      if(AFK.isPlayerAfk(event.getPlayer().getName()) && this.plugin.getCfg().onPlayerMessage()) {
         this.plugin.afk(event.getPlayer().getName(), true, "");
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   private void onPlayerQuit(PlayerQuitEvent event) {
      String playerName = event.getPlayer().getName();
      AFK.removePlayerFromAfkMap(playerName);
      AFK.removePlayerFromTimeMap(playerName);
      this.plugin.getLocationMap().remove(playerName);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   private void onPlayerKicked(PlayerKickEvent event) {
      String playerName = event.getPlayer().getName();
      AFK.removePlayerFromAfkMap(playerName);
      AFK.removePlayerFromTimeMap(playerName);
      this.plugin.getLocationMap().remove(playerName);
   }
}
