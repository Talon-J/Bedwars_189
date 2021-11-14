package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.ArmorConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

//1 for each player
public class ArmorSectionInventory extends InventorySetter {
    private Inventory inv;
    private final boolean isInflated;

    public ArmorSectionInventory(boolean isInflated) {
        super();
        this.inv = Bukkit.createInventory(null, InventoryLocation.SHOP_SIZE.getValue(), InventoryName.ARMOR.getTitle());
        this.isInflated = isInflated;
        inv = setTemplate(inv, isInflated,false);
        setInventoryItems();

    }

    @Override
    public void setInventoryItems()
    {
        for (ArmorConfig config: ArmorConfig.values())
            setItem(config.getSlot(),config.getItem(),inv,isInflated);

    }


    //Method for setting enchants to the item.
    @Override
    public void setItem(int slot, ItemStack item)
    {
        try
        {
            inv.setItem(slot,item);
        }
        catch (IndexOutOfBoundsException ignored)
        {

        }
    }

    @Override
    public void setItem(int slot, GameItem item)
    {
        inv.setItem(slot, toDisplayItem(item, isInflated));
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public ArrayList<ItemSet> packageInventory(Inventory inv) {
        return super.packageInventory(inv);
    }

}
