package com.example.cef.listener;

import com.example.cef.core.CustomEnchant;
import com.example.cef.core.EnchantRegistry;
import com.example.cef.core.Util;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventRouterListener implements Listener {
    private final org.bukkit.plugin.Plugin plugin;
    private final ThreadLocal<Boolean> guard = ThreadLocal.withInitial(() -> false);

    public EventRouterListener(org.bukkit.plugin.Plugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (guard.get()) return; guard.set(true);
        try {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            ItemStack tool = e.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
            for (CustomEnchant ench : EnchantRegistry.all()) {
                if (!plugin.getConfig().getBoolean("enchants."+ench.id(), true)) continue;
                if (!ench.appliesTo().contains(tool.getType())) continue;
                if (!EnchantRegistry.itemHasEnchant(tool, ench)) continue;
                ench.onBlockBreak(e, tool);
            }
        } finally { guard.set(false); }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof org.bukkit.entity.Player p)) return;
        if (guard.get()) return; guard.set(true);
        try {
            ItemStack weapon = p.getInventory().getItem(EquipmentSlot.HAND);
            for (CustomEnchant ench : EnchantRegistry.all()) {
                if (!plugin.getConfig().getBoolean("enchants."+ench.id(), true)) continue;
                if (!ench.appliesTo().contains(weapon.getType())) continue;
                if (!EnchantRegistry.itemHasEnchant(weapon, ench)) continue;
                ench.onHitEntity(e, weapon);
            }
        } finally { guard.set(false); }
    }
}
