package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.RangedConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;


public class RangedSectionInventory extends InventorySetter {

    public RangedSectionInventory(boolean isInflated)
    {
        super(null, InventoryLocation.SHOP_SIZE.getValue(), InventoryName.RANGED.getTitle(),isInflated);
        setTemplate(isInflated,false);
        setInventoryItems();
    }

    @Override
    public void setInventoryItems()
    {
        for (RangedConfig config: RangedConfig.values())
            setItem(config.getSlot(),config.getItem(),isInflated);
    }
}

