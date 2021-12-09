package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.PotionConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;


public class PotionSectionInventory extends InventorySetter {


    public PotionSectionInventory(boolean isInflated) {
        super(null, InventoryLocation.SHOP_SIZE.getValue(), InventoryName.POTION.getTitle(),isInflated);
        super.setTemplate(isInflated,false);
        setInventoryItems();
    }

    @Override
    public void setInventoryItems()
    {
        for (PotionConfig config: PotionConfig.values())
            setItem(config.getSlot(),config.getItem(),isInflated);
    }

}
