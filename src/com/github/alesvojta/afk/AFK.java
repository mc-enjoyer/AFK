package com.github.alesvojta.afk;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 * @version bukkit-1.3.2-R0, JRE 7
 */
public class AFK extends JavaPlugin {

    private static HashMap<String, String> afkPlayerMap;
    private static HashMap<String, Long> afkTimeMap;
    private Config cfg;

    /**
     * Returns true if afkPlayerMap contains Player.
     *
     * @param player Player
     * @return Boolean
     */
    public static boolean isPlayerAfk(Player player) {
        return afkPlayerMap.containsKey(player.getName());
    }

    /**
     * Returns timestamp when Player gets AFK.
     *
     * @param player Player
     * @return Long
     */
    public static long getPlayerAfkTime(Player player) {
        return afkTimeMap.get(player.getName());
    }

    /**
     * Returns Players name.
     *
     * @param player Player
     * @return String
     */
    protected static String getAfkPlayerName(Player player) {
        return afkPlayerMap.get(player.getName());
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
     * @param player Player
     */
    protected static void putPlayerToAfkMap(Player player) {
        afkPlayerMap.put(player.getName(), player.getPlayerListName());
    }

    /**
     * Removes Player from afkPlayerMap.
     *
     * @param player Player
     */
    protected static void removePlayerFromAfkMap(Player player) {
        afkPlayerMap.remove(player.getName());
    }

    /**
     * Puts PLayer to afkTimeMap.
     *
     * @param player Player
     */
    protected static void putPlayerToTimeMap(Player player) {
        afkTimeMap.put(player.getName(), player.getPlayerTime());
        player.resetPlayerTime();
    }

    /**
     * Removes player from afkTimeMap.
     *
     * @param player PLayer
     */
    protected static void removePlayerFromTimeMap(Player player) {
        afkTimeMap.remove(player.getName());
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
        putPlayerToAfkMap(player);
        putPlayerToTimeMap(player);

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
         * If is Players name longer than 14 chars, it cuts the name about 2 chars (color tag).
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

        player.setPlayerListName(getAfkPlayerName(player));
        removePlayerFromAfkMap(player);
    }

    /**
     * Returns Players AFK time.
     *
     * @param player Player
     * @return String
     */
    private String returnAfkTime(Player player) {
        long oldTime = getPlayerAfkTime(player);
        long newTime = player.getPlayerTime();

        long minutes = ((newTime - oldTime) / 20) / 60;
        long seconds = ((newTime - oldTime) / 20) % 60;

        String time = (minutes == 0) ? seconds + "s" : minutes + "m" + seconds + "s";

        removePlayerFromTimeMap(player);
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
            if (isPlayerAfk((Player) sender)) {
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
