package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.TeamInventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TeamBuyInventory extends TeamInventorySetter
{

    public TeamBuyInventory(InventoryHolder owner, InventoryType type) {
        super(owner, type);
    }

    public TeamBuyInventory(InventoryHolder owner, InventoryType type, String title) {
        super(owner, type, title);
    }

    public TeamBuyInventory(InventoryHolder owner, int size) {
        super(owner, size);
    }

    public TeamBuyInventory(InventoryHolder owner, int size, String title) {
        super(owner, size, title);
    }



    @Override
    public void setTemplate(boolean isInflated, boolean includeEmpties) {

    }

    @Override
    public void setInventoryItems() {

    }

    @Override
    public void setItem(int index, GameItem item, boolean isInflated) {

    }

    @Override
    public void setItem(int slot, GameItem item) {

    }

    @Override
    public ArrayList<ItemSet> packageInventory(Inventory inv) {
        return null;
    }

    @Override
    public void setItem(int slot, ItemStack item) {

    }
}
