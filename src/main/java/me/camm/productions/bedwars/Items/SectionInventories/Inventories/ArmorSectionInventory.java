package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.ArmorConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;


//1 for each player
//this is an instance of Inventory
public class ArmorSectionInventory extends ShopInventorySetter {

    //(InventoryHolder owner, int size, String title)
    public ArmorSectionInventory(boolean isInflated) {
        super(null, InventoryProperty.SHOP_SIZE.getValue(),InventoryName.ARMOR.getTitle(),isInflated);
        setTemplate(isInflated,false);
        setInventoryItems();

    }

    @Override
    public void setInventoryItems()
    {
        for (ArmorConfig config: ArmorConfig.values())
            super.setItem(config.getSlot(),config.getItem(),isInflated);
        //method in InventorySetter

    }
}
