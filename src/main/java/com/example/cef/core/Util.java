package com.example.cef.core;

import com.example.cef.CustomEnchantPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class Util {
    private static final Random RNG = new Random();

    public static NamespacedKey key(String path) {
        return new NamespacedKey(JavaPlugin.getProvidingPlugin(CustomEnchantPlugin.class), path);
    }

    public static boolean isPickaxe(Material m) {
        return switch (m) {
            case WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> true;
            default -> false;
        };
    }

    public static boolean isSwordOrAxe(Material m) {
        return m.name().endsWith("_SWORD") || m.name().endsWith("_AXE");
    }

    public static boolean isMineableByPickaxe(Material m) {
        return m.name().contains("STONE") || m.name().contains("DEEPSLATE") || m.name().contains("ORE") ||
               m.name().contains("NETHERRACK") || m.name().contains("END_STONE") || m.name().contains("GRANITE") ||
               m.name().contains("ANDESITE") || m.name().contains("DIORITE") || m == Material.BLACKSTONE ||
               m == Material.CALCITE || m == Material.TUFF || m == Material.BASALT || m == Material.COBBLESTONE ||
               m == Material.MAGMA_BLOCK || m == Material.OBSIDIAN || m == Material.COBBLED_DEEPSLATE;
    }

    public static boolean canBreak(Block b, JavaPlugin plugin) {
        if (b == null) return false;
        Material m = b.getType();
        if (m.isAir()) return false;
        List<String> protectedList = plugin.getConfig().getStringList("protected-blocks");
        return !protectedList.contains(m.name());
    }

    public static int[][] facePlane(BlockFace face) {
        List<int[]> list = new ArrayList<>();
        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            for (int x=-1; x<=1; x++) for (int z=-1; z<=1; z++) list.add(new int[]{x,0,z});
        } else if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
            for (int x=-1; x<=1; x++) for (int y=-1; y<=1; y++) list.add(new int[]{x,y,0});
        } else {
            for (int y=-1; y<=1; y++) for (int z=-1; z<=1; z++) list.add(new int[]{0,y,z});
        }
        return list.toArray(new int[0][]);
    }

    public static void addLore(ItemStack item, String line) {
        var meta = item.getItemMeta(); if (meta == null) return;
        List<String> lore = meta.getLore(); if (lore == null) lore = new ArrayList<>();
        if (!lore.contains(line)) lore.add(0, line);
        meta.setLore(lore); item.setItemMeta(meta);
    }

    public static void tagItem(ItemStack item, String pdcKey) {
        var meta = item.getItemMeta(); if (meta == null) return;
        meta.getPersistentDataContainer().set(key(pdcKey), PersistentDataType.BYTE, (byte)1);
        item.setItemMeta(meta);
    }

    public static void untagItem(ItemStack item, String pdcKey) {
        var meta = item.getItemMeta(); if (meta == null) return;
        meta.getPersistentDataContainer().remove(key(pdcKey));
        item.setItemMeta(meta);
    }

    public static void damageTool(ItemStack tool, int points, int unbreakingLevel, Player holder) {
        if (!(tool.getItemMeta() instanceof Damageable dmg)) return;
        for (int i=0;i<points;i++) {
            if (unbreakingLevel > 0 && RNG.nextInt(unbreakingLevel + 1) != 0) continue; // ignored
            dmg.setDamage(dmg.getDamage() + 1);
        }
        tool.setItemMeta(dmg);
        if (tool.getType().getMaxDurability() > 0 && dmg.getDamage() >= tool.getType().getMaxDurability()) {
            holder.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            holder.getWorld().playSound(holder.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        }
    }
}
