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
 * @version bukkit-1.6.4-R0.1, JRE 7
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
                //this.cancelAfk(sender.getName());
                this.afk(sender.getName(), true);
            } else {
                //this.becomeAfk(sender.getName());
                this.afk(sender.getName(), false);
            }
            return true;
        }

        return false;
    }

    /**
     * Provides setting and resetting AFK status on Player and server messages.
     *
     * @param playerName Player's name
     * @param afk        AFK status
     */
    void afk(String playerName, boolean afk) {
        String afkMessage;
        String fallbackMessage;
        ChatColor color = this.config.serverMessagesColor();

        if (afk) {
            afkMessage = this.config.noAfk();
            fallbackMessage = playerName + " is no longer AFK.";

            if (afkMessage.matches(".*\\{TIME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{TIME}", this.returnAfkTime(playerName));
            }

            Bukkit.getPlayer(playerName).setPlayerListName(getAfkPlayerName(playerName));
            removePlayerFromAfkMap(playerName);
        } else {
            putPlayerToAfkMap(playerName);
            putPlayerToTimeMap(playerName);

            afkMessage = this.config.toAfk();
            fallbackMessage = playerName + " is now AFK";

            /*
             * If Players name is longer than 14 chars, it cuts 2 chars (color tag) from the name.
             *
             * @param tempName Temporary Players name after conversion
             */
            if (playerName.length() > 14) {
                String tempName = playerName.substring(0, 13);
                Bukkit.getPlayer(playerName).setPlayerListName(this.config.playerListColor() + tempName);
            } else {
                Bukkit.getPlayer(playerName).setPlayerListName(this.config.playerListColor() + playerName);
            }
        }

        if (this.config.serverMessages()) {
            if (afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
                String name;

                if (this.config.displayNicknames()) {
                    name = ChatColor.stripColor(Bukkit.getPlayer(playerName).getDisplayName());
                } else {
                    name = ChatColor.stripColor(Bukkit.getPlayer(playerName).getPlayerListName());
                }

                afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", name);
                Bukkit.broadcastMessage(color + ChatColor.translateAlternateColorCodes('&', afkMessage));
            } else {
                Bukkit.broadcastMessage(color + fallbackMessage);
            }
        }
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
