package com.example.cef.enchants;

import com.example.cef.core.CustomEnchant;
import com.example.cef.CustomEnchantPlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LifestealEnchant implements CustomEnchant {
    private final CustomEnchantPlugin plugin;
    private final Map<UUID, Long> lastUse = new ConcurrentHashMap<>();
    public LifestealEnchant(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @Override public String id() { return "lifesteal"; }
    @Override public String displayName() { return "Lifesteal"; }
    @Override public String loreLine() { return ChatColor.DARK_RED + "Lifesteal" + ChatColor.GRAY + " (custom enchant)"; }
    @Override public List<Material> appliesTo() { return Arrays.asList(
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE
    ); }
    @Override public String pdcKey() { return "cef_lifesteal"; }

    @Override public ItemStack createBook(int amount) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, Math.min(64, amount)));
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_RED + "Lifesteal Book");
            meta.setLore(Arrays.asList(ChatColor.GRAY+"Heal on hit (cooldown)"));
            meta.getPersistentDataContainer().set(CustomEnchantPlugin.BOOK_ID, PersistentDataType.STRING, id());
            book.setItemMeta(meta);
        }
        return book;
    }

    @Override public void onHitEntity(EntityDamageByEntityEvent e, ItemStack weapon) {
        if (!(e.getDamager() instanceof Player p)) return;
        long now = System.currentTimeMillis();
        long cd = plugin.getConfig().getLong("lifesteal.cooldown-ticks", 20L) * 50L;
        Long last = lastUse.getOrDefault(p.getUniqueId(), 0L);
        if (now - last < cd) return;
        lastUse.put(p.getUniqueId(), now);

        double hearts = plugin.getConfig().getDouble("lifesteal.heal-per-hit", 1.0);
    Attribute maxHealthAttr = Attribute.valueOf("GENERIC_MAX_HEALTH");
    double max = p.getAttribute(maxHealthAttr) != null ? p.getAttribute(maxHealthAttr).getValue() : 20.0;
    p.setHealth(Math.min(max, p.getHealth() + hearts * 2.0));

        p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0,1.2,0), 6, 0.4,0.6,0.4, 0.01);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6f, 1.8f);
    }
}
