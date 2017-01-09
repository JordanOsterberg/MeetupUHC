package tech.shadowsystems.meetupuhc.utilities;

import net.md_5.bungee.api.ChatColor;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */
public class ChatUtil {

    public static String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
