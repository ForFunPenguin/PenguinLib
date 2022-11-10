package me.forfunpenguin.penguinapi.Utils;

import org.bukkit.Bukkit;
import org.bukkit.util.CachedServerIcon;

import java.io.File;

public class FileUtils {
    public static CachedServerIcon getServerIcon(String path) {
        try {
            CachedServerIcon serverIcon = Bukkit.loadServerIcon(new File(path));
            return serverIcon;
        } catch (Exception error) {
            error.printStackTrace();
            return null;
        }
    }
}
