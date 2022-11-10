package me.forfunpenguin.penguinapi.Hologram;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

public class HoloMemory {
    @Getter
    @Setter
    private String holoName;

    @Getter
    @Setter
    private Location location;

    @Getter
    @Setter
    private List<String> lines;


}
