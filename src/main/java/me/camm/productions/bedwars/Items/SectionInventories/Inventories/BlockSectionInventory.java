package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.BlockConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;


import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.SHOP_SIZE;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.BLOCKS;


//Universal for all players
public class BlockSectionInventory extends InventorySetter {

    public BlockSectionInventory(boolean isInflated)
    {
        super(null,SHOP_SIZE.getValue(),BLOCKS.getTitle(),isInflated);
        setTemplate(isInflated,false);
        setInventoryItems();
    }


    @Override
    public void setInventoryItems() {
        for (BlockConfig config: BlockConfig.values())
            setItem(config.getSlot(),config.getItem(), isInflated);
    }

}

