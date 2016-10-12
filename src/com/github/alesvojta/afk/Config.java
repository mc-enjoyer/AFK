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

   boolean onPlayerMove() {
      return this.cfg.getBoolean("Events.move");
   }

   boolean onPlayerMessage() {
      return this.cfg.getBoolean("Events.chat");
   }

   ChatColor serverMessagesColor() {
      try {
         return ChatColor.valueOf(this.cfg.getString("Colors.message").toUpperCase());
      } catch (IllegalArgumentException var2) {
         Bukkit.getLogger().warning("[AFK] " + var2.toString());
         return ChatColor.GRAY;
      }
   }

   ChatColor playerListColor() {
      try {
         return ChatColor.valueOf(this.cfg.getString("Colors.player").toUpperCase());
      } catch (IllegalArgumentException var2) {
         Bukkit.getLogger().warning("[AFK] " + var2.toString());
         return ChatColor.GRAY;
      }
   }

   boolean idleTimer() {
      return this.cfg.getBoolean("IdleTimer.enabled");
   }

   int idleTime() {
      return this.cfg.getInt("IdleTimer.period");
   }

   boolean serverMessages() {
      return this.cfg.getBoolean("Messages.enabled");
   }

   boolean displayNicknames() {
      return this.cfg.getBoolean("Messages.nicknames");
   }

   String toAfk() {
      return this.cfg.getString("Messages.+afk");
   }

   String noAfk() {
      return this.cfg.getString("Messages.-afk");
   }
}
