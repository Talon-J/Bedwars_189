package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.DefaultQuickItemConfig;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemStackSet;
import me.camm.productions.bedwars.Util.DataSets.ShopItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.QUICK_BUY;

public class QuickBuySection extends ShopInventorySetter {
   private ArrayList<ShopItemSet> values;

    public QuickBuySection(boolean isInflated, ArrayList<ShopItemSet> values)
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
                    setItem(slot, config.getItem(),isInflated);
            }
        }
        else {
            //Filling the remaining slots with the configured items.
            for (ShopItemSet set : values)
                setItem(set.getSlot(), set.getItem(),isInflated);
        }

        fillEmpties();
    }

    private void fillEmpties()
    {
        for (int slot=QUICK_INV_BORDER_START.getValue();slot<=QUICK_INV_BORDER_END.getValue();slot++)
            if (getItem(slot)==null && !(slot%9 == 0 || (slot+1)%9 == 0))
                setItem(slot, ShopItem.EMPTY_SLOT);
    }

    public ArrayList<ItemStackSet> getItems(){
        ArrayList<ItemStackSet> items = new ArrayList<>();
        for (int start = QUICK_INV_BORDER_START.getValue();start <= QUICK_INV_BORDER_END.getValue();start++) {
           items.add(new ItemStackSet(getItem(start),start));
        }
        return items;
    }
}
