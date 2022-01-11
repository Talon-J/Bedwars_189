package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum ArmorConfig
{
    //Note that this enum should be synced up with both the quick buy and section inventories of the player.
    CHAIN(QUICK_INV_BORDER_START.getValue(), ShopItem.CHAIN_MAIL),
    IRON(QUICK_INV_BORDER_START.getValue()+1, ShopItem.IRON_ARMOR),
    DIAMOND(QUICK_INV_BORDER_START.getValue()+2, ShopItem.DIAMOND_ARMOR);

    private final int slot;
    private final ShopItem item;

    ArmorConfig(int slot, ShopItem item)
    {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public ShopItem getItem() {
        return item;
    }
}
