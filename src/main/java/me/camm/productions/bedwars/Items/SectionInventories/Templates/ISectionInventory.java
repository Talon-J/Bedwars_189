package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Util.DataSets.ShopItemSet;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * @author CAMM
 * This is an interface for functionality that section inventories should have
 */
public interface ISectionInventory
{
    void setTemplate(boolean isInflated, boolean includeEmpties);
    void setInventoryItems();
    void setItem(int index, ShopItem item, boolean isInflated);
   // ArrayList<ShopItemSet> packageInventory(Inventory inv);



}
