package com.example.cef.enchants;

import com.example.cef.core.CustomEnchant;
import com.example.cef.CustomEnchantPlugin;
import org.bukkit.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.Recipe;
import java.util.*;

public class AutoSmeltEnchant implements CustomEnchant {
    private final CustomEnchantPlugin plugin;
    public AutoSmeltEnchant(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @Override public String id() { return "auto_smelt"; }
    @Override public String displayName() { return "Auto Smelt"; }
    @Override public String loreLine() { return ChatColor.RED + "Auto Smelt" + ChatColor.GRAY + " (custom enchant)"; }
    @Override public List<Material> appliesTo() { return Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE); }
    @Override public String pdcKey() { return "cef_auto_smelt"; }

    @Override public ItemStack createBook(int amount) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, Math.min(64, amount)));
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Auto Smelt Book");
            meta.setLore(Arrays.asList(ChatColor.GRAY+"Drops smelted form of blocks"));
            meta.getPersistentDataContainer().set(CustomEnchantPlugin.BOOK_ID, PersistentDataType.STRING, id());
            book.setItemMeta(meta);
        }
        return book;
    }

    @Override public void onBlockBreak(BlockBreakEvent e, ItemStack tool) {
        // Replace drops with their furnace result if available
        List<ItemStack> drops = new ArrayList<>(e.getBlock().getDrops(tool));
        if (drops.isEmpty()) return;
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack d : drops) {
            ItemStack smelt = smelted(d);
            out.add(smelt == null ? d : smelt);
        }
        e.setDropItems(false);
        for (ItemStack is : out) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), is);
        }
    e.getBlock().getWorld().spawnParticle(Particle.SMOKE, e.getBlock().getLocation().add(0.5,0.5,0.5), 12, 0.3,0.3,0.3, 0.02);
        e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.7f, 1.2f);
    }

    private ItemStack smelted(ItemStack in) {
        // Try to find a furnace recipe result for the exact item
        for (Recipe r : Bukkit.getServer().getRecipesFor(in)) {
            if (r instanceof FurnaceRecipe fr) {
                ItemStack res = fr.getResult().clone();
                res.setAmount(in.getAmount());
                return res;
            }
        }
        return null;
    }
}
