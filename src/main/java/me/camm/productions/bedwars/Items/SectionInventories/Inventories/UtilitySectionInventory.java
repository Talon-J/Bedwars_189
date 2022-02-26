package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.UtilityConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;

public class UtilitySectionInventory extends ShopInventorySetter {


    public UtilitySectionInventory(boolean isInflated) {
        super(null, InventoryProperty.SHOP_SIZE.getValue(), InventoryName.UTILITY.getTitle(), isInflated);
   setTemplate(isInflated,false);
        setInventoryItems();
    }


    @Override
    public void setInventoryItems()
    {
        for (UtilityConfig config: UtilityConfig.values())
            setItem(config.getSlot(),config.getItem(),isInflated);
    }

}
