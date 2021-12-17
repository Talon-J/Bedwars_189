package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.MeleeConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;

//Universal for all players
public class MeleeSectionInventory extends ShopInventorySetter {

    public MeleeSectionInventory(boolean isInflated) {
        super(null, InventoryLocation.SHOP_SIZE.getValue(), InventoryName.MELEE.getTitle(),isInflated);
        super.setTemplate(isInflated,false);
       setInventoryItems();
    }

    @Override
    public void setInventoryItems() {
        for (MeleeConfig config: MeleeConfig.values())
            setItem(config.getSlot(),config.getItem(),isInflated);
    }
}
