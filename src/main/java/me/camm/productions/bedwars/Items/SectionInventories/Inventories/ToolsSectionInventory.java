package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.ToolsConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;


public class ToolsSectionInventory extends InventorySetter {


    public ToolsSectionInventory(boolean isInflated)
    {
        super(null, InventoryLocation.SHOP_SIZE.getValue(), InventoryName.TOOLS.getTitle(),isInflated);
        setTemplate(isInflated,false);
        setInventoryItems();
    }

    @Override
    public void setInventoryItems() {
        for (ToolsConfig config: ToolsConfig.values())
            setItem(config.getSlot(),config.getItem(),isInflated);
    }

}
