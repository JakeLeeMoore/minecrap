package com.example.cef.enchants;

import com.example.cef.core.CustomEnchant;
import com.example.cef.CustomEnchantPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class FlightBootsEnchant implements CustomEnchant {
    private final CustomEnchantPlugin plugin;
    public FlightBootsEnchant(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @Override public String id() { return "flight_boots"; }
    @Override public String displayName() { return "Skywalker"; }
    @Override public String loreLine() { return ChatColor.BLUE + "Skywalker" + ChatColor.GRAY + " (boots fly)"; }
    @Override public List<Material> appliesTo() { return Arrays.asList(
            Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
    ); }
    @Override public String pdcKey() { return "cef_flight_boots"; }

    @Override public ItemStack createBook(int amount) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, Math.min(64, amount)));
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.BLUE + "Skywalker Book");
            meta.setLore(Arrays.asList(ChatColor.GRAY+"Gives creative-like flight when worn"));
            meta.getPersistentDataContainer().set(CustomEnchantPlugin.BOOK_ID, PersistentDataType.STRING, id());
            book.setItemMeta(meta);
        }
        return book;
    }
}
