package com.github.alesvojta.afk;

import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * bukkit-1.3.2-R0, JRE 7
 *
 * @author Aleš Vojta (https://github.com/alesvojta)
 * @version 3.0.6
 */
public class AFK extends JavaPlugin {

    /**
     * Instance konfigurace. Vrací data z configu.
     */
    public Config cfg;
    /**
     * Mapa AFK hráčů.
     */
    public HashMap<Player, String> afkPlayerMap;
    private HashMap<Player, Calendar> afkTimeMap;

    /**
     * Funkce se volá při zavádění pluginu.
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        this.afkPlayerMap = new HashMap<Player, String>();
        this.afkTimeMap = new HashMap<Player, Calendar>();
        this.cfg = new Config(this);
    }

    /**
     * Funkce se volá při deaktivaci pluginu.
     */
    @Override
    public void onDisable() {
        this.getLogger().info("Thank you for choosing AFK plugin!");
    }

    /**
     * Nastaví hráči AFK status.
     *
     * @param player Hráč, který jde AFK
     */
    public void becomeAFK(Player player) {
        afkPlayerMap.put(player, player.getPlayerListName());
        afkTimeMap.put(player, Calendar.getInstance());

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
         * Pokud je jméno hráče delší než 14 znaků, je mu jméno v PlayerListu
         * (klávesa TAB) zkráceno o 2 znaky kvůli barevné značce, kterou
         * používám k označení AFK statusu.
         *
         * @param tempName Dočasné jméno hráče po zkrácení
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
     * Ruší hráči AFK status.
     *
     * @param player Hráč, který se vrátil ke hře
     */
    public void cancelAFK(Player player) {
        if (cfg.serverMessages()) {
            String afkMessage = cfg.fromAfk();
            String fallbackMessage = player.getName() + " is no longer AFK";
            ChatColor color = ChatColor.valueOf(cfg.serverMessagesColor());

            if (afkMessage.matches(".*\\{AFKTIME}.*")) {
                afkMessage = afkMessage.replaceAll("\\{AFKTIME}", returnPlayerAfkTime(player));
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
     * Vrací čas, po který byl hráč AFK.
     *
     * @param player Hráč, který se vrátil ke hře
     * @return {String} Doba AFK
     */
    private String returnPlayerAfkTime(Player player) {
        Calendar newTime = Calendar.getInstance();
        Calendar oldTime = afkTimeMap.get(player);
        String playerAfkTime;

        int[] newTimeArray = {
                newTime.get(Calendar.HOUR),
                newTime.get(Calendar.MINUTE),
                newTime.get(Calendar.SECOND)
        };

        int[] oldTimeArray = {
                oldTime.get(Calendar.HOUR),
                oldTime.get(Calendar.MINUTE),
                oldTime.get(Calendar.SECOND)
        };

        int[] afkTimeArray = {
                newTimeArray[0] - oldTimeArray[0],
                newTimeArray[1] - oldTimeArray[1],
                newTimeArray[2] - oldTimeArray[2]
        };

        //  0 = HOUR, 1 = MINUTE, 2 = SECOND
        if (afkTimeArray[0] < 1 && afkTimeArray[1] < 1) {
            playerAfkTime = afkTimeArray[2] + "s";
        } else if (afkTimeArray[0] < 1) {
            playerAfkTime = afkTimeArray[1] + "m" + afkTimeArray[2] + "s";
        } else {
            playerAfkTime = afkTimeArray[0] + "h" + afkTimeArray[1] + "m " + afkTimeArray[2] + "s";
        }

        afkTimeMap.remove(player);
        return playerAfkTime;
    }

    /**
     * Ovladač příkazu '/afk'.
     *
     * @param sender Odesílatel příkazu
     * @param cmd    Příkaz
     * @param label  Alias příkazu
     * @param args   Další parametry příkazu
     * @return {boolean}
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            this.getLogger().info("This command can only be run by a player!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("afk")) {
            if (afkPlayerMap.containsKey(player)) {
                cancelAFK(player);
                return true;
            } else {
                becomeAFK(player);
                return true;
            }
        }
        return false;
    }
}
