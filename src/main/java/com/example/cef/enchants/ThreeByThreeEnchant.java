package com.example.cef.enchants;

import com.example.cef.core.CustomEnchant;
import com.example.cef.core.Util;
import com.example.cef.CustomEnchantPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ThreeByThreeEnchant implements CustomEnchant {
    private final CustomEnchantPlugin plugin;
    public ThreeByThreeEnchant(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @Override public String id() { return "three_by_three"; }
    @Override public String displayName() { return "3x3"; }
    @Override public String loreLine() { return ChatColor.AQUA + "3x3" + ChatColor.GRAY + " (custom enchant)"; }
    @Override public List<Material> appliesTo() { return Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE); }
    @Override public String pdcKey() { return "cef_three_by_three"; }

    @Override public ItemStack createBook(int amount) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, Math.min(64, amount)));
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "3x3 Enchant Book");
            meta.setLore(Arrays.asList(ChatColor.GRAY+"Combine with a pickaxe in an anvil", ChatColor.GRAY+"Mines a 3x3 area"));
            meta.getPersistentDataContainer().set(CustomEnchantPlugin.BOOK_ID, PersistentDataType.STRING, id());
            book.setItemMeta(meta);
        }
        return book;
    }

    @Override public void onBlockBreak(BlockBreakEvent e, ItemStack tool) {
        Block center = e.getBlock();
        BlockFace face = e.getPlayer().getLastTwoTargetBlocks(null, 5).size() >= 2 ? e.getPlayer().getLastTwoTargetBlocks(null, 5).get(1).getFace(center) : BlockFace.UP;
        int[][] offs = Util.facePlane(face);
        int extras = 0;
        for (int[] o : offs) {
            if (o[0]==0 && o[1]==0 && o[2]==0) continue;
            Block b = center.getRelative(o[0], o[1], o[2]);
            if (!Util.canBreak(b, plugin)) continue;
            if (!Util.isMineableByPickaxe(b.getType())) continue;
            if (b.getType().isAir()) continue;
            b.getWorld().spawnParticle(Particle.CRIT, b.getLocation().add(0.5,0.5,0.5), 4, 0.2,0.2,0.2, 0.01);
            b.breakNaturally(tool);
            extras++;
            if (extras >= plugin.getConfig().getInt("max-extras-per-break", 128)) break;
        }
        if (plugin.getConfig().getBoolean("consume-durability", true) && extras>0) {
            int unb = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
            Util.damageTool(tool, extras, unb, e.getPlayer());
        }
        center.getWorld().playSound(center.getLocation(), Sound.BLOCK_STONE_BREAK, 0.6f, 1.2f);
    }
}
