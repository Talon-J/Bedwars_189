package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import me.camm.productions.bedwars.Items.ItemDatabases.DefaultTemplateNavigation;
import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.DefaultTemplateNavigation.EMPTY;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public abstract class InventorySetter extends ItemHelper implements ISectionInventory
{
    @Override
    public Inventory setItem(int index, GameItem item, Inventory inv, boolean isInflated) {
        try
        {
            inv.setItem(index, toDisplayItem(item, isInflated));
            return inv;
        }
        catch (IndexOutOfBoundsException | NullPointerException e)
        {
            return inv;
        }
    }

    /*
    Setting the items at the top of an inv for navigation
    @param inv
    @param isInflated
    @param includeEmpties
     */
    @Override
    public Inventory setTemplate(Inventory inv, boolean isInflated, boolean includeEmpties)
    {

        for (DefaultTemplateNavigation template: DefaultTemplateNavigation.values())
        {
            if (template == EMPTY)
                continue;

            int[] range = template.getRange();
            for (int slot: range)
                setItem(slot,template.getItem(),inv,isInflated);
        }


        return includeEmpties ? fillEmpties(inv): inv;
    }

    private Inventory fillEmpties(Inventory inv)
    {
        int[] range = EMPTY.getRange();
        for (int slot: range)
            setItem(slot,EMPTY.getItem(),inv,false);
        return inv;

    }

    @Override
    public ArrayList<ItemSet> packageInventory(Inventory inv)
    {
        ArrayList<ItemSet> items = new ArrayList<>();
        for (int slot=QUICK_INV_BORDER_START.getValue();slot<=QUICK_INV_BORDER_END.getValue();slot++)
        {
            if (isItemInvalid(inv.getItem(slot)))
                continue;

            ItemStack stack = inv.getItem(slot);
            try
            {
                GameItem item = GameItem.valueOf(stack.getItemMeta().getDisplayName());
                items.add(new ItemSet(item,slot));
            }
            catch (IllegalArgumentException | NullPointerException ignored)
            {
            }
        }
        return items;
    }
}
