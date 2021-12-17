package me.camm.productions.bedwars.Arena.Players.Managers;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Items.ItemDatabases.TieredItem;
import me.camm.productions.bedwars.Util.Helpers.IArenaWorldHelper;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.HOT_BAR_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory.ARMOR;
import static me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory.MELEE;


/*
TODO:
- Add an inventory for when the player modifies their hotbar manager.
- see if the manager is active or passive, code appropriately for that. We now know it is active


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
        //armor is not set in this method.
        if (ItemHelper.isItemInvalid(item)||ItemHelper.isPlaceHolder(category)||
                category==ARMOR||!player.isOnline())
            return;

        Inventory inventory = player.getInventory();

         //if it is a degradable tool, then we attempt to replace the one in the player's inventory first
        IS_TOOL:
        {
            //added the category of MELEE. Test pls.
            if (category == ItemCategory.TOOLS || category == MELEE) {
                GameItem detected = ItemHelper.getAssociate(item);
                TieredItem tiered = detected == null ? null : ItemHelper.isTieredItem(detected);
                if (tiered == null) {
                    break IS_TOOL;
                }

                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    ItemStack current = inventory.getItem(slot);
                    if (ItemHelper.isItemInvalid(current))
                        continue;

                    GameItem associate = ItemHelper.getAssociate(current);
                   TieredItem associateTier = ItemHelper.isTieredItem(associate);
                   if (associateTier == null)
                       continue;

                   if (tiered.getCategory()!=associateTier.getCategory())
                   continue;

                       if (associateTier.getIndex() < tiered.getIndex()) {
                           inventory.setItem(slot, item);
                           return;
                       }

                }
            }
        }//this piece is good. it replaces. (Tested for tools. For Swords, haven't tested yet.)


        //First we try to put it in the preferred slot and replace items.
       for (int slot=0;slot<layout.length;slot++)
       {
           if (layout[slot]==null)
               continue;

           if (layout[slot]==category)
           {
               if (isSlotEmpty(inventory,slot))
               {
                   inventory.setItem(slot, item);
                   player.updateInventory();
                   return;
               }
               else
               {
                   //at this point we know that it is not null.
                   ItemStack stack = inventory.getItem(slot);

                   //It's likely not part of the game, so let's just replace it.
                   if (stack.getItemMeta()==null)
                   {
                       inventory.setItem(slot,item);
                       player.updateInventory();
                       return;
                   }

                   GameItem itemCharted = ItemHelper.getAssociate(stack);

                   //It's not part of the game, so let's just replace it.
                   if (itemCharted == null)
                   {
                       inventory.setItem(slot,item);
                       player.updateInventory();
                       return;
                   }

                   //if it is the same, don't replace it
                   if (category == itemCharted.category)
                       continue;

                   //If the inventory has room, and the categories are not the same, then replace the item and
                   //move it deeper into the inventory.
                   if (ItemHelper.hasRoom(inventory,item,item.getAmount()))
                   {
                       try {
                           inventory.setItem(slot, item);
                           player.updateInventory();
                           return;
                       }
                       catch (IndexOutOfBoundsException ignored) {
                           inventory.addItem(stack);
                           player.updateInventory();
                           return;
                       }

                   }
               }
           }
       }

       //if for some reason the layout is clear of any preferences...
       if (ItemHelper.hasRoom(inventory, item,item.getAmount()))
          inventory.addItem(item);

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
