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
      for (Player player : this.plugin.getServer().getOnlinePlayers()) {
          if (player.isOp()) {return;}
          if(this.plugin.getLocationMap().containsKey(player.getName()) && this.plugin.getLocationMap().get(player.getName()).equals(player.getLocation()) && !AFK.isPlayerAfk(player.getName())) {
              long idleTime = this.plugin.getCfg().idleTime() * 20L;
              player.setPlayerTime(-idleTime, true);
              this.plugin.afk(player.getName(), false, "");
          }
      }
   }
}
