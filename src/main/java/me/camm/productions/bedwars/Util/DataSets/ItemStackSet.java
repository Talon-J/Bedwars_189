package me.camm.productions.bedwars.Util.DataSets;

import org.bukkit.inventory.ItemStack;

public class ItemStackSet {

    private final ItemStack stack;
    private final int slot;

    public ItemStackSet(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getSlot() {
        return slot;
    }
}
