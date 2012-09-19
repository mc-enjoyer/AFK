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

    private HashMap<String, String> afkPlayerMap;
    private HashMap<String, Long> afkTimeMap;
    private Config cfg;

    public HashMap<String, String> getAfkMap() {
        return this.afkPlayerMap;
    }

    public HashMap<String, Long> getTimeMap() {
        return this.afkTimeMap;
    }

    public Config getCfg() {
        return this.cfg;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        this.afkPlayerMap = new HashMap<String, String>();
        this.afkTimeMap = new HashMap<String, Long>();
        this.cfg = new Config(this);
    }

    @Override
    public void onDisable() {
        this.afkPlayerMap.clear();
        this.afkTimeMap.clear();
        this.cfg = null;
    }

    /**
     * Sets PLayer AFK.
     *
     * @param player Player
     */
    public void becomeAFK(Player player) {
        getAfkMap().put(player.getName(), player.getPlayerListName());
        getTimeMap().put(player.getName(), player.getPlayerTime());

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
         * @param tempName Temporary name of Player after conversion
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
    public void cancelAFK(Player player) {
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

        player.setPlayerListName(getAfkMap().get(player.getName()));
        getAfkMap().remove(player.getName());
    }

    /**
     * Returns Players AFK time.
     *
     * @param player Player
     * @return String
     */
    private String returnAfkTime(Player player) {
        long oldTime = getTimeMap().get(player.getName());
        long newTime = player.getPlayerTime();

        long minutes = ((newTime - oldTime) / 20) / 60;
        long seconds = ((newTime - oldTime) / 20) % 60;

        String time = (minutes == 0) ? seconds + "s" : minutes + "m" + seconds + "s";

        getTimeMap().remove(player.getName());
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
            this.getLogger().info("This command can only be run by a player!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("afk")) {
            if (getAfkMap().containsKey(sender.getName())) {
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
