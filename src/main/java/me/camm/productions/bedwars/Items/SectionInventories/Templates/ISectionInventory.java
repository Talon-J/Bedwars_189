package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface ISectionInventory
{
    void setTemplate(boolean isInflated, boolean includeEmpties);
    void setInventoryItems();
    void setItem(int index, GameItem item, boolean isInflated);
    void setItem(int slot, GameItem item);
    ArrayList<ItemSet> packageInventory(Inventory inv);



}
