package me.forfunpenguin.penguinapi.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.forfunpenguin.penguinapi.PenguinAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerUtils {
    private static final Map<Player, String> fakeNames = new WeakHashMap<Player, String>();

    public PlayerUtils(PenguinAPI plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER) return;
                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<PlayerInfoData>();
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
                for (PlayerInfoData playerInfoData : playerInfoDataList) {
                    if (playerInfoData == null || playerInfoData.getProfile() == null || Bukkit.getPlayer(playerInfoData.getProfile().getUUID()) == null) { //Unknown Player
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }
                    WrappedGameProfile profile = playerInfoData.getProfile();
                    profile = profile.withName(getNameToSend(profile.getUUID()));
                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getPing(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }
        });
    }

    private static String getNameToSend(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if (!fakeNames.containsKey(player)) return player.getName();
        return fakeNames.get(player);
    }

    public static void changeName(final Player player, String fakeName) {
        fakeNames.put(player, ChatColor.translateAlternateColorCodes('&', fakeName));
        refresh(player);
    }

    public static void resetName(Player player, String fakeName) {
        if (!fakeNames.containsKey(player))
            fakeNames.remove(player);
        refresh(player);
    }

    private static void refresh(final Player player) {
        for (final Player forWhom : player.getWorld().getPlayers()) {
            if (player.equals(forWhom) || player.getWorld().equals(forWhom.getWorld()) || !forWhom.canSee(player)) {
                forWhom.hidePlayer(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        forWhom.showPlayer(player);
                    }
                }.runTaskLater(PenguinAPI.getPlugin(), 2);
            }
        }
    }
}
