package tech.shadowsystems.meetupuhc.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.shadowsystems.meetupuhc.utilities.ChatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Please see LICENSE.yml for the license of this project.
 */

public class ItemBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.itemStack = item;
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        this.itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level){
        this.itemMeta.addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemBuilder type(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder durability(int dura) {
        Material mat = this.itemStack.getType();
        this.itemStack = new ItemStack(mat, 1, (short)((byte)dura));
        return this;
    }

    public ItemBuilder addlore(String string) {
        Object lore = this.itemMeta.getLore();
        if(lore == null) {
            lore = new ArrayList();
        }

        ((List)lore).add(ChatUtil.format(string));
        this.itemMeta.setLore((List)lore);
        this.itemStack.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemBuilder amount(int amount){
        this.itemStack.setAmount(amount);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public ItemBuilder lore(List<String> lore) {
        this.itemMeta.setLore(lore);
        this.itemStack.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemBuilder glow() {
        this.itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        this.itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        this.itemStack.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}


