package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum RangedConfig
{
    ARROW(QUICK_INV_BORDER_START.getValue(), GameItem.ARROW),
    BOW(QUICK_INV_BORDER_START.getValue()+1, GameItem.BOW),
    POWER(QUICK_INV_BORDER_START.getValue()+2, GameItem.POW_BOW),
    PUNCH(QUICK_INV_BORDER_START.getValue()+3, GameItem.PUNCH_BOW);
    //start from slot 19

    private final int slot;
    private final GameItem item;

    RangedConfig(int slot, GameItem item) {
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
