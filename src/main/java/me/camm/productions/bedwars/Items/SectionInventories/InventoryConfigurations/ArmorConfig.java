package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum ArmorConfig
{
    //Note that this enum should be synced up with both the quick buy and section inventories of the player.
    CHAIN(QUICK_INV_BORDER_START.getValue(), GameItem.CHAIN_MAIL),
    IRON(QUICK_INV_BORDER_START.getValue()+1, GameItem.IRON_ARMOR),
    DIAMOND(QUICK_INV_BORDER_START.getValue()+2, GameItem.DIAMOND_ARMOR);

    private final int slot;
    private final GameItem item;

    ArmorConfig(int slot, GameItem item)
    {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public GameItem getItem() {
        return item;
    }
}
