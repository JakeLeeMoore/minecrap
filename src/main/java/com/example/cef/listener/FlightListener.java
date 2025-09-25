package com.example.cef.listener;

import com.example.cef.core.EnchantRegistry;
import com.example.cef.core.Util;
import com.example.cef.enchants.FlightBootsEnchant;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class FlightListener implements Listener {
    private final org.bukkit.plugin.Plugin plugin;
    private final FlightBootsEnchant ench;

    public FlightListener(org.bukkit.plugin.Plugin plugin) {
        this.plugin = plugin;
        this.ench = (FlightBootsEnchant) EnchantRegistry.get("flight_boots");
    }

    private boolean hasFlightBoots(Player p) {
        ItemStack boots = p.getInventory().getBoots();
        if (boots == null) return false;
        return ench != null && EnchantRegistry.itemHasEnchant(boots, ench);
    }

    private boolean survivalLike(Player p) {
        return p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE;
    }

    private void update(Player p) {
        if (!survivalLike(p)) return; // don't touch creative/spectator
        boolean shouldFly = hasFlightBoots(p) && plugin.getConfig().getBoolean("enchants.flight_boots", true);
        if (shouldFly) {
            p.setAllowFlight(true);
            p.setFlySpeed((float) plugin.getConfig().getDouble("flight.fly-speed", 0.1));
        } else {
            if (p.isFlying()) p.setFlying(false);
            p.setAllowFlight(false);
        }
    }

    @EventHandler public void onMove(PlayerMoveEvent e) { update(e.getPlayer()); }
    @EventHandler public void onJoin(PlayerJoinEvent e) { update(e.getPlayer()); }
    @EventHandler public void onInv(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            plugin.getServer().getScheduler().runTask(plugin, () -> update(p));
        }
    }

    @EventHandler public void onToggle(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (!survivalLike(p)) return;
        if (!hasFlightBoots(p)) return;
        // Let them fly; add some flair on takeoff/land
        if (!p.isFlying() && e.isFlying()) {
            p.getWorld().playSound(p.getLocation(), Sound.ITEM_ELYTRA_FLYING, 0.6f, 1.6f);
            p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 12, 0.3, 0.1, 0.3, 0.02);
        }
    }

    @EventHandler public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!plugin.getConfig().getBoolean("flight.cancel-fall-damage", true)) return;
        if (hasFlightBoots(p)) e.setCancelled(true);
    }
}
