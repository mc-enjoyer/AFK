package com.github.alesvojta.afk;

import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.entity.Player;

class IdleTimer implements Runnable {

   private final AFK plugin;


    IdleTimer(AFK plugin) {
      this.plugin = plugin;
    }

   public void run() {
       Player player;
       for (Iterator<Player> i$ = Arrays.stream(this.plugin.getServer().getOnlinePlayers()).iterator(); i$.hasNext(); this.plugin.getLocationMap().put(player.getName(), player.getLocation())) {
           player = i$.next();
           if (this.plugin.getLocationMap().containsKey(player.getName()) && this.plugin.getLocationMap().get(player.getName()).equals(player.getLocation()) && !AFK.isPlayerAfk(player.getName())) {
               long idleTime = this.plugin.getCfg().idleTime() * 20L;
               player.setPlayerTime(-idleTime, true);
               this.plugin.afk(player.getName(), false, "");
           }
       }
   }
}
