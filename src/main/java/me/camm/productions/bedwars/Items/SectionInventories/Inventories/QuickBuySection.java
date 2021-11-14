package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.DefaultQuickItemConfig;
import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.QUICK_BUY;

public class QuickBuySection extends InventorySetter {
    private Inventory inv;
    private final boolean isInflated;
   private ArrayList<ItemSet> values;

    public QuickBuySection(boolean isInflated, ArrayList<ItemSet> values)
    {
        super();
        this.isInflated = isInflated;

        if (values!=null)
        this.values = filter(values);

        inv = Bukkit.createInventory(null,SHOP_SIZE.getValue(),QUICK_BUY.getTitle());
        inv = setTemplate(inv, isInflated,true);
        setInventoryItems();
    }


    //Setting the default configurations.
    @Override
    public void setInventoryItems()
    {
        if (values==null || values.size() == 0)
        {

            //Setting the default items.
            for (DefaultQuickItemConfig config : DefaultQuickItemConfig.values()) {
                int[] slots = config.getSlots();
                for (int slot : slots)
                    setItem(slot, config.getItem());
            }
        }
        else {
            //Filling the remaining slots with the configured items.
            for (ItemSet set : values)
                setItem(set.getSlot(), set.getItem());
        }

        fillEmpties();
    }

    private void fillEmpties()
    {
        for (int slot=QUICK_INV_BORDER_START.getValue();slot<=QUICK_INV_BORDER_END.getValue();slot++)
            if (inv.getItem(slot)==null && !(slot%9 == 0 || (slot+1)%9 == 0))
                setItem(slot,GameItem.EMPTY_SLOT);

    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public void setItem(int slot, ItemStack item)
    {
        try {
            inv.setItem(slot, item);
        }
        catch (IndexOutOfBoundsException ignored)
        {

        }
    }

    @Override
    public void setItem(int slot, GameItem item)
    {
        try {
            inv.setItem(slot, toDisplayItem(item, isInflated));
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
