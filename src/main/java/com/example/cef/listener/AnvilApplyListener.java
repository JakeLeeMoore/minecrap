package com.example.cef.listener;

import com.example.cef.CustomEnchantPlugin;
import com.example.cef.core.CustomEnchant;
import com.example.cef.core.EnchantRegistry;
import com.example.cef.core.Util;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AnvilApplyListener implements Listener {
    private final CustomEnchantPlugin plugin;
    public AnvilApplyListener(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();
    ItemStack left = inv.getItem(0);
    ItemStack right = inv.getItem(1);
        if (left == null || right == null) return;
        ItemMeta meta = right.getItemMeta(); if (meta == null) return;
        String enchId = meta.getPersistentDataContainer().get(CustomEnchantPlugin.BOOK_ID, PersistentDataType.STRING);
        if (enchId == null) return;
        CustomEnchant ench = EnchantRegistry.get(enchId);
        if (ench == null) return;
        if (right.getType() != Material.ENCHANTED_BOOK) return;
        if (!ench.appliesTo().contains(left.getType())) return;

        ItemStack result = left.clone();
        Util.tagItem(result, ench.pdcKey());
        Util.addLore(result, ench.loreLine());
        inv.setRepairCost(3);
        e.setResult(result);
    }
}
