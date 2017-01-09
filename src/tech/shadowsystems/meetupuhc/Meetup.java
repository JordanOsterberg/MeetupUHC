package tech.shadowsystems.meetupuhc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import tech.shadowsystems.meetupuhc.commands.StatsCommand;
import tech.shadowsystems.meetupuhc.commands.VoteStartCommand;
import tech.shadowsystems.meetupuhc.listeners.PlayerListener;
import tech.shadowsystems.meetupuhc.commands.StartGameCommand;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class Meetup extends JavaPlugin {

    /*
    World generation code here is flawed
     */

    private static Meetup instance;
    public static Meetup getInstance() {
        return instance;
    }
    public void onEnable(){
        instance = this;

        getServer().createWorld(new WorldCreator("lobby"));
        getServer().createWorld(new WorldCreator("arena"));

        getServer().getWorld("lobby");
        getServer().getWorld("arena");

        for (World worlds : Bukkit.getWorlds()){
            worlds.setGameRuleValue("naturalRegeneration", "false");
        }

        System.out.print("[MUHC] All worlds have been established.");

        getCommand("startgame").setExecutor(new StartGameCommand());
        getCommand("votestart").setExecutor(new VoteStartCommand());
        getCommand("stats").setExecutor(new StatsCommand());

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        System.out.print("[MUHC] All commands and listeners have been established.");
    }

}
