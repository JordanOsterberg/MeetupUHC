package tech.shadowsystems.meetupuhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.shadowsystems.meetupuhc.utilities.ChatUtil;
import tech.shadowsystems.meetupuhc.enumerators.GameState;
import tech.shadowsystems.meetupuhc.managers.GameManager;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        GameManager.getInstance().setTeam(player, null);
        if (!GameManager.getInstance().isState(GameState.LOBBY)){
            e.setQuitMessage(null);
        } else {
            e.setQuitMessage(ChatUtil.format("&6&lUHC &7>> &a" + player.getName() + " left the game. &7(" + Bukkit.getOnlinePlayers().size() + "/12)"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (!GameManager.getInstance().isState(GameState.LOBBY)){
            e.setJoinMessage(null);
            player.sendMessage(ChatUtil.format("&6&lUHC &7>> &cYou joined an in-progress match. Please wait for the next round."));
            GameManager.getInstance().setTeam(player, "spectators");
        } else {
            e.setJoinMessage(ChatUtil.format("&6&lUHC &7>> &a" + player.getName() + " joined the game. &7(" + Bukkit.getOnlinePlayers().size() + "/12)"));
            player.teleport(new Location(Bukkit.getWorld("lobby"), 0, 150, 0));
            GameManager.getInstance().setTeam(player, "players");
            GameManager.getInstance().generateScoreboard(null);
            GameManager.getInstance().checkForStart();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        e.setCancelled(!GameManager.getInstance().isState(GameState.INGAME));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        e.setCancelled(!GameManager.getInstance().isState(GameState.INGAME));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        e.setDeathMessage(null);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        if (GameManager.getInstance().isState(GameState.RESTARTING)){
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatUtil.format("&f&lYou cannot join this game while it is restarting!"));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            if (player.getKiller()!=null){
                Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &c" + player.getName() + " was killed by " + player.getKiller().getName() + " using " + player.getKiller().getItemInHand().getType().name().replaceAll("_", " ") + "!"));
                Player killer = player.getKiller();
                GameManager.getInstance().addKill(killer);
                killer.sendMessage(ChatUtil.format("&6&lUHC &7>> &aYou killed " + player.getName() + " using your " + killer.getItemInHand().getType().name().replaceAll("_", " ") + "!"));
            } else {
                Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &c" + player.getName() + " was killed by an unknown force..."));
            }
            GameManager.getInstance().setTeam(player, "spectator");
            GameManager.getInstance().generateScoreboard(null);
            GameManager.getInstance().endGame();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        e.setCancelled(!GameManager.getInstance().isState(GameState.INGAME));
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent e){
        if (e.getEntity() instanceof Monster){
            e.setCancelled(true);
        }
    }

}
