package com.example.cef.enchants;

import com.example.cef.core.CustomEnchant;
import com.example.cef.core.Util;
import com.example.cef.CustomEnchantPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class VeinMinerEnchant implements CustomEnchant {
    private final CustomEnchantPlugin plugin;
    public VeinMinerEnchant(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @Override public String id() { return "vein_miner"; }
    @Override public String displayName() { return "Vein Miner"; }
    @Override public String loreLine() { return ChatColor.GOLD + "Vein Miner" + ChatColor.GRAY + " (custom enchant)"; }
    @Override public List<Material> appliesTo() { return Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE); }
    @Override public String pdcKey() { return "cef_vein_miner"; }

    @Override public ItemStack createBook(int amount) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, Math.min(64, amount)));
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Vein Miner Book");
            meta.setLore(Arrays.asList(ChatColor.GRAY+"Mine one ore -> break connected vein", ChatColor.GRAY+"Particles + sounds"));
            meta.getPersistentDataContainer().set(CustomEnchantPlugin.BOOK_ID, PersistentDataType.STRING, id());
            book.setItemMeta(meta);
        }
        return book;
    }

    @Override public void onBlockBreak(BlockBreakEvent e, ItemStack tool) {
        Material startType = e.getBlock().getType();
        if (!startType.name().endsWith("_ORE") && !startType.name().contains("ANCIENT_DEBRIS")) return;
        int max = plugin.getConfig().getInt("max-vein-blocks", 64);
        Set<Block> visited = new HashSet<>();
        Deque<Block> q = new ArrayDeque<>();
        q.add(e.getBlock()); visited.add(e.getBlock());
        int broken = 0;
        while (!q.isEmpty() && broken < max) {
            Block b = q.poll();
            for (Block n : neighbors(b)) {
                if (visited.contains(n)) continue;
                if (n.getType() == startType) { visited.add(n); q.add(n); }
            }
            if (b.equals(e.getBlock())) continue; // original already broken by event
            if (!Util.canBreak(b, plugin)) continue;
            b.getWorld().spawnParticle(Particle.CRIT, b.getLocation().add(0.5,0.5,0.5), 6, 0.2,0.2,0.2, 0.02);
            b.breakNaturally(tool);
            broken++;
        }
        if (plugin.getConfig().getBoolean("consume-durability", true) && broken>0) {
            int unb = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
            Util.damageTool(tool, broken, unb, e.getPlayer());
        }
        if (broken>0)
            e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.8f, 1.6f);
    }

    private List<Block> neighbors(Block b) {
        List<Block> list = new ArrayList<>();
        for (int dx=-1; dx<=1; dx++) for (int dy=-1; dy<=1; dy++) for (int dz=-1; dz<=1; dz++) {
            if (dx==0 && dy==0 && dz==0) continue;
            list.add(b.getRelative(dx,dy,dz));
        }
        return list;
    }
}
