package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface ISectionInventory
{
    Inventory setTemplate(Inventory inv, boolean isInflated, boolean includeEmpties);
    void setInventoryItems();
    Inventory setItem(int index, GameItem item, Inventory inv, boolean isInflated);
    Inventory getInventory();
    void setItem(int slot, ItemStack item);
    void setItem(int slot, GameItem item);
    ArrayList<ItemSet> packageInventory(Inventory inv);



}
