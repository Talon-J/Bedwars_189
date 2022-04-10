package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.BlockConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;


import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty.SHOP_SIZE;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.BLOCKS;


/**
 * @author CAMM
 * This inventory models a section inventory in the quick buy
 */
//Universal for all players
public class BlockSectionInventory extends ShopInventorySetter {

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

