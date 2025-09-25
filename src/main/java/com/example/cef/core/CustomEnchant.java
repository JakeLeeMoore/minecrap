package com.example.cef.core;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CustomEnchant {
    String id();                 // unique id, e.g., "three_by_three"
    String displayName();        // e.g., "3x3"
    String loreLine();           // a short lore identifier
    List<Material> appliesTo();  // which item types

    // PersistentData key name to set on items when applied
    String pdcKey();

    // Create an enchanted book for this enchant
    ItemStack createBook(int amount);

    // Hooks (only implement what you need)
    default void onBlockBreak(BlockBreakEvent e, ItemStack tool) {}
    default void onHitEntity(EntityDamageByEntityEvent e, ItemStack weapon) {}
}
