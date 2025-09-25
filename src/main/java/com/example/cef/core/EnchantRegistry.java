package com.example.cef.core;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnchantRegistry {
    private static final Map<String, CustomEnchant> ENCHANTS = new LinkedHashMap<>();

    public static void register(CustomEnchant enchant) {
        ENCHANTS.put(enchant.id(), enchant);
    }

    public static void clear() { ENCHANTS.clear(); }

    public static CustomEnchant get(String id) { return ENCHANTS.get(id); }

    public static Collection<CustomEnchant> all() { return ENCHANTS.values(); }

    public static int size() { return ENCHANTS.size(); }

    public static boolean itemHasEnchant(ItemStack item, CustomEnchant e) {
        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null) return false;
        Byte b = item.getItemMeta().getPersistentDataContainer().get(Util.key(e.pdcKey()), org.bukkit.persistence.PersistentDataType.BYTE);
        return b != null && b == 1;
    }
}
