package tech.shadowsystems.meetupuhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.shadowsystems.meetupuhc.enumerators.GameState;
import tech.shadowsystems.meetupuhc.managers.GameManager;
import tech.shadowsystems.meetupuhc.utilities.ChatUtil;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class VoteStartCommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            if (GameManager.getInstance().isState(GameState.LOBBY)) {
                GameManager.getInstance().addVote(player);
                Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &a" + player.getName() + " voted to start the game early! &7(" + GameManager.getInstance().getVotesNeeded() + "/4)"));
                GameManager.getInstance().canStartWithVotes();
            } else {
                player.sendMessage(ChatUtil.format("&6&lUHC &7>> &cYou cannot vote to start the game right now."));
            }
        }

        return false;
    }

}
