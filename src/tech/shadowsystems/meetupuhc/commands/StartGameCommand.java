package tech.shadowsystems.meetupuhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.shadowsystems.meetupuhc.managers.GameManager;
import tech.shadowsystems.meetupuhc.enumerators.GameState;
import tech.shadowsystems.meetupuhc.utilities.ChatUtil;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class StartGameCommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            if (!player.hasPermission("uhc.startgame")){
                player.sendMessage(ChatUtil.format("&6&lUHC &7>> &cYou do not have permission to execute this command."));
                return true;
            }
            if (GameManager.getInstance().isState(GameState.LOBBY)) {
                Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &d&l" + player.getName() + " started the game."));
                GameManager.getInstance().startGame();
            } else {
                player.sendMessage(ChatUtil.format("&6&lUHC &7>> &cYou cannot start the game right now."));
            }
        }

        return false;
    }
}
