package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.DefaultQuickItemConfig;
import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.QUICK_BUY;

public class QuickBuySection extends ShopInventorySetter {
   private ArrayList<ItemSet> values;

    public QuickBuySection(boolean isInflated, ArrayList<ItemSet> values)
    {
        super(null,SHOP_SIZE.getValue(),QUICK_BUY.getTitle(),isInflated);

        if (values!=null)
        this.values = ItemHelper.filter(values);

        setTemplate(isInflated,true);
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
            if (getItem(slot)==null && !(slot%9 == 0 || (slot+1)%9 == 0))
                setItem(slot,GameItem.EMPTY_SLOT);
    }
}
