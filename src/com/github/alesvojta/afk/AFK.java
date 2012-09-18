package com.github.alesvojta.afk;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Aleš Vojta (https://github.com/alesvojta)
 * @version bukkit-1.3.2-R0, JRE 7
 */
public class AFK extends JavaPlugin {

    /**
     * Configuration instance.
     */
    public Config cfg;
    /**
     * AFK Players map.
     */
    public HashMap<Player, String> afkPlayerMap;
    private HashMap<Player, Long> afkTimeMap;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        this.afkPlayerMap = new HashMap<Player, String>();
        this.afkTimeMap = new HashMap<Player, Long>();
        this.cfg = new Config(this);
    }

    @Override
    public void onDisable() {
    }

    /**
     * Sets the PLayer AFK.
     *
     * @param player Hráč, který jde AFK
     */
    public void becomeAFK(Player player) {
        afkPlayerMap.put(player, player.getPlayerListName());
        afkTimeMap.put(player, player.getPlayerTime());

        if (cfg.serverMessages()) {
            String afkMessage = cfg.toAfk();
            String fallbackMessage = player.getName() + " is now AFK";
            ChatColor color = ChatColor.valueOf(cfg.serverMessagesColor());

            if (afkMessage.matches(".*\\{DISPLAYNAME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{DISPLAYNAME}", player.getName());
                player.getServer().broadcastMessage(color + afkMessage);
            } else {
                player.getServer().broadcastMessage(color + fallbackMessage);
            }
        }

        /*
         * If is Players name longer than 14 chars cuts the name about 2 chars (color tag).
         *
         * @param tempName Temporary name of the Player after conversion
         */
        if (player.getName().length() > 14) {
            String tempName = player.getName().substring(0, 13);
            ChatColor color = ChatColor.valueOf(cfg.playerListColor());

            player.setPlayerListName(color + tempName);
        } else {
            ChatColor color = ChatColor.valueOf(cfg.playerListColor());

            player.setPlayerListName(color + player.getName());
        }
    }

    /**
     * Removes AFK status from the Player.
     *
     * @param player Player
     */
    public void cancelAFK(Player player) {
        if (cfg.serverMessages()) {
            String afkMessage = cfg.noAfk();
            String fallbackMessage = player.getName() + " is no longer AFK";
            ChatColor color = ChatColor.valueOf(cfg.serverMessagesColor());

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

        player.setPlayerListName(afkPlayerMap.get(player));
        afkPlayerMap.remove(player);
    }

    /**
     * Returns Players AFK time.
     *
     * @param player Player
     * @return String
     */
    private String returnAfkTime(Player player) {
        long oldTime = afkTimeMap.get(player);
        long newTime = player.getPlayerTime();

        long minutes = ((newTime - oldTime) / 20) / 60;
        long seconds = ((newTime - oldTime) / 20) % 60;

        String time = (minutes == 0) ? seconds + "s" : minutes + "m" + seconds + "s";

        afkTimeMap.remove(player);
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
            if (afkPlayerMap.containsKey(sender)) {
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
