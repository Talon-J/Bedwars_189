package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.BlockConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.SHOP_SIZE;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.BLOCKS;


//Universal for all players
public class BlockSectionInventory extends InventorySetter {
    private  Inventory inv;
    private final boolean isInflated;

    public BlockSectionInventory(boolean isInflated)
    {
        this.isInflated = isInflated;
        this.inv = Bukkit.createInventory(null,SHOP_SIZE.getValue(),BLOCKS.getTitle());
        inv =  setTemplate(inv,isInflated,false);
        setInventoryItems();
    }



    @Override
    public void setInventoryItems() {
        for (BlockConfig config: BlockConfig.values())
            setItem(config.getSlot(),config.getItem(),inv, isInflated);
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
    public void setItem(int slot, ItemStack item) {
        try {
            inv.setItem(slot, item);
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

