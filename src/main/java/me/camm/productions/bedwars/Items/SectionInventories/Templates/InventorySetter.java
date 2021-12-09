package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import me.camm.productions.bedwars.Items.ItemDatabases.DefaultTemplateNavigation;
import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.DefaultTemplateNavigation.EMPTY;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

//this is a subclass of inventory.
public abstract class InventorySetter extends InventoryConfiguration implements ISectionInventory
{
    protected final boolean isInflated;

    public InventorySetter(InventoryHolder owner, InventoryType type,boolean isInflated) {
        super(owner, type);
        this.isInflated = isInflated;
    }

    public InventorySetter(InventoryHolder owner, InventoryType type, String title,boolean isInflated) {
        super(owner, type, title);
        this.isInflated = isInflated;
    }

    public InventorySetter(InventoryHolder owner, int size,boolean isInflated) {
        super(owner, size);
        this.isInflated = isInflated;
    }

    public InventorySetter(InventoryHolder owner, int size, String title, boolean isInflated) {
        super(owner, size, title);
        this.isInflated = isInflated;
    }

    @Override
    public void setItem(int slot, GameItem item) {
        this.setItem(slot, item, false);
    }

    @Override
    public void setItem(int index, GameItem item, boolean isInflated) {
        try
        {
           super.setItem(index, ItemHelper.toDisplayItem(item, isInflated));
        }
        catch (IndexOutOfBoundsException | NullPointerException ignored)
        {

        }
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
        for (int slot: range)
            setItem(slot,EMPTY.getItem(),false);


    }

    @Override
    public ArrayList<ItemSet> packageInventory(Inventory inv)
    {
        ArrayList<ItemSet> items = new ArrayList<>();
        for (int slot=QUICK_INV_BORDER_START.getValue();slot<=QUICK_INV_BORDER_END.getValue();slot++)
        {
            if (ItemHelper.isItemInvalid(inv.getItem(slot)))
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
