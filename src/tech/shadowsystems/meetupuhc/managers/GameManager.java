package tech.shadowsystems.meetupuhc.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tech.shadowsystems.meetupuhc.Meetup;
import tech.shadowsystems.meetupuhc.tasks.game.GameTask;
import tech.shadowsystems.meetupuhc.enumerators.GameState;
import tech.shadowsystems.meetupuhc.gui.ItemBuilder;
import tech.shadowsystems.meetupuhc.utilities.ChatUtil;
import tech.shadowsystems.meetupuhc.utilities.MapResetUtil;
import tech.shadowsystems.meetupuhc.utilities.PacketUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class GameManager {



    private GameTask gameTask;

    private static GameManager instance;
    public static GameManager getInstance() {
        if (instance==null){
            instance = new GameManager();
        }
        return instance;
    }

    private List<UUID> spectators = new ArrayList<>();
    private List<UUID> players = new ArrayList<>();
    private Map<UUID, Integer> kills = new HashMap<>();
    private List<UUID> votedToStart = new ArrayList<>();
    private int votesNeededToStart = 4;
    private boolean starting = false;
    private int cyclesLeft = 5;

    public int getVotesNeeded(){
        return votesNeededToStart;
    }

    public void addVote(Player player){
        if (hasVotedToStart(player)){
            return;
        }

        votesNeededToStart--;
        votedToStart.add(player.getUniqueId());
    }

    public void canStartWithVotes(){
        if (votesNeededToStart <= 0 && Bukkit.getOnlinePlayers().size() >= 2 &&!starting){
            Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &aThe vote to start early passed!"));
            startGame();
        }
    }

    public boolean hasVotedToStart(Player player){
        return votedToStart.contains(player.getUniqueId());
    }

    public void addKill(Player killah){
        kills.putIfAbsent(killah.getUniqueId(), 0);
        kills.put(killah.getUniqueId(), getKills(killah)+1);
    }

    public int getKills(Player player){
        kills.putIfAbsent(player.getUniqueId(), 0);
        return kills.get(player.getUniqueId());
    }

    private GameState state = GameState.LOBBY;
    public GameState getState() {
        return state;
    }
    public boolean isState(GameState state){
        return getState()==state;
    }

    /**
     * If the object 'player' doesn't have an assigned value, the action will be performed for everyone online the minecraft server.
     * @param player The object you wish to perform the action for. Set to null for everyone.
     */
    public void generateScoreboard(Player player){
        if (player==null){ // If the object 'player' doesn't have an assigned value, perform action for all online players.
            Bukkit.getOnlinePlayers().forEach(this::generateScoreboardPrivate);
        } else {
            generateScoreboardPrivate(player);
        }
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public boolean isSpectator(Player player){
        return getSpectators().contains(player.getUniqueId());
    }

    public boolean isPlayer(Player player){
        return getPlayers().contains(player.getUniqueId());
    }

    public void setTeam(Player player, String team){
        if (team==null){
            spectators.remove(player.getUniqueId());
            players.remove(player.getUniqueId());
            return;
        }
        if (team.equalsIgnoreCase("players")){
            getPlayers().add(player.getUniqueId());
            player.setGameMode(GameMode.SURVIVAL);
            getSpectators().remove(player.getUniqueId());
        } else {
            getSpectators().add(player.getUniqueId());
            getPlayers().remove(player.getUniqueId());
            player.setGameMode(GameMode.SPECTATOR);
            player.setHealth(20);
            player.teleport(new Location(Bukkit.getWorld("arena"), 0, 150, 0));
            player.sendMessage(ChatUtil.format("&6&lUHC &7>> &aYou are now a spectator."));
        }
    }

    public void checkForStart(){
        if (2 <= getPlayers().size()&&!starting){
            starting=true;
            Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby will end in 30 seconds..."));
            new BukkitRunnable(){
                int seconds = 30;
                public void run(){
                    if (seconds<=0){
                        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby has ended!"));
                        startGame();
                        cancel();
                        return;
                    } else if (seconds==5){
                        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby will end in 5 seconds.."));
                    } else if (seconds==4){
                        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby will end in 4 seconds.."));
                    } else if (seconds==3){
                        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby will end in 3 seconds.."));
                    } else if (seconds==2){
                        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby will end in 2 seconds.."));
                    } else if (seconds==1){
                        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cLobby will end in 1 second.."));
                    }
                    seconds--;
                }
            }.runTaskTimer(Meetup.getInstance(), 0, 20);
        }
    }

    private void generateScoreboardPrivate(Player who){
        /** SETUP **/
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("dummy", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        /** OBJECTS **/
        int playerHealth = (int) who.getHealth();
        int playerKills = getKills(who);
        int borderSize = (int) who.getWorld().getWorldBorder().getSize();
        int amountOfPlayers = players.size();
        int amountOfSpectators = spectators.size();

        /** GENERATING **/
        objective.setDisplayName(ChatUtil.format("&6&lMeetup UHC"));
        objective.getScore(ChatUtil.format("&aHealth: &f" + playerHealth)).setScore(8);
        objective.getScore(ChatUtil.format("&aKills: &f" + playerKills)).setScore(7);
        objective.getScore(ChatUtil.format("&aBorder: &f" + borderSize)).setScore(6);
        objective.getScore(ChatUtil.format(ChatColor.RED + " ")).setScore(5);
        objective.getScore(ChatUtil.format("&aPlayers: &f" + amountOfPlayers)).setScore(4);
        objective.getScore(ChatUtil.format("&aSpectators: &f" + amountOfSpectators)).setScore(3);
        objective.getScore(ChatUtil.format(ChatColor.AQUA + " ")).setScore(2);
        objective.getScore(ChatUtil.format("&5play.paradoxmcs.us")).setScore(1);

        /** HEALTH **/
        Objective health = scoreboard.registerNewObjective("health", "health");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setDisplayName(ChatUtil.format("&4❤"));
        for (Player all : Bukkit.getOnlinePlayers()){
            health.getScore(all.getName()).setScore((int) all.getHealth());
        }

        /** TEAMS **/
        Team alive = scoreboard.registerNewTeam("alive");
        Team dead = scoreboard.registerNewTeam("dead");

        alive.setPrefix(ChatUtil.format("&a"));
        dead.setPrefix(ChatUtil.format("&c"));

        for (Player all : Bukkit.getOnlinePlayers()){
            if (isSpectator(all)){
                dead.addPlayer(all);
            } else {
                alive.addPlayer(all);
            }
        }

        /** FINALIZING **/
        who.setScoreboard(scoreboard);
    }

    public void startGame(){
        /** TEAM **/
        spectators.clear();
        players.clear();
        players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));

        /** SCOREBOARD **/
        generateScoreboard(null);

        /** KITS **/
        for (Player all : Bukkit.getOnlinePlayers()) {
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
                all.getInventory().setHelmet(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build();
                all.getInventory().setChestplate(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
                all.getInventory().setLeggings(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build();
                all.getInventory().setBoots(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.FISHING_ROD).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 2).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.COBBLESTONE).amount(64).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.GOLDEN_APPLE).amount(2).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.COOKED_BEEF).amount(16).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.GOLD_INGOT).amount(3).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_PICKAXE).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.IRON_SPADE).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.WATER_BUCKET).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.LAVA_BUCKET).build();
                all.getInventory().addItem(itemStack);
            }
            {
                ItemStack itemStack = new ItemBuilder(Material.ARROW).amount(32).build();
                all.getInventory().addItem(itemStack);
            }
            all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aYou have been given your items!"));
        }

        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &aLoading an arena..."));
        Bukkit.createWorld(new WorldCreator("arena"));
        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &aArena loaded."));

        for (Player all : Bukkit.getOnlinePlayers()){
            all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aYou are now being teleported to an arena..."));
            Random random = new Random();
            int x = random.nextInt(120);
            int y = 100;
            int z = random.nextInt(120);
            all.teleport(new Location(Bukkit.getWorld("arena"), x, y, z));
            all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aTeleported to: X:" + x + " Y: " + y + " Z: " + z + "!"));
            all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aThe game will begin in &4&l5 seconds..."));
        }

        Bukkit.getWorld("arena").getWorldBorder().setCenter(0, 0);
        Bukkit.getWorld("arena").getWorldBorder().setSize(250);
        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &aThe worldborder was shrunk to 250x250."));
        generateScoreboard(null);

        new BukkitRunnable(){
            public void run(){
                for (Player all : Bukkit.getOnlinePlayers()){
                    all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aThe game will begin in &44 seconds..."));
                }
            }
        }.runTaskLater(Meetup.getInstance(), 20);
        new BukkitRunnable(){
            public void run(){
                for (Player all : Bukkit.getOnlinePlayers()){
                    all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aThe game will begin in &c3 seconds..."));
                }
            }
        }.runTaskLater(Meetup.getInstance(), 20*2);
        new BukkitRunnable(){
            public void run(){
                for (Player all : Bukkit.getOnlinePlayers()){
                    all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aThe game will begin in &e2 seconds..."));
                }
            }
        }.runTaskLater(Meetup.getInstance(), 20*3);
        new BukkitRunnable(){
            public void run(){
                for (Player all : Bukkit.getOnlinePlayers()){
                    all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aThe game will begin in &a1 second..."));
                }
            }
        }.runTaskLater(Meetup.getInstance(), 20*4);
        new BukkitRunnable(){
            public void run(){
                for (Player all : Bukkit.getOnlinePlayers()){
                    all.sendMessage(ChatUtil.format("&6&lUHC &7>> &aPvP is now enabled, the game has begun."));
                    all.sendMessage(ChatUtil.format("&6&lUHC &7>> &cYour coordinates will be revealed in 30 seconds."));
                    state = GameState.INGAME;
                    PacketUtil.sendTitle(all, ChatUtil.format("&6&lUHC"), ChatUtil.format("&cYour coordinates will be revealed in 30 seconds."), 10, 40, 20);
                }
            }
        }.runTaskLater(Meetup.getInstance(), 20*5);

        new BukkitRunnable() {
            public void run() {
                if (gameTask == null)
                {
                    gameTask = new GameTask();
                    gameTask.runTaskTimer(Meetup.getInstance(), 0, 20);
                } else {
                    Bukkit.getScheduler().cancelAllTasks();
                    gameTask = new GameTask();
                    gameTask.runTaskTimer(Meetup.getInstance(), 0, 20);
                }
            }
        }.runTaskLater(Meetup.getInstance(), 20*6);

    }

    public Player locateWinner(){
        if (players.size()<=1){
            Player toReturn = null;
            for (Player all : Bukkit.getOnlinePlayers()){
                if (all.getGameMode()== GameMode.SURVIVAL){
                    toReturn=all;
                }
            }
            return toReturn;
        } else {
            return null;
        }
    }

    public void endGame(){
        if (locateWinner()==null){
            // Game cannot end yet, there is no clear winner.
            return;
        }

        Player winner = locateWinner();
        Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &a" + winner.getName() + " has won &6&lMeetupUHC&a!"));
        state = GameState.RESETTING;

        new BukkitRunnable(){
            public void run(){
                resetGame();
            }
        }.runTaskLater(Meetup.getInstance(), 20*5);
    }

    public void resetGame(){
        kills.clear();
        players.clear();
        spectators.clear();
        starting=false;
        for (Player all : Bukkit.getOnlinePlayers()){
            players.add(all.getUniqueId());
            all.getInventory().clear();
            all.getInventory().setArmorContents(null);
            all.setGameMode(GameMode.SURVIVAL);
            all.teleport(new Location(Bukkit.getWorld("lobby"), 0, 150, 0));
            all.setHealth(20D);
        }
        votesNeededToStart=4;
        votedToStart.clear();
        Bukkit.unloadWorld(Bukkit.getWorld("arena"), false);
        MapResetUtil.getMapResetUtil().resetMap("arena");
        for (World worlds : Bukkit.getWorlds()){
            worlds.setGameRuleValue("naturalRegeneration", "false");
        }
        state = GameState.LOBBY;
        cyclesLeft--;
        if (cyclesLeft <= 0){
            Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe server will restart in 5 seconds..."));
            state = GameState.RESTARTING;
            new BukkitRunnable(){
                public void run(){
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe server is now restarting..."));
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        // TODO connect your players to your hub server
                    }
                    new BukkitRunnable(){
                        public void run() {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                        }
                    }.runTaskLater(Meetup.getInstance(), 20*3);
                }
            }.runTaskLater(Meetup.getInstance(), 20*5);
        } else {
            checkForStart();
        }
    }

}