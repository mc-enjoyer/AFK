package com.github.alesvojta.afk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 * @version bukkit-1.4.2-R0.3, JRE 7
 */
public class AFK extends JavaPlugin {

    private static HashMap<String, String> afkPlayerMap;
    private static HashMap<String, Long> afkTimeMap;
    private Config cfg;

    /**
     * Returns true if afkPlayerMap contains Player.
     *
     * @param playerName Player
     * @return Boolean
     */
    public static boolean isPlayerAfk(String playerName) {
        return afkPlayerMap.containsKey(playerName);
    }

    /**
     * Returns timestamp when Player gets AFK.
     *
     * @param playerName Player
     * @return Long
     */
    public static long getPlayerAfkTime(String playerName) {
        return afkTimeMap.get(playerName);
    }

    /**
     * Returns Players name.
     *
     * @param playerName Player
     * @return String
     */
    protected static String getAfkPlayerName(String playerName) {
        return afkPlayerMap.get(playerName);
    }

    /**
     * Returns config.
     *
     * @return Config
     */
    protected Config getCfg() {
        return this.cfg;
    }

    /**
     * Puts Player to afkPlayerMap.
     *
     * @param playerName Player
     */
    protected static void putPlayerToAfkMap(String playerName) {
        afkPlayerMap.put(playerName, Bukkit.getPlayer(playerName).getPlayerListName());
    }

    /**
     * Removes Player from afkPlayerMap.
     *
     * @param playerName Player
     */
    protected static void removePlayerFromAfkMap(String playerName) {
        afkPlayerMap.remove(playerName);
    }

    /**
     * Puts PLayer to afkTimeMap.
     *
     * @param playerName Player
     */
    protected static void putPlayerToTimeMap(String playerName) {
        afkTimeMap.put(playerName, Bukkit.getPlayer(playerName).getPlayerTime());
        Bukkit.getPlayer(playerName).resetPlayerTime();
    }

    /**
     * Removes player from afkTimeMap.
     *
     * @param playerName PLayer
     */
    protected static void removePlayerFromTimeMap(String playerName) {
        afkTimeMap.remove(playerName);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        afkPlayerMap = new HashMap<String, String>();
        afkTimeMap = new HashMap<String, Long>();
        this.cfg = new Config(this);
    }

    @Override
    public void onDisable() {
        afkPlayerMap = null;
        afkTimeMap = null;
    }

    /**
     * Sets PLayer AFK.
     *
     * @param player Player
     */
    protected void becomeAFK(Player player) {
        putPlayerToAfkMap(player.getName());
        putPlayerToTimeMap(player.getName());

        if (getCfg().serverMessages()) {
            String afkMessage = getCfg().toAfk();
            String fallbackMessage = player.getName() + " is now AFK";
            ChatColor color = ChatColor.valueOf(getCfg().serverMessagesColor());

            if (afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", player.getName());
                player.getServer().broadcastMessage(color + afkMessage);
            } else {
                player.getServer().broadcastMessage(color + fallbackMessage);
            }
        }

        /*
         * If Players name is longer than 14 chars, it cuts 2 chars (color tag) from the name.
         *
         * @param tempName Temporary Players name after conversion
         */
        if (player.getName().length() > 14) {
            String tempName = player.getName().substring(0, 13);
            ChatColor color = ChatColor.valueOf(getCfg().playerListColor());

            player.setPlayerListName(color + tempName);
        } else {
            ChatColor color = ChatColor.valueOf(getCfg().playerListColor());

            player.setPlayerListName(color + player.getName());
        }
    }

    /**
     * Removes AFK status from Player.
     *
     * @param player Player
     */
    protected void cancelAFK(Player player) {
        if (getCfg().serverMessages()) {
            String afkMessage = getCfg().noAfk();
            String fallbackMessage = player.getName() + " is no longer AFK";
            ChatColor color = ChatColor.valueOf(getCfg().serverMessagesColor());

            if (afkMessage.matches(".*\\{AFKTIME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{AFKTIME}", returnAfkTime(player));
            }

            if (afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", player.getName());
                player.getServer().broadcastMessage(color + afkMessage);
            } else {
                player.getServer().broadcastMessage(color + fallbackMessage);
            }
        }

        player.setPlayerListName(getAfkPlayerName(player.getName()));
        removePlayerFromAfkMap(player.getName());
    }

    /**
     * Returns Players AFK time.
     *
     * @param player Player
     * @return String
     */
    private String returnAfkTime(Player player) {
        long oldTime = getPlayerAfkTime(player.getName());
        long newTime = player.getPlayerTime();

        long minutes = ((newTime - oldTime) / 20) / 60;
        long seconds = ((newTime - oldTime) / 20) % 60;

        String time = (minutes == 0) ? seconds + "s" : minutes + "m" + seconds + "s";

        removePlayerFromTimeMap(player.getName());
        return time;
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
            getLogger().info("This command can only be run by a player!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("afk")) {
            if (isPlayerAfk(sender.getName())) {
                cancelAFK((Player) sender);
                return true;
            } else {
                becomeAFK((Player) sender);
                return true;
            }
        }
        return false;
    }
}
