package com.example.cef.cmd;

import com.example.cef.core.CustomEnchant;
import com.example.cef.core.EnchantRegistry;
import com.example.cef.core.Util;
import com.example.cef.CustomEnchantPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CECommand implements CommandExecutor {
    private final CustomEnchantPlugin plugin;
    public CECommand(CustomEnchantPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.AQUA + "Custom Enchants:");
            for (CustomEnchant e : EnchantRegistry.all()) {
                sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.AQUA + e.id() + ChatColor.GRAY + " (" + e.displayName() + ")");
            }
            sender.sendMessage(ChatColor.YELLOW + "Usage: /"+label+" <give|remove|givebook> <id> [player] [amount]");
            return true;
        }

        String sub = args[0].toLowerCase();
        if (!(sub.equals("give") || sub.equals("remove") || sub.equals("givebook"))) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /"+label+" <give|remove|givebook> <id> [player] [amount]");
            return true;
        }
        if (args.length < 2) { sender.sendMessage(ChatColor.RED+"Missing enchant id"); return true; }
        CustomEnchant ench = EnchantRegistry.get(args[1]);
        if (ench == null) { sender.sendMessage(ChatColor.RED+"Unknown enchant id"); return true; }

        switch (sub) {
            case "give": {
                Player target = targetPlayer(sender, args, 2);
                if (target == null) return true;
                ItemStack hand = target.getInventory().getItemInMainHand();
                if (hand == null || hand.getType() == Material.AIR) { sender.sendMessage(ChatColor.RED+"Hold an item"); return true; }
                if (!ench.appliesTo().contains(hand.getType())) { sender.sendMessage(ChatColor.RED+"That item type doesn't support "+ench.displayName()); return true; }
                Util.tagItem(hand, ench.pdcKey());
                Util.addLore(hand, ench.loreLine());
                sender.sendMessage(ChatColor.GREEN+"Applied "+ench.displayName()+" to "+target.getName()+"'s item.");
                return true;
            }
            case "remove": {
                Player target = targetPlayer(sender, args, 2);
                if (target == null) return true;
                ItemStack hand = target.getInventory().getItemInMainHand();
                if (hand == null || hand.getType() == Material.AIR) { sender.sendMessage(ChatColor.RED+"Hold an item"); return true; }
                var meta = hand.getItemMeta(); if (meta != null) {
                    meta.getPersistentDataContainer().remove(Util.key(ench.pdcKey()));
                    hand.setItemMeta(meta);
                }
                // (Optional) remove lore line if present
                sender.sendMessage(ChatColor.GREEN+"Removed "+ench.displayName()+" from "+target.getName()+"'s item.");
                return true;
            }
            case "givebook": {
                if (!sender.hasPermission("cef.admin")) { sender.sendMessage(ChatColor.RED+"No permission"); return true; }
                if (args.length < 3) { sender.sendMessage(ChatColor.YELLOW+"Usage: /"+label+" givebook <id> <player> [amount]"); return true; }
                Player target = Bukkit.getPlayerExact(args[2]);
                if (target == null) { sender.sendMessage(ChatColor.RED+"Player not found"); return true; }
                int amount = 1; if (args.length >= 4) { try { amount = Math.max(1, Math.min(64, Integer.parseInt(args[3]))); } catch (Exception ignored) {} }
                target.getInventory().addItem(ench.createBook(amount));
                sender.sendMessage(ChatColor.GREEN+"Gave "+amount+"x book for "+ench.displayName()+" to "+target.getName());
                return true;
            }
        }
        return true;
    }

    private Player targetPlayer(CommandSender sender, String[] args, int idx) {
        Player target;
        if (args.length > idx) {
            target = Bukkit.getPlayerExact(args[idx]);
            if (target == null) { sender.sendMessage(ChatColor.RED+"Player not found"); return null; }
        } else {
            if (!(sender instanceof Player)) { sender.sendMessage(ChatColor.RED+"Console must specify a player"); return null; }
            target = (Player) sender;
        }
        return target;
    }
}
