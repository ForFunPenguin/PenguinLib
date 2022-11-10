package me.forfunpenguin.penguinapi;

import me.forfunpenguin.penguinapi.Tasks.DelayedTask;
import me.forfunpenguin.penguinapi.Utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class PenguinAPI extends JavaPlugin implements Listener {
    private static PenguinAPI plugin;
    public HashMap<String, ArmorStand> armorstands = new HashMap<>();
    //public NBTUtils NBTUtils;
    @Override
    public void onEnable() {
        // Plugin startup logic
        //NBTUtils = new NBTUtils();
        plugin = this;

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[PenguinAPI] &aAPI已啟用!"));

        new PlayerUtils(this);
        new DelayedTask(this);

    }

    public static String getAPIVersion() {
        return "1.0.4";
    }

    public static PenguinAPI getPlugin() {
        return plugin;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        armorstands.forEach((holoName, stand) -> {
            stand.remove();
        });
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[PenguinAPI] &a盔甲座已清除!"));
    }
}
