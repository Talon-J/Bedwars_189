package me.camm.productions.bedwars.Arena.Players.Managers;

import me.camm.productions.bedwars.Items.ItemDatabases.*;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.HotBarConfig;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty.HOT_BAR_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory.*;


/*
- Add an inventory for when the player modifies their hotbar manager.

 */
public class HotbarManager
{
    private final ItemCategory[] layout;
    private final Inventory editor;

    public HotbarManager()
    {
        this.layout = new ItemCategory[HOT_BAR_END.getValue()];
         editor = Bukkit.createInventory(null, InventoryProperty.SHOP_SIZE.getValue(),InventoryName.HOTBAR_MANAGER.getTitle());

        initInventory();
        addCategory(ItemCategory.MELEE,0);
        updateDisplay();
    }



    public HotbarManager(ItemCategory[] layout)
    {

        editor = Bukkit.createInventory(null, InventoryProperty.SHOP_SIZE.getValue(),InventoryName.HOTBAR_MANAGER.getTitle());
        if ((layout==null)||(layout.length!= HOT_BAR_END.getValue())) {
            this.layout = new ItemCategory[HOT_BAR_END.getValue()];
            addCategory(ItemCategory.MELEE,0);
        }
        else {
            this.layout = layout;

            boolean empty = true;
            for (ItemCategory category: layout) {
                if (category!=null)
                {
                    empty = false;
                    break;
                }
            }

            if (empty)
                addCategory(MELEE,0);
        }


        initInventory();
        updateDisplay();
    }


    private void initInventory(){
        for (HotBarConfig config: HotBarConfig.values()) {
            ItemStack item = ItemHelper.toSimpleItem(config.getMat(),config.getName());
            for (int slot: config.getSlots()) {
                editor.setItem(slot, item);
            }
        }
    }

    //we are only updating the layout here.
    public boolean updateLayout(int clickedSlot, ItemStack placed){
        clickedSlot -= InventoryProperty.LARGE_ROW_FIVE_START.getValue();
        if (clickedSlot < 0 || clickedSlot > layout.length)
            return false;

        if (placed==null) {
            layout[clickedSlot] = null;
            return true;
        }

        ItemCategory category = ItemHelper.getHotBarAssociate(placed);
        if (category == null)
            return false;

        if (category == OPERATOR)
            return false;

        layout[clickedSlot] = category;
        return true;
    }


    public void updateDisplay(){
        for (int slot=0;slot<layout.length;slot++) {
            ItemStack stack = ItemHelper.toBarItem(layout[slot]);
            editor.setItem(slot+InventoryProperty.LARGE_ROW_FIVE_START.getValue(),stack);
        }
    }


    public void reset(){

        layout[0] = ItemCategory.MELEE;
        addCategory(MELEE,0);

        for (int slot=1;slot<layout.length;slot++) {
            layout[slot] = null;
            addCategory(null,slot);
        }
    }


    //slot category is in terms of the hotbar slots
    public void addCategory(ItemCategory category, int slot)
    {
        if (slot>layout.length||slot<0)
            return;

        layout[slot] = category;
        editor.setItem(slot+ InventoryProperty.LARGE_ROW_FIVE_START.getValue(),ItemHelper.toBarItem(category));
    }

    //shouldn't matter if raw or not since top inv is 0
    public static boolean slotInRangeTake(int slot){
        return slot >= InventoryProperty.LARGE_ROW_THREE_START.getValue() &&
                slot <= InventoryProperty.LARGE_ROW_THREE_END.getValue();
    }

    public static boolean slotInRangePlace(int slot) {
        return slot >= InventoryProperty.LARGE_ROW_FIVE_START.getValue() &&
                slot <= InventoryProperty.LARGE_ROW_FIVE_END.getValue();
    }




    public ItemCategory[] getLayout()
    {
        return layout;
    }

    public void display(Player player) {
        player.openInventory(editor);
    }

    public boolean invEquals(Inventory other){
        return other.equals(editor);
    }


        /*
        TODO:
        refactor this to include game items as the parameters. Makes it SO much more efficient.


       DEFINITION OF HAS ROOM
        get an array of all empty slots. Make sure the array length is at least 2.

         y
        [x] [] [] []

        Parameters:

        For TIERED ITEMS
        if the inventory is full, return

        if x and y are a tiered item, and index x is less than index y, then x replaces y.
        If index x  == index y, then we search for the next spot in the hotbar to place y.
        First we check for reserved slots. If the all of the reserved slots are full, then we then
        put the item in a non reserved slot if it exists.

        If no spot exists [since the hotbar is full], then we go through all of the slots which are dedicated for the category of item
        We should make an array containing all of the possible slots when we do this so we don't have to loop over again
        if no spot is vacant, then we go through all of the slots which have items in them.
        If the category of the item is not the same as the category of the reserved slot,
        then we first check the fullness of the inventory. If the inventory is not full, then
        replace the item in the slot with the item, and put the replaced item in the next vacant or holdable slot
        in the inventory.


        for NORMAL ITEMS
        if the inventory is full, return

        if x and y are not the same category, and the slot of x is reserved for y, then
        we first check if the inv is full. If it is not full, then y replaces x.
        Else, if it is full, do not allow the player to purchase.

        if x and y are the same category, then we loop through all of the other slots, looking for a vacant spot, with
        the same reservation.


        If there is a slot with a different reservation, and all reserved slots are full, then place the item in the slot.

        If all hotbar slots are full, then check for items that are in slots with different reservations.
        If such item exists on a slot with the same reservation as y, then replace that item with y and
        put the replaced item deeper in the inventory.




         */

    public void set(ItemStack item, ShopItem enumItem, Player player)
    {
        ItemCategory category = enumItem.category;
        Inventory playerInv = player.getInventory();



        //armor is not set in this method.
        if (ItemHelper.isItemInvalid(item)||ItemHelper.isPlaceHolder(category)||
                category==ARMOR||!player.isOnline())
            return;

        //maybe do a refunding action here?
        if (playerInv.firstEmpty()==-1) {
            player.sendMessage(ChatColor.RED + "Your inventory is full!");
            return;
        }

        TieredItem enumTiered = ItemHelper.isTieredItem(enumItem);
        // normal item
        if (enumTiered != null) {

            if (enumTiered.getIndex() == ItemHelper.getLowestTier(enumTiered).getIndex()) {

                doNormalSet(playerInv, enumItem, item,player);
                return;
            }

            //If the item does not replace other things

            //First we try to find items that it is trying to replace.
            ItemStack[] items = playerInv.getContents();
            for (int slot = 0; slot < items.length; slot++) {
                ItemStack residing = items[slot];
                if (ItemHelper.isItemInvalid(residing))
                    continue;

                ShopItem associate = ItemHelper.getAssociate(residing);
                if (associate == null)
                    continue;

                TieredItem associateTier = ItemHelper.isTieredItem(associate);
                if (associateTier == null)
                    continue;

                if (associateTier.getCategory() != enumTiered.getCategory())
                    continue;

                //It replaces everything
                if (enumTiered.isTotalReplacing()) {
                    if (associateTier.getIndex() < enumTiered.getIndex()) {
                        playerInv.setItem(slot, item);
                        return;
                    }
                } else {
                    //If it is the smallest index
                    if (associateTier.getIndex() == ItemHelper.getLowestTier(associateTier).getIndex()) {
                        playerInv.setItem(slot, item);
                        return;
                    }

                }
            }

            //if there is nothing to replace, do a normal set.

        }
        doNormalSet(playerInv,enumItem, item, player);

    }


    //When we need behaviour similar to that of setting normally
    private void doNormalSet(Inventory playerInv, ShopItem enumItem, ItemStack item, Player player)
    {

        //if there is already a slot in the inv with the stack
        //merge the stacks together.
      for (int slot=0;slot<layout.length;slot++) {

          ItemStack stack = playerInv.getItem(slot);
          if (item.isSimilar(stack) && (stack.getAmount()+item.getAmount() <= item.getMaxStackSize()))
          {
              item.setAmount(item.getAmount()+stack.getAmount());
              playerInv.setItem(slot,item);
              return;
          }
      }



        for (int slot=0;slot<layout.length;slot++)
        {
            ItemCategory reserved = layout[slot];
            ItemStack residing = playerInv.getItem(slot);

            // if the reserved slot is in the same category
            if (reserved != enumItem.category)
                continue;


             // test if the residing item is valid
             ShopItem residingAssociate = ItemHelper.getAssociate(residing);

                /*
                So if the slot is empty and the slot is reserved, place it in.
                 */
             if (residingAssociate == null) {
                //if we cannot resolve the residing item as an enum item

                 //check if it's currency
                 if (ItemHelper.isCurrencyItem(residing))
                 {
                     ItemStack residingPlaceholder = residing.clone();
                     playerInv.setItem(slot, item);
                     playerInv.addItem(residingPlaceholder);
                 }
                 else
                     playerInv.setItem(slot, item);
                    return;
                }


                //so if the two categories between residing and placing in are not the same,
                // and the reserved slot matches the one to place in,
                //then replace the item.
             if ((residingAssociate.category != enumItem.category))
                 {
                     //replace the item in the slot.
                     //Add the replaced item back.
                     ItemStack residingPlaceholder = residing.clone();
                    playerInv.setItem(slot,item);
                   playerInv.addItem(residingPlaceholder);
                   return;
                 }

                //in this case, residing and item are the same category and residing is in the reserved slot, so we
                //try to place it in.
                 if (!item.isSimilar(residing))
                     continue;

                 if ( (item.getAmount() + residing.getAmount()) > residing.getMaxStackSize() )
                     continue;

                 item.setAmount(item.getAmount() + residing.getAmount());
                 playerInv.setItem(slot, item);
                 return;

        }


        //Now we look at the other slots. (ones with other categories)


        for (int slot=0;slot<layout.length;slot++)
        {
            ItemStack residing = playerInv.getItem(slot);

            //If the category is not specified there...
               if (layout[slot] == null)
               {
                   //if the item is undefined or it is currency, place it in.
                   if (residing == null || residing.getType() == Material.AIR) {
                       playerInv.setItem(slot,item);
                       return;
                   }
                   else if (ItemHelper.isCurrencyItem(residing))
                   {
                       ItemStack cloned = residing.clone();
                       playerInv.setItem(slot, item);
                       playerInv.addItem(cloned);
                       return;
                   }
               }
        }

        //try to place it in their main hand
        ItemStack hand = player.getInventory().getItemInHand();
        if (ItemHelper.isItemInvalid(hand)) {
            player.setItemInHand(item);
            return;
        }

        //If the entire hotbar is unavailable, then just add the item to the inv.
        playerInv.addItem(item);
    }
}
