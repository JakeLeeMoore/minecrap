package com.example.cef;

import com.example.cef.cmd.CECommand;
import com.example.cef.core.EnchantRegistry;
import com.example.cef.enchants.AutoSmeltEnchant;
import com.example.cef.enchants.LifestealEnchant;
import com.example.cef.enchants.FlightBootsEnchant;
import com.example.cef.enchants.ThreeByThreeEnchant;
import com.example.cef.enchants.VeinMinerEnchant;
import com.example.cef.listener.AnvilApplyListener;
import com.example.cef.listener.EventRouterListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEnchantPlugin extends JavaPlugin {
    public static NamespacedKey BOOK_ID; // holds enchant id string on book

    @Override
    public void onEnable() {
        saveDefaultConfig();
        BOOK_ID = new NamespacedKey(this, "cef_book_id");

        // Register enchants
EnchantRegistry.clear();
EnchantRegistry.register(new ThreeByThreeEnchant(this));
EnchantRegistry.register(new VeinMinerEnchant(this));
EnchantRegistry.register(new AutoSmeltEnchant(this));
EnchantRegistry.register(new LifestealEnchant(this));
EnchantRegistry.register(new FlightBootsEnchant(this));

        // Commands
        getCommand("ce").setExecutor(new CECommand(this));

        // Listeners: route events to active enchants
getServer().getPluginManager().registerEvents(new EventRouterListener(this), this);
getServer().getPluginManager().registerEvents(new AnvilApplyListener(this), this);
getServer().getPluginManager().registerEvents(new com.example.cef.listener.FlightListener(this), this);

        getLogger().info("CustomEnchantFramework enabled with " + EnchantRegistry.size() + " enchants");
    }
}
