package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import org.bukkit.inventory.ItemStack;

public abstract class TeamInventorySetter
{
    protected abstract void setTemplate();
    protected abstract void setItems();
    public abstract void setItem(int slot, ItemStack item);

}
