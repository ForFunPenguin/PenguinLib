package me.forfunpenguin.penguinapi.Utils;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class NBTUtils {
    public static String getTag(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = nmsItem.getTag();
        return tag.getAsString();
    }

    public static Boolean hasTag(ItemStack item, String tag) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.getTag() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static int getTagIntValue(ItemStack item, String tag) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getInt(tag);
    }

    public static String getTagStringValue(ItemStack item, String tag) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getString(tag);
    }

    public static Set<String> getTagList(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getAllKeys();
    }

    public static ItemStack putIntTag(ItemStack item, String tag, int value) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        CompoundTag compoundTag = nmsItem.getTag();
        compoundTag.putInt(tag, value);
        nmsItem.setTag(compoundTag);
        ItemStack finalitem = CraftItemStack.asBukkitCopy(nmsItem);
        return finalitem;
    }

    public static ItemStack putStringTag(ItemStack item, String tag, String value) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        CompoundTag compoundTag = nmsItem.getTag();
        compoundTag.putString(tag, value);
        nmsItem.setTag(compoundTag);
        ItemStack finalitem = CraftItemStack.asBukkitCopy(nmsItem);
        return finalitem;
    }

    public static boolean isIntTag(ItemStack item, String tag, int value) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.getTag() != null) {
            if (nmsItem.getTag().getInt(tag) == value) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isStringTag(ItemStack item, String tag, String value) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.getTag() != null) {
            if (nmsItem.getTag().getString(tag).equalsIgnoreCase(value)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
