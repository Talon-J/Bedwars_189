package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.ToolsConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;


public class ToolsSectionInventory extends ShopInventorySetter {


    public ToolsSectionInventory(boolean isInflated)
    {
        super(null, InventoryProperty.SHOP_SIZE.getValue(), InventoryName.TOOLS.getTitle(),isInflated);
        setTemplate(isInflated,false);
        setInventoryItems();
    }

    @Override
    public void setInventoryItems() {
        for (ToolsConfig config: ToolsConfig.values())
            setItem(config.getSlot(),config.getItem(),isInflated);
    }

}
