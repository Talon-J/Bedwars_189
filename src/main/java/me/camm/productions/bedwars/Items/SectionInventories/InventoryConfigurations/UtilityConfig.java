package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_ROW2_START;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum UtilityConfig
{
    APPLE(QUICK_INV_BORDER_START.getValue(), GameItem.GOLDEN_APPLE),
    BEDBUG(QUICK_INV_BORDER_START.getValue()+1, GameItem.BEDBUG),
    WATER(QUICK_INV_BORDER_START.getValue()+2, GameItem.WATER),
    DREAM_DEFENDER(QUICK_INV_BORDER_START.getValue()+3, GameItem.DREAM_DEFENDER),
    FIREBALL(QUICK_INV_BORDER_START.getValue()+4, GameItem.FIREBALL),
    TNT(QUICK_INV_BORDER_START.getValue()+5, GameItem.TNT),
    ENDER_PEARL(QUICK_INV_BORDER_START.getValue()+6, GameItem.ENDER_PEARL),


    //2ND ROW
    BRIDGE_EGG(QUICK_INV_BORDER_ROW2_START.getValue(), GameItem.BRIDGE_EGG),
    MILK(QUICK_INV_BORDER_ROW2_START.getValue()+1, GameItem.MILK),
    SPONGE(QUICK_INV_BORDER_ROW2_START.getValue()+2, GameItem.SPONGE),
    TOWER(QUICK_INV_BORDER_ROW2_START.getValue()+3, GameItem.POPUP_TOWER);




    private final int slot;
    private final GameItem item;

    UtilityConfig(int slot, GameItem item) {
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
