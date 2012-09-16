package com.github.alesvojta.afk;

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
 * @version 3.2.0
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
    private HashMap<Player, Long> afkTimeMap;

    /**
     * Funkce se volá při zavádění pluginu.
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        this.afkPlayerMap = new HashMap<Player, String>();
        this.afkTimeMap = new HashMap<Player, Long>();
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
     * Vrací čas, po který byl hráč AFK.
     *
     * @param player Hráč, který se vrátil ke hře
     * @return String
     */
    private String returnAfkTime(Player player) {
        long oldTime = afkTimeMap.get(player);     //  mensi
        long newTime = player.getPlayerTime();  //  vetsi

        long minutes = ((newTime - oldTime) / 20) / 60;
        long seconds = ((newTime - oldTime) / 20) % 60;

        String time = (minutes == 0) ? seconds + "s" : minutes + "m" + seconds + "s";
        return time;
    }

    /**
     * Ovladač příkazu '/afk'.
     *
     * @param sender Odesílatel příkazu
     * @param cmd    Příkaz
     * @param label  Alias příkazu
     * @param args   Další parametry příkazu
     * @return boolean
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
