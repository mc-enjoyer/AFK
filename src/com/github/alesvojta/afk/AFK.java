package com.github.alesvojta.afk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 * @version bukkit-1.6.2-R0.2, JRE 7
 */
public class AFK extends JavaPlugin {

    private Config config;
    private HashMap<String, Location> locationMap;
    private static HashMap<String, String> afkPlayerMap;
    private static HashMap<String, Long> afkTimeMap;

    @Override
    public void onEnable() {
        this.config = new Config(this);
        this.locationMap = new HashMap<String, Location>();
        afkPlayerMap = new HashMap<String, String>();
        afkTimeMap = new HashMap<String, Long>();

        if (this.getCfg().idleTimer()) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new IdleTimer(this), 20L * this.getCfg().idleTime(), 20L * this.getCfg().idleTime());
        }
        Bukkit.getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        afkPlayerMap = null;
        afkTimeMap = null;
        this.locationMap = null;
    }

    /**
     * '/afk' command controller.
     *
     * @param sender Command sender
     * @param cmd    Command
     * @param label  Command alias
     * @param args   Other params
     * @return boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info("This command can only be run by a player!");
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("afk")) {
            if (isPlayerAfk(sender.getName())) {
                this.cancelAfk(sender.getName());
            } else {
                this.becomeAfk(sender.getName());
            }
            return true;
        }

        return false;
    }

    /**
     * Sets Player to AFK state and broadcasts message.
     *
     * @param playerName Players name
     */
    void becomeAfk(String playerName) {
        putPlayerToAfkMap(playerName);
        putPlayerToTimeMap(playerName);

        playerName = Bukkit.getPlayer(playerName).getPlayerListName();

        if (this.config.serverMessages()) {
            String afkMessage = this.config.toAfk();
            String fallbackMessage = playerName + " is now AFK";
            ChatColor color = this.config.serverMessagesColor();

            if (afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", playerName);
                Bukkit.broadcastMessage(color + ChatColor.translateAlternateColorCodes('&', afkMessage));
            } else {
                Bukkit.broadcastMessage(color + fallbackMessage);
            }
        }

        /*
         * If Players name is longer than 14 chars, it cuts 2 chars (color tag) from the name.
         *
         * @param tempName Temporary Players name after conversion
         */
        if (playerName.length() > 14) {
            String tempName = playerName.substring(0, 13);
            ChatColor color = this.config.playerListColor();

            Bukkit.getPlayer(playerName).setPlayerListName(color + tempName);
        } else {
            ChatColor color = this.config.playerListColor();

            Bukkit.getPlayer(playerName).setPlayerListName(color + playerName);
        }
    }

    /**
     * Removes AFK state from player and broadcasts message.
     *
     * @param playerName Players name
     */
    void cancelAfk(String playerName) {
        if (this.config.serverMessages()) {
            String afkMessage = this.config.noAfk();
            String fallbackMessage = playerName + " is no longer AFK.";
            ChatColor color = this.config.serverMessagesColor();

            if (afkMessage.matches(".*\\{TIME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{TIME}", this.returnAfkTime(playerName));
            }
            if (afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", playerName);
                Bukkit.broadcastMessage(color + ChatColor.translateAlternateColorCodes('&', afkMessage));
            } else {
                Bukkit.broadcastMessage(color + fallbackMessage);
            }
        }

        Bukkit.getPlayer(playerName).setPlayerListName(getAfkPlayerName(playerName));
        removePlayerFromAfkMap(playerName);
    }

    /**
     * Returns how long was Player AFK.
     *
     * @param playerName Players name
     * @return String
     */
    String returnAfkTime(String playerName) {
        long oldTime = getPlayerAfkTime(playerName);
        long currentTime = Bukkit.getPlayer(playerName).getPlayerTime();

        long minutes = ((currentTime - oldTime) / 20) / 60;
        long seconds = ((currentTime - oldTime) / 20) % 60;

        return (minutes == 0) ? seconds + "s" : minutes + "m" + seconds + "s";
    }

    /**
     * Returns configurations.
     *
     * @return Config
     */
    Config getCfg() {
        return this.config;
    }

    /**
     * Returns locations hash map.
     *
     * @return LocationHashMap
     */
    HashMap<String, Location> getLocationMap() {
        return this.locationMap;
    }

    //    PUBLIC API

    /**
     * Puts Player to AFK hash map.
     *
     * @param PlayerName PLayers name
     */
    public static void putPlayerToAfkMap(String PlayerName) {
        afkPlayerMap.put(PlayerName, Bukkit.getPlayer(PlayerName).getPlayerListName());
    }

    /**
     * Removes Player from AFK hash map.
     *
     * @param playerName Players name
     */
    public static void removePlayerFromAfkMap(String playerName) {
        afkPlayerMap.remove(playerName);
    }

    /**
     * Returns original Players name from AFK hash map.
     *
     * @param playerName Players name
     * @return String|null
     */
    public static String getAfkPlayerName(String playerName) {
        return afkPlayerMap.get(playerName);
    }

    /**
     * Puts Player to AFK time hash map.
     *
     * @param playerName Players name
     */
    public static void putPlayerToTimeMap(String playerName) {
        afkTimeMap.put(playerName, Bukkit.getPlayer(playerName).getPlayerTime());
        Bukkit.getPlayer(playerName).resetPlayerTime();
    }

    /**
     * Removes Player from AFK time hash map.
     *
     * @param playerName Players name
     */
    public static void removePlayerFromTimeMap(String playerName) {
        afkTimeMap.remove(playerName);
    }

    /**
     * Returns Players time from AFK time hash map.
     *
     * @param playerName Players name
     * @return Long|null
     */
    public static Long getPlayerAfkTime(String playerName) {
        return afkTimeMap.get(playerName);
    }

    /**
     * Returns whether is Player AFK or not.
     *
     * @param playerName Players name
     * @return boolean
     */
    public static boolean isPlayerAfk(String playerName) {
        return afkPlayerMap.containsKey(playerName);
    }
}
