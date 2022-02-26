package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.PotionConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;


public class PotionSectionInventory extends ShopInventorySetter {


    public PotionSectionInventory(boolean isInflated) {
        super(null, InventoryProperty.SHOP_SIZE.getValue(), InventoryName.POTION.getTitle(),isInflated);
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
