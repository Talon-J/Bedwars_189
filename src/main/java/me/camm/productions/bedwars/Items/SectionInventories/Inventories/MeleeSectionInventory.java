package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.MeleeConfig;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

//Universal for all players
public class MeleeSectionInventory extends InventorySetter {

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
