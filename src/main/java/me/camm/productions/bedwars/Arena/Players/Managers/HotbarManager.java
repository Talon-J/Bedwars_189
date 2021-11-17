package me.camm.productions.bedwars.Arena.Players.Managers;

import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Util.Helpers.IArenaWorldHelper;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Objects;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.HOT_BAR_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory.ARMOR;
import static me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory.MELEE;


/*
TODO:
- Add an inventory for when the player modifies their hotbar manager.
- see if the manager is active or passive, code appropriately for that.


 */
public class HotbarManager implements IArenaWorldHelper
{
    private final Plugin plugin;
    private final ItemCategory[] layout;

    public HotbarManager(Plugin plugin)
    {
        super();
        this.plugin = plugin;
         this.layout = new ItemCategory[HOT_BAR_END.getValue()];
         layout[0] = ItemCategory.MELEE;
    }



    public HotbarManager(Plugin plugin,ItemCategory[] layout)
    {
        super();
        this.plugin = plugin;
        if ((layout==null)||(layout.length!= HOT_BAR_END.getValue())) {
            this.layout = new ItemCategory[HOT_BAR_END.getValue()];
            addCategory(MELEE,0);
        }
        else
            this.layout = layout;
    }


    public void addCategory(ItemCategory category, int slot)
    {
        if (slot>layout.length||slot<0)
            return;

        layout[slot] = category;
    }

    public ItemCategory getCategory(int slot)
    {
        try
        {
            return layout[slot];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }

    public ItemCategory[] getLayout()
    {
        return layout;
    }

    public void set(ItemStack item, ItemCategory category, Player player)
    {
        if (ItemHelper.isItemInvalid(item)||ItemHelper.isPlaceHolder(category)||
                category==ARMOR||!player.isOnline())
            return;

        Inventory inventory = player.getInventory();
       for (int slot=0;slot<layout.length;slot++)
       {
           if (layout[slot]==null)
               continue;

           if (layout[slot]==category)
           {
               if (isSlotEmpty(inventory,slot))
               {
                   inventory.setItem(slot, item);
                   return;
               }
               else
               {
                  /*
                  Not enough info. Is the manager "active" as in it will override existing items and
                  replace them, putting them elsewhere, or is it passive as in it will only use slots that are empty?
                   */

               }
           }

       }

    }

    public boolean isSlotEmpty(Inventory inv, int slot)
    {
        try {
            return inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR;
        }
        catch (IndexOutOfBoundsException e)
        {
            return false;
        }
    }


}
