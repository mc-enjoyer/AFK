package com.github.alesvojta.afk;

import com.github.alesvojta.afk.AFK;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class IdleTimer implements Runnable {

   private final AFK plugin;


    IdleTimer(AFK plugin) {
      this.plugin = plugin;
    }

   public void run() {
      Player player;
      for(Iterator i$ = this.plugin.getServer().getOnlinePlayers().iterator(); i$.hasNext(); this.plugin.getLocationMap().put(player.getName(), player.getLocation())) {
         player = (Player)i$.next();
         if(this.plugin.getLocationMap().containsKey(player.getName()) && ((Location)this.plugin.getLocationMap().get(player.getName())).equals(player.getLocation()) && !AFK.isPlayerAfk(player.getName())) {
            long idleTime = (long)(this.plugin.getCfg().idleTime() * 20);
            player.setPlayerTime(-idleTime, true);
            this.plugin.afk(player.getName(), false, "");
         }
      }

   }
}
