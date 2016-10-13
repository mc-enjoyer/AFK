package com.github.alesvojta.afk;

import com.github.alesvojta.afk.AFK;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

class Config {

   private final Configuration cfg;


   Config(AFK plugin) {
      this.cfg = plugin.getConfig();
      this.cfg.options().copyDefaults(true);
      plugin.saveConfig();
   }

   /**
    * Move event.
    *
    * @return Boolean
    */
   boolean onPlayerMove() {
      return this.cfg.getBoolean("Events.move");
   }

   /**
    * Chat event
    *
    * @return Boolean
    */
   boolean onPlayerMessage() {
      return this.cfg.getBoolean("Events.chat");
   }

   /**
    * Server messages color.
    *
    * @return ChatColor
    */
   ChatColor serverMessagesColor() {
      try {
         return ChatColor.valueOf(this.cfg.getString("Colors.message").toUpperCase());
      } catch (IllegalArgumentException var2) {
         Bukkit.getLogger().warning("[AFK] " + var2.toString());
         return ChatColor.GRAY;
      }
   }

   /**
    * Player List AFK name color.
    * 
    * @return ChatColor
    */
   ChatColor playerListColor() {
      try {
         return ChatColor.valueOf(this.cfg.getString("Colors.player").toUpperCase());
      } catch (IllegalArgumentException var2) {
         Bukkit.getLogger().warning("[AFK] " + var2.toString());
         return ChatColor.GRAY;
      }
   }

   /**
    * Idle Timer.
    *
    * @return Boolean
    */
   boolean idleTimer() {
      return this.cfg.getBoolean("IdleTimer.enabled");
   }

   /**
    * Number of seconds.
    *
    * @return int
    */
   int idleTime() {
      return this.cfg.getInt("IdleTimer.period");
   }

   /**
    * Server Messages
    *
    * @return Boolean
    */
   boolean serverMessages() {
      return this.cfg.getBoolean("Messages.enabled");
   }

   /**
    * Whether displays nickname or Player's name
    * 
    * @return Boolean
    */
   boolean displayNicknames() {
      return this.cfg.getBoolean("Messages.nicknames");
   }

   /**
    * AFK message. When the Player becomes AFK.
    *
    * @return String
    */
   String toAfk() {
      return this.cfg.getString("Messages.+afk");
   }

   /**
    * AFK message. When the Player cencels AFK
    *
    * @return String
    */
   String noAfk() {
      return this.cfg.getString("Messages.-afk");
   }
}
