package com.github.alesvojta.afk;

import com.github.alesvojta.afk.Config;
import com.github.alesvojta.afk.Events;
import com.github.alesvojta.afk.IdleTimer;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AFK extends JavaPlugin {

   private Config config;
   private HashMap<String, Location> locationMap;
   private static HashMap<String, String> afkPlayerMap;
   private static HashMap<String, Long> afkTimeMap;


   public AFK() {}

   public void onEnable() {
      this.config = new Config(this);
      this.locationMap = new HashMap();
      afkPlayerMap = new HashMap();
      afkTimeMap = new HashMap();
      if(this.getCfg().idleTimer()) {
         Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new IdleTimer(this), 20L * (long)this.getCfg().idleTime(), 20L * (long)this.getCfg().idleTime());
      }

      Bukkit.getServer().getPluginManager().registerEvents(new Events(this), this);
   }

   public void onDisable() {
      afkPlayerMap = null;
      afkTimeMap = null;
      this.locationMap = null;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if(!(sender instanceof Player)) {
         Bukkit.getLogger().info("This command can only be run by a player!");
         return false;
      } else if(cmd.getName().equalsIgnoreCase("afk")) {
         String senderName = sender.getName();
         ChatColor color = this.config.serverMessagesColor();
         if(isPlayerAfk(sender.getName())) {
            this.afk(sender.getName(), true, "");
         } else if(args.length == 0) {
            this.afk(senderName, false, "");
         } else {
            String Rargs = " , because: " + StringUtils.join(args, " ");
            this.afk(senderName, false, Rargs);
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * sets the player, afk or not afk
    */
   void afk(String playerName, boolean afk, String reason) {
      ChatColor color = this.config.serverMessagesColor();
      String afkMessage;
      String fallbackMessage;
      String name;
      if(afk) {
         afkMessage = this.config.noAfk();
         fallbackMessage = playerName + " is no longer AFK.";
         if(afkMessage.matches(".*\\{TIME}.*")) {
            afkMessage = afkMessage.replaceAll("\\{TIME}", this.returnAfkTime(playerName));
         }

         Bukkit.getPlayer(playerName).setPlayerListName(getAfkPlayerName(playerName));
         removePlayerFromAfkMap(playerName);
      } else {
         putPlayerToAfkMap(playerName);
         putPlayerToTimeMap(playerName);
         afkMessage = this.config.toAfk();
         fallbackMessage = playerName + " is now AFK" + reason;
         if(playerName.length() > 14) {
            name = playerName.substring(0, 13);
            Bukkit.getPlayer(playerName).setPlayerListName(this.config.playerListColor() + name);
         } else {
            Bukkit.getPlayer(playerName).setPlayerListName(this.config.playerListColor() + playerName);
         }
      }

      if(this.config.serverMessages()) {
         if(afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
            if(this.config.displayNicknames()) {
               name = ChatColor.stripColor(Bukkit.getPlayer(playerName).getDisplayName());
            } else {
               name = ChatColor.stripColor(Bukkit.getPlayer(playerName).getPlayerListName());
            }

            afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", name);
            Bukkit.broadcastMessage(color + ChatColor.translateAlternateColorCodes('&', afkMessage) + reason);
         } else {
            Bukkit.broadcastMessage(color + fallbackMessage);
         }
      }

   }

   String returnAfkTime(String playerName) {
      long oldTime = getPlayerAfkTime(playerName).longValue();
      long currentTime = Bukkit.getPlayer(playerName).getPlayerTime();
      long minutes = (currentTime - oldTime) / 20L / 60L;
      long seconds = (currentTime - oldTime) / 20L % 60L;
      return minutes == 0L?seconds + "s":minutes + "m" + seconds + "s";
   }

   Config getCfg() {
      return this.config;
   }

   HashMap<String, Location> getLocationMap() {
      return this.locationMap;
   }

   
   /**
    * sets the player as afk.
    */
   public static void putPlayerToAfkMap(String PlayerName) {
      afkPlayerMap.put(PlayerName, Bukkit.getPlayer(PlayerName).getPlayerListName());
   }

   
   /**
    * sets the player as NOT afk.
    */
   public static void removePlayerFromAfkMap(String playerName) {
      afkPlayerMap.remove(playerName);
   }

   public static String getAfkPlayerName(String playerName) {
      return (String)afkPlayerMap.get(playerName);
   }

   public static void putPlayerToTimeMap(String playerName) {
      afkTimeMap.put(playerName, Long.valueOf(Bukkit.getPlayer(playerName).getPlayerTime()));
      Bukkit.getPlayer(playerName).resetPlayerTime();
   }

   public static void removePlayerFromTimeMap(String playerName) {
      afkTimeMap.remove(playerName);
   }

   public static Long getPlayerAfkTime(String playerName) {
      return (Long)afkTimeMap.get(playerName);
   }

   /**
    * Checks if the player is AFK.
    */
   public static boolean isPlayerAfk(String playerName) {
      return afkPlayerMap.containsKey(playerName);
   }
}
