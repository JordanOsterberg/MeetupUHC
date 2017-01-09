package tech.shadowsystems.meetupuhc.tasks.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tech.shadowsystems.meetupuhc.enumerators.GameState;
import tech.shadowsystems.meetupuhc.managers.GameManager;
import tech.shadowsystems.meetupuhc.utilities.ChatUtil;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class GameTask extends BukkitRunnable {

    private int seconds = 30;

    public void run() {
        if (GameManager.getInstance().isState(GameState.INGAME)) {
            if (seconds <= 0) {
                seconds = 30;
                Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &aCoordinates of players: "));
                for (int count = 0; count < GameManager.getInstance().getPlayers().size(); count++) {
                    Player all = Bukkit.getPlayer(GameManager.getInstance().getPlayers().get(count));
                    Location loc = all.getLocation();
                    int x = (int) loc.getX();
                    int y = (int) loc.getY();
                    int z = (int) loc.getZ();

                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &a" + all.getName() + " X:" + x + ", Y: " + y + ", Z: " + z));
                    all.playSound(all.getLocation(), Sound.LEVEL_UP, 1, 1);
                }
                if (!(Bukkit.getWorld("arena").getWorldBorder().getSize() <= 50)){
                    Bukkit.getWorld("arena").getWorldBorder().setSize(Bukkit.getWorld("arena").getWorldBorder().getSize()-25);
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe worldborder has shrunk by 25 blocks!"));
                }
            }
            if (!(Bukkit.getWorld("arena").getWorldBorder().getSize() <= 50)) {
                if (seconds == 5) {
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe border will shrink in 5 seconds!"));
                } else if (seconds == 4) {
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe border will shrink in 4 seconds!"));
                } else if (seconds == 3) {
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe border will shrink in 3 seconds!"));
                } else if (seconds == 2) {
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe border will shrink in 2 seconds!"));
                } else if (seconds == 1) {
                    Bukkit.broadcastMessage(ChatUtil.format("&6&lUHC &7>> &cThe border will shrink in 1 second!"));
                }
            }
            seconds--;
            GameManager.getInstance().generateScoreboard(null);
        } else {
            cancel();
        }
    }

}
