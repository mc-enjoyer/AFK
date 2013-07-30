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
 * @version bukkit-1.6.2-R0.1, JRE 7
 */
public class AFK extends JavaPlugin {

    private Config config;
    private HashMap<String, Location> locationMap;
    private static HashMap<String, String> afkPlayerMap;
    private static HashMap<String, Long> afkTimeMap;

    @Override
    public void onEnable() {
        if (this.getCfg().idleTimer()) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new IdleTimer(this), 20L * this.getCfg().idleTime(), 20L * this.getCfg().idleTime());
        }

        Bukkit.getServer().getPluginManager().registerEvents(new Events(this), this);
        this.config = new Config(this);
        this.locationMap = new HashMap<String, Location>();
        afkPlayerMap = new HashMap<String, String>();
        afkTimeMap = new HashMap<String, Long>();
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
    protected void becomeAfk(String playerName) {
        putPlayerToAfkMap(playerName);
        putPlayerToTimeMap(playerName);

        if (this.config.serverMessages()) {
            String afkMessage = this.config.toAfk();
            String fallbackMessage = Bukkit.getPlayer(playerName) + " is now AFK";
            ChatColor color = ChatColor.valueOf(this.config.serverMessagesColor());

            if (afkMessage.matches(".*\\{PLAYER}.*")) {
                afkMessage = afkMessage.replaceAll("\\{PLAYER}", playerName);
                Bukkit.broadcastMessage(color + afkMessage);
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
            ChatColor color = ChatColor.valueOf(this.config.playerListColor());

            Bukkit.getPlayer(playerName).setPlayerListName(color + tempName);
        } else {
            ChatColor color = ChatColor.valueOf(this.config.playerListColor());

            Bukkit.getPlayer(playerName).setPlayerListName(color + playerName);
        }
    }

    /**
     * Removes AFK state from player and broadcasts message.
     *
     * @param playerName Players name
     */
    protected void cancelAfk(String playerName) {
        if (this.config.serverMessages()) {
            String afkMessage = this.config.noAfk();
            String fallbackMessage = playerName + " is no longer AFK.";
            ChatColor color = ChatColor.valueOf(this.config.serverMessagesColor());

            if (afkMessage.matches(".*\\{TIME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{TIME}", this.returnAfkTime(playerName));
            }
            if (afkMessage.matches(".*\\{PLAYER}.*")) {
                afkMessage = afkMessage.replaceAll("\\{PLAYER}", playerName);
                Bukkit.broadcastMessage(color + afkMessage);
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
    protected String returnAfkTime(String playerName) {
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
    protected Config getCfg() {
        return this.config;
    }

    /**
     * Returns locations hash map.
     *
     * @return LocationHashMap
     */
    protected HashMap<String, Location> getLocationMap() {
        return this.locationMap;
    }

    //    PUBLIC API
    public static void putPlayerToAfkMap(String PlayerName) {
        afkPlayerMap.put(PlayerName, Bukkit.getPlayer(PlayerName).getPlayerListName());
    }

    public static void removePlayerFromAfkMap(String playerName) {
        afkPlayerMap.remove(playerName);
    }

    public static String getAfkPlayerName(String playerName) {
        return afkPlayerMap.get(playerName);
    }

    public static void putPlayerToTimeMap(String playerName) {
        afkTimeMap.put(playerName, Bukkit.getPlayer(playerName).getPlayerTime());
        Bukkit.getPlayer(playerName).resetPlayerTime();
    }

    public static void removePlayerFromTimeMap(String playerName) {
        afkTimeMap.remove(playerName);
    }

    public static Long getPlayerAfkTime(String playerName) {
        return afkTimeMap.get(playerName);
    }

    public static boolean isPlayerAfk(String playerName) {
        return afkPlayerMap.containsKey(playerName);
    }
}
