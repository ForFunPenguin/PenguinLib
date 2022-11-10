package me.forfunpenguin.penguinapi.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {
    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
