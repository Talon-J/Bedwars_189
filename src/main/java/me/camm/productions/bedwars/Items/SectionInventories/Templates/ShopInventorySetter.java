package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import me.camm.productions.bedwars.Items.ItemDatabases.DefaultTemplateNavigation;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Util.DataSets.ShopItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.DefaultTemplateNavigation.EMPTY;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty.QUICK_INV_BORDER_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty.QUICK_INV_BORDER_START;

//this is a subclass of inventory.
public abstract class ShopInventorySetter extends CraftInventoryCustom implements ISectionInventory
{
    protected final boolean isInflated;

    public ShopInventorySetter(InventoryHolder owner, InventoryType type, String title, boolean isInflated) {
        super(owner, type, title);
        this.isInflated = isInflated;
    }

    public ShopInventorySetter(InventoryHolder owner, int size, boolean isInflated) {
        super(owner, size);
        this.isInflated = isInflated;
    }

    public ShopInventorySetter(InventoryHolder owner, int size, String title, boolean isInflated) {
        super(owner, size, title);
        this.isInflated = isInflated;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);
    }

    @Override
    public void setItem(int index, ShopItem item, boolean isInflated) {
        try
        {
           super.setItem(index, ItemHelper.toDisplayItem(item, isInflated));
        }
        catch (IndexOutOfBoundsException | NullPointerException ignored)
        {

        }
    }

    @Override
    public void setInventoryItems() {

    }

    /*
        Setting the items at the top of an inv for navigation
        @param inv
        @param isInflated
        @param includeEmpties
         */
    @Override
    public void setTemplate(boolean isInflated, boolean includeEmpties)
    {

        for (DefaultTemplateNavigation template: DefaultTemplateNavigation.values())
        {
            if (template == EMPTY)
                continue;

            int[] range = template.getRange();
            for (int slot: range)
                setItem(slot,template.getItem(),isInflated);
        }
        if (includeEmpties)
        {
            fillEmpties();
        }


    }

    private void fillEmpties()
    {
        int[] range = EMPTY.getRange();
        for (int slot: range) {
            if (getItem(slot)==null || getItem(slot).getItemMeta()==null)
            setItem(slot, EMPTY.getItem(), false);
        }


    }
}
