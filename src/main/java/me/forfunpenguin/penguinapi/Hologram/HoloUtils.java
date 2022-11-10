package me.forfunpenguin.penguinapi.Hologram;

import lombok.val;
import me.forfunpenguin.penguinapi.PenguinAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoloUtils {
    private static final Map<String, HoloMemory> holomemory = new HashMap<>();

    public static HoloMemory getHoloMemory(Player p) {
        if (!holomemory.containsKey(p.getUniqueId().toString())) {
            HoloMemory m = new HoloMemory();
            holomemory.put(p.getUniqueId().toString(), m);
            return m;
        }
        return holomemory.get(p.getUniqueId().toString());
    }

    public static void setHoloMemory(String holoName, HoloMemory memory) {
        if (memory == null) holomemory.remove(holoName);
        else holomemory.put(holoName, memory);
    }

    public static Boolean hasHoloName(String holoName) {
        return holomemory.containsKey(holoName);
    }

    public static void loadMemory() {
        holomemory.clear();
        File file = new File(HoloUtils.getHoloFolderPath(PenguinAPI.getPlugin().getName()) + "/Hologram.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(String name : config.getKeys(false)){
            HoloMemory memory = new HoloMemory();
            memory.setHoloName(name);
            memory.setLocation(config.getLocation(name + ".position"));
            memory.setLines((List<String>) config.getList(name + ".lines"));
            HoloUtils.setHoloMemory(name, memory);
            spawnHologram(memory.getHoloName(), memory.getLocation(), memory.getLines());
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[PenguinAPI] &aHologram已載入!"));
    }
    public static void saveMemory() {
        holomemory.forEach((holoName, memory) -> {
            File file = new File(HoloUtils.getHoloFolderPath(PenguinAPI.getPlugin().getName()) + "/Hologram.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(memory.getHoloName() + ".lines", memory.getLines());
            config.set(memory.getHoloName() + ".position", memory.getLocation());
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[PenguinAPI] &aHologram已儲存!"));
    }
    public static String getHoloFolderPath(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName).getDataFolder().getAbsolutePath() + "/Server/";
    }

    public static Map<String, HoloMemory> getHoloMemory() {
        return holomemory;
    }

    public static void spawnHologram(String holoName, Location location, List<String> lines) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        try {
            stand.setCustomName(ChatColor.translateAlternateColorCodes('&', lines.get(0)));
        } catch (Exception e) {
            e.printStackTrace();
            stand.remove();
        }
        PenguinAPI.getPlugin().armorstands.put(holoName, stand);
    }
}
