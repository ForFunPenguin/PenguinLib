package me.forfunpenguin.penguinapi.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.forfunpenguin.penguinapi.Tasks.DelayedTask;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BlockUtils {
    private static PacketContainer createColorTeamPacket(ChatColor colorID) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        container.getStrings().write(0, "color_" + colorID.ordinal());
        container.getIntegers().write(0, Integer.valueOf(0));
        Optional<InternalStructure> optStruct = (Optional<InternalStructure>)container.getOptionalStructures().read(0);
        InternalStructure structure = optStruct.orElseThrow();
        Class<?> nmsClass = MinecraftReflection.getMinecraftClass("EnumChatFormat");
        structure.getEnumModifier(ChatColor.class, nmsClass).write(0, colorID);
        structure.getStrings().write(0, "always");
        structure.getStrings().write(1, "never");
        structure.getChatComponents().write(0, WrappedChatComponent.fromText("color_" + colorID.ordinal()));
        container.getOptionalStructures().write(0, Optional.of(structure));
        return container;
    }

    private static PacketContainer createColorTeamAddPacket(ChatColor colorID, UUID addID) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        container.getStrings().write(0, "color_" + colorID.ordinal());
        container.getIntegers().write(0, Integer.valueOf(3));
        container.getSpecificModifier(Collection.class).write(0, new ArrayList(List.of(addID.toString())));
        return container;
    }

    private static PacketContainer createColorTeamRemovePacket(ChatColor colorID, UUID removeID) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        container.getStrings().write(0, "color_" + colorID.ordinal());
        container.getIntegers().write(0, Integer.valueOf(4));
        container.getSpecificModifier(Collection.class).write(0, new ArrayList(List.of(removeID.toString())));
        return container;
    }
    //0x01 = 2 0x60 = 0x20 + 0x40 = 64 + 32 = 96
    private static PacketContainer createBlockSlimeMetaPacket(int entityID, int size) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, Integer.valueOf(entityID));
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        byte optionsMask = 96;
        dataWatcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), Byte.valueOf(optionsMask)); //0x60 = Is invisible + has glowing effect
        dataWatcher.setObject(16, WrappedDataWatcher.Registry.get(Integer.class), Integer.valueOf(size));
        metaPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return metaPacket;
    }

    private static PacketContainer createBlockSlimeSpawnPacket(int entityID, UUID entityUID, Location loc) {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        StructureModifier<Integer> intMod = spawnPacket.getIntegers();
        StructureModifier<EntityType> entityMod = spawnPacket.getEntityTypeModifier();
        intMod.write(0, Integer.valueOf(entityID));
        StructureModifier<UUID> uidMod = spawnPacket.getUUIDs();
        uidMod.write(0, entityUID);
        entityMod.write(0, EntityType.SLIME);
        StructureModifier<Double> doubleMod = spawnPacket.getDoubles();
        doubleMod.write(0, Double.valueOf(loc.getX()));
        doubleMod.write(1, Double.valueOf(loc.getY()));
        doubleMod.write(2, Double.valueOf(loc.getZ()));
        return spawnPacket;
    }

    private static PacketContainer createEntityDestroyPacket(Integer... ids) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, Arrays.asList(ids));
        return destroyPacket;
    }

    public static void sendInvisibleGlowSlime(Player player, Location location, int lifetime, ChatColor chatColor, int size) {
        WeakReference<Player> playerRef = new WeakReference<>(player);
        int entityID = ThreadLocalRandom.current().nextInt();
        UUID entityUID = UUID.randomUUID();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer spawnPacket = createBlockSlimeSpawnPacket(entityID, entityUID, location);
        PacketContainer metaPacket = createBlockSlimeMetaPacket(entityID, size);
        PacketContainer teamPacket = createColorTeamPacket(chatColor);
        PacketContainer teamAddPacket = createColorTeamAddPacket(chatColor, entityUID);
        PacketContainer teamRemovePacket = createColorTeamRemovePacket(chatColor, entityUID);
        PacketContainer destroyPacket = createEntityDestroyPacket(new Integer[] { Integer.valueOf(entityID) });
        try {
            protocolManager.sendServerPacket(player, teamPacket);
            protocolManager.sendServerPacket(player, spawnPacket);
            protocolManager.sendServerPacket(player, teamAddPacket);
            protocolManager.sendServerPacket(player, metaPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DelayedTask(() -> {
            try {
                Player referencedPlayer = playerRef.get();
                if (referencedPlayer == null)
                    return;
                protocolManager.sendServerPacket(referencedPlayer, teamRemovePacket);
                protocolManager.sendServerPacket(referencedPlayer, destroyPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, lifetime);
    }

    public static void sendGlowingBlock(Player player, Block block, int lifetime, ChatColor chatColor) {
        sendInvisibleGlowSlime(player, block.getLocation().add(0.5D, 0.0D, 0.5D), lifetime, chatColor, 2);
    }

    public Object createDigAnimationPacket(Block block, int stage) {
        int entityID = ThreadLocalRandom.current().nextInt();
        return new ClientboundBlockDestructionPacket(entityID, new BlockPos(block.getX(), block.getY(), block.getZ()), stage);
    }

}