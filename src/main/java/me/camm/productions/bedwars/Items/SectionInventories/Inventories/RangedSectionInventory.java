package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.RangedConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;

/**
 * @author CAMM
 * This inventory models a section inventory in the quick buy
 */
public class RangedSectionInventory extends ShopInventorySetter {

    public RangedSectionInventory(boolean isInflated)
    {
        super(null, InventoryProperty.SHOP_SIZE.getValue(), InventoryName.RANGED.getTitle(),isInflated);
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

