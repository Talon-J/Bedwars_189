package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.PotionConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PotionSectionInventory extends InventorySetter {
    private final boolean isInflated;
    private Inventory inv;

    public PotionSectionInventory(boolean isInflated) {
        this.isInflated = isInflated;
        inv = Bukkit.createInventory(null, InventoryLocation.SHOP_SIZE.getValue(), InventoryName.POTION.getTitle());
        inv = setTemplate(inv, isInflated,false);
        setInventoryItems();
    }

    @Override
    public void setItem(int slot, GameItem item)
    {
        inv.setItem(slot, toDisplayItem(item, isInflated));
    }

    @Override
    public void setInventoryItems()
    {
        for (PotionConfig config: PotionConfig.values())
            setItem(config.getSlot(),config.getItem(),inv,isInflated);
    }

    @Override
    public Inventory getInventory()
    {
        return inv;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        try
        {
            inv.setItem(slot,item);
        }
        catch (IndexOutOfBoundsException ignored)
        {

        }

    }

    @Override
    public ArrayList<ItemSet> packageInventory(Inventory inv) {
        return super.packageInventory(inv);
    }
}
