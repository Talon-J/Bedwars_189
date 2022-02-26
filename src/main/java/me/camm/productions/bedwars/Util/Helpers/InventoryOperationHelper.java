package me.camm.productions.bedwars.Util.Helpers;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.Managers.HotbarManager;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamTraps.*;
import me.camm.productions.bedwars.Generators.Forge;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Items.ItemDatabases.TeamItem;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.QuickBuyEditor;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.QuickBuySection;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.TeamBuyInventory;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryDoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryOperationHelper
{

    private final static ShopItem[] gameItems;
    private final static TeamInventoryConfig[] config;



    static {
        gameItems = ShopItem.values();
        config = TeamInventoryConfig.values();
    }

    /*
    Code to stop the player from accidently putting things in the shop inventories
    Note that the CURSOR is the item on the mouse AFTER the event is completed.

    E.g swap gold onto iron, cursor is iron, not gold.

     */

    public static boolean didTryToPlaceIn(InventoryClickEvent event, BattlePlayer player)
    {
        Inventory topInventory = event.getInventory();
        Inventory section = player.getShopManager().isSectionInventory(topInventory);

        return section != null && didTryToPlaceIn(event, section);

    }




    public static boolean didTryToPlaceIn(InventoryClickEvent event, Inventory restrictedInventory)
    {
        InventoryAction action = event.getAction();
        String name = action.name();

        Inventory clickedInventory = event.getClickedInventory();
        Inventory playerInventory = event.getWhoClicked().getInventory();
        Inventory topInventory = event.getInventory();



        if (event.isShiftClick() && (topInventory.equals(restrictedInventory)||playerInventory.equals(restrictedInventory))) {

            return true;
        }
        /*
        if the clicked inventory equals the player's inventory and the
        top inventory is not the shop inventory
         */



        if (!(clickedInventory.equals(restrictedInventory)) && !(topInventory.equals(restrictedInventory))) {

            return false;
        }

        if (!clickedInventory.equals(restrictedInventory) &&
                !(name.contains("MOVE") || name.contains("SWAP") || name.contains("COLLECT"))) {

            return false;
        }

        //Could still attempt to place out
        return name.contains("HOTBAR") || name.contains("PLACE") || name.contains("MOVE") ||
                name.contains("COLLECT") || name.contains("SWAP");
    }

    public static boolean didTryToDragIn(InventoryDragEvent event, Inventory restrictedInventory)
    {
        Inventory eventInv = event.getInventory();
        return eventInv.equals(restrictedInventory);
    }





    /*
    Code for making sure the player doesn't lose their sword, or break away their tools into a chest
     */
    public static void operateRestrictions(InventoryClickEvent event, Arena arena){
       // System.out.println("[DEBUG] operate click restrict");

        BattlePlayer clicked = arena.getPlayers().getOrDefault(event.getWhoClicked().getUniqueId(),null);

        if (clicked == null)
            return;

        Inventory topInv = event.getInventory();
        Inventory clickedInv = event.getClickedInventory();
        Inventory playerInv = clicked.getRawPlayer().getInventory();


        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (topInv instanceof CraftInventoryCrafting)
        {
            //if they clicked on the crafting inventory.
            if (clickedInv.equals(topInv)) {
                event.setCancelled(true);
                return;
            }
        }

        //we now know they clicked on a chest inv (top inv is chest inv) or their own inv

        CLICKED_IN_CHEST: {
            //if they clicked in the chest
            if (!topInv.equals(clickedInv))
            {
                break CLICKED_IN_CHEST;
            }
                //if they tried to put something into chest

            //if they didn't try to place in the chest, then break
            if (!didTryToPlaceIn(event, clickedInv))
                break CLICKED_IN_CHEST;



            SWAP:
            {

                if (!event.getAction().name().contains("HOTBAR")) {
                    break SWAP;
                }

                    int hotBarButton = event.getHotbarButton();

                    int raw = event.getRawSlot();
                     ItemStack swapped = null;

                     if (hotBarButton != -1) {
                         swapped = playerInv.getItem(hotBarButton);
                     }

                     if (swapped == null)
                         break SWAP;

                     boolean restrict = (!ItemHelper.isItemInvalid(swapped)) && ItemHelper.isInventoryPlaceRestrict(swapped);
                     if (!restrict) {

                         break SWAP;
                     }

             //   System.out.println("restricted");


                if (topInv instanceof CraftInventoryCrafting) {
                    if (raw < InventoryProperty.SMALL_CRAFTING_GRID.getValue()) {
                    //    System.out.println("raw less than 4");
                        event.setCancelled(true);
                        break SWAP;
                    }
                }

                if (topInv instanceof CraftInventoryDoubleChest) {
                    if (raw < InventoryProperty.SHOP_SIZE.getValue()) {
                        event.setCancelled(true);
                        break SWAP;
                    }
                }

                if (raw < InventoryProperty.SMALL_SHOP_SIZE.getValue()) {
                    event.setCancelled(true);
                }

            }

                    //if it's restricted, then cancel the event. (Note that may be air)
                    if (ItemHelper.isInventoryPlaceRestrict(cursorItem) || ItemHelper.isInventoryPlaceRestrict(clickedItem))
                    {
                        event.setCancelled(true);
                        return;
                    }
                    else
                    {
                        //find out if they placed a sword in the inv. (item is not restricted)
                        int swordsLeft = ItemHelper.countSwords(playerInv);
                        if (swordsLeft!=1) {
                            operateSwordCount(clicked);
                            return;
                        }
                    }
        }


        //if they clicked something in their own inv
        if (clickedInv.equals(playerInv))
        {
            if (ItemHelper.isSword(cursorItem))
                return;

            if (ItemHelper.countSwords(playerInv) != 1)
                operateSwordCount(clicked);

                /*

                If the cursor item has an item, then it will be transferred to the slot.
                If the slot has an item, then check if it is a restricted item. (ONLY if the slot is not in the player inv)

               if  restricted, then cancel the event.

               if not restricted, then check the swords.

                 */
        }
    }



    public static void operateSwordCount(BattlePlayer clicked)
    {
        Inventory playerInv = clicked.getRawPlayer().getInventory();

        int amount = ItemHelper.getPresent(Material.WOOD_SWORD,playerInv);
        if (amount != 1) {
            ItemStack shopItem = ItemHelper.toSoldItem(ShopItem.WOODEN_SWORD,clicked);
            ItemHelper.clearAll(shopItem,playerInv);

            int swords = ItemHelper.countSwords(playerInv);
            if (swords == 0) {
                clicked.getBarManager().set(shopItem, ShopItem.WOODEN_SWORD, clicked.getRawPlayer());
            }
        }

    }



    public static void operateRestrictions(InventoryDragEvent event, Arena arena){

        BattlePlayer clicked = arena.getPlayers().getOrDefault(event.getWhoClicked().getUniqueId(),null);

        if (clicked == null)
            return;


        ItemStack dragged = event.getOldCursor();
        Inventory topInv = event.getView().getTopInventory();
        Set<Integer> slots = event.getRawSlots();
        int lowest = getLowestSlot(slots);

        boolean isRestricted = ItemHelper.isInventoryPlaceRestrict(dragged);

        //so this should be their 2x2 default crafting grid
        if (topInv instanceof CraftInventoryCrafting) {
            if (lowest < InventoryProperty.SMALL_CRAFTING_GRID.getValue()) {
                event.setCancelled(true);
            }
           return;
        }

        //it's just the regular old CraftInv
        if (topInv instanceof CraftInventoryDoubleChest && isRestricted) {
            if (lowest < InventoryProperty.SHOP_SIZE.getValue()) {
                event.setCancelled(true);
                return;
            }
        }

        if (lowest < InventoryProperty.SMALL_SHOP_SIZE.getValue()) {
            event.setCancelled(true);
        }


        /*
        the ints are the max values of certain inventory slots. By validating that the lowest slot
        is less than that, we make sure it is in the player's inventory. (Not the crafting grid, or a chest)
         */



    }

    private static int getLowestSlot(Set<Integer> slots ){
        int lowest = -1;

        for (int slot: slots) {
            if (lowest == -1) {
                lowest = slot;
            }
            else {
                if (slot < lowest)
                    lowest = slot;
            }
        }
        return lowest;
    }





    public static void doTeamBuy(InventoryClickEvent event, Arena arena) {

        ConcurrentHashMap<UUID, BattlePlayer> registeredPlayers = arena.getPlayers();
        UUID id = event.getWhoClicked().getUniqueId();
        if (!registeredPlayers.containsKey(id))
            return;

        BattlePlayer clicked = registeredPlayers.get(id);
        BattleTeam team = clicked.getTeam();
        TeamBuyInventory teamInventory = team.getTeamInventory();

        if (event.getClickedInventory().equals(teamInventory)||didTryToPlaceIn(event,teamInventory)) {
            event.setCancelled(true);
        }

        int slot = event.getRawSlot();
        TeamInventoryConfig configItem = null;

        for (TeamInventoryConfig current: config)
        {
            for (int index: current.getSlots())
                if (slot == index)
                {
                    configItem = current;
                    break;
                }
        }

        if (configItem == null)
            return;


        event.setCancelled(true);

        TeamItem teamItem = configItem.getItems();
        if (teamItem.name().contains("SLOT"))
            return;

        int cost;
        if (teamItem.isRenewable()) {
            cost = team.countTraps()+1;

            if (team.countTraps() >= team.getMaxTrapNumber()) {
                clicked.sendMessage(ChatColor.RED+"Your trap slots are full!");
                return;
            }
        }
        else
        {
            int index = team.getUpgradeIndex(teamItem);

            if (index == teamItem.getCost().length + 1)
            {
              clicked.sendMessage(ChatColor.RED+"You have the max upgrades for this category!");
              return;
            }
            cost = index == -1 ? teamItem.getCost()[0] : teamItem.getCost()[index-1];
        }

              //check for current traps and upgrade limits here.


        boolean paid = ItemHelper.didPay(clicked,teamItem.getCostMat(),cost);

        if (paid)
        {

            team.sendTeamMessage(ChatColor.GREEN+clicked.getRawPlayer().getName()+" bought "+teamItem.format());

            if (teamItem.isRenewable())
            {

                Trap trap = null;
                switch (teamItem) {
                    case TRAP_ALARM:
                        trap = new AlarmTrap(team,team.getTrapArea());
                        break;

                    case TRAP_MINER_SLOW:
                        trap = new MiningTrap(team, team.getTrapArea());
                        break;

                    case TRAP_OFFENSIVE:
                        trap = new OffensiveTrap(team, team.getTrapArea());
                        break;

                    case TRAP_SIMPLE:
                        trap = new SimpleTrap(team,team.getTrapArea());
                        break;
                }

               if (trap == null)
                   throw new IllegalArgumentException("Trap should not be null! Given team item: "+teamItem);

               team.addTrap(trap);
               team.updateTrapDisplay();

                return;
            }


            // Only updates the hashmap for level keeping
           boolean upgraded = team.updateUpgradeTeamModifier(teamItem);

           if (!upgraded)
           {
               clicked.sendMessage(ChatColor.RED+"[ERROR] Could not upgrade team modifier. (This should not be)");
               return;
           }

           //if it's not a trap, then we should make do to update modifiers. (saves resources)
            team.applyPlayerTeamModifiers();
           //updates everything in the inv.
           team.updateModifierDisplay(configItem);

           //do the modifiers here. Only these should be here, since an update to the applications will
            //refresh everything including this. (We don't want 9999 dragons spawning, etc)
            //These upgrades only have a limited amount they can be upgraded, so no if statement
            //needed here.
           switch (teamItem) {

               case BUFF_DRAGONS: //
                   team.setDragonSpawnNumber(team.getDragonSpawnNumber()+1);
                   break;

               case UPGRADE_FORGE: //
                  Forge forge = team.getForge();
                  forge.setTier(forge.getTier()+1);
                   break;

               case BUFF_BASE_REGEN:
                   team.loadAura();
                   break;
           }
        }
    }


    public static void operateHotBarClick(InventoryClickEvent event, Arena arena)
    {
        BattlePlayer clicked = arena.getPlayers().getOrDefault(event.getWhoClicked().getUniqueId(),null);
        if (clicked == null)
            return;

        HotbarManager manager = clicked.getBarManager();
        Inventory clickedInv = event.getClickedInventory();

        int slot = event.getSlot();

        if (!manager.equals(clickedInv)) {
            event.setCancelled(true);
            return;
        }

        ItemStack residing = clickedInv.getItem(slot);
        ItemStack cursor = event.getCursor();



        //if it was placed into the area
            boolean placeResult = operateBarItemPlace(clickedInv, slot);
            if (placeResult) {
                manager.updateLayout(slot, cursor);
                return;
            }

        //if the residing item is not invalid (the clicked item)
        //if the item is valid for editing, put it onto the cursor and set the item back in there.
        //if it is a nav item, then do the actions instead then.
        if (!ItemHelper.isItemInvalid(residing)) {

            boolean result = operateBarItemTake(clickedInv,slot, arena.getPlugin());
          if (!result)
            {
                event.setCancelled(true);
                BattlePlayer player = arena.getPlayers().getOrDefault(event.getWhoClicked().getUniqueId(),null);
                if (player == null)
                    return;

                //this is for the reset and return functions
                //better way to do this???
                switch (slot) {

                    //return
                    case 3:
                        player.getRawPlayer().openInventory(player.getShopManager().getQuickBuy());
                        break;


                        //reset
                    case 5:
                        player.getBarManager().reset();
                        break;
                }
            }

        }

    }

    /*
    clicked: the inv clicked
    placed: the item placed into the inv
    return: if the action is valid
     */
    private static boolean operateBarItemPlace(Inventory clicked, int slot){
       if (slot >= InventoryProperty.LARGE_ROW_FIVE_START.getValue() &&
               slot <= InventoryProperty.LARGE_ROW_FIVE_END.getValue()){

           //make way for the cursor
           clicked.setItem(slot, null);
           return true;
       }
       return false;
    }

    private static boolean operateBarItemTake(Inventory clicked, int slot, Plugin plugin){
        if (slot >= InventoryProperty.LARGE_ROW_THREE_START.getValue() &&
                slot <= InventoryProperty.LARGE_ROW_THREE_END.getValue()) {

            //not sure if this will work. Needs to be invoked after the event, so maybe use bukkitrunnable instead?

            final ItemStack replace = clicked.getItem(slot).clone();

            new BukkitRunnable() {
                @Override
                public void run() {
                    clicked.setItem(slot, replace);
                    cancel();
                }
            }.runTaskLater(plugin, 1);
            return true;

        }
        return false;
    }




    public static void operateHotBarDrag(InventoryDragEvent event, Arena arena)
    {

    }



    /*
    replace: the itemstack which will replace an existing item in the inventory
    We can assume that the player is the player that invoked the event
     */
    public static void operateInventoryEdit(InventoryClickEvent event, Arena arena){

        BattlePlayer player = arena.getPlayers().getOrDefault(event.getWhoClicked().getUniqueId(),null);
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        QuickBuyEditor editor = player.getQuickEditor();
        Inventory clicked = event.getClickedInventory();


        Inventory display = editor.getEditor();

        //if it does not equal the display
        if (!clicked.equals(display)) {
            event.setCancelled(true);
            return;
        }

        //also need to account for dragging
        if (didTryToPlaceIn(event, display)) {
            event.setCancelled(true);
        }

        int slot = event.getSlot();
        if (slot >= InventoryProperty.HOT_BAR_START.getValue() && slot <= InventoryProperty.HOT_BAR_END.getValue()) {
            editor.applyConfigChange(slot);
            player.getShopManager().getQuickBuy().setItem(slot, editor.getCurrentAdding());
            player.getRawPlayer().openInventory(player.getShopManager().getQuickBuy());
        }
    }




    /*
    @Author CAMM
    Takes an inventory click event and determines whether action on the quick buy inventory should be
    executed to sell an item, etc.

    At this point, the inventory referenced should be guaranteed a shop inventory.
     */
    public static void doQuickBuy(InventoryClickEvent event, Arena arena, boolean isInflated)
    {
        ConcurrentHashMap<UUID,BattlePlayer> registeredPlayers = arena.getPlayers();
        Inventory clickedInv = event.getClickedInventory();
        HumanEntity player = event.getWhoClicked();


        if (!registeredPlayers.containsKey(player.getUniqueId()))
            return;

        BattlePlayer currentPlayer = registeredPlayers.get(player.getUniqueId());

        if (clickedInv==null||clickedInv.getTitle()==null)
            return;

        //they can still buy items with an item in the cursor, just not put it in.
        if (didTryToPlaceIn(event, currentPlayer))
            event.setCancelled(true);

        ItemStack item = event.getCurrentItem();

        if (item==null||item.getItemMeta()==null)
            return;

        PlayerInventoryManager manager = currentPlayer.getShopManager();

        if (manager==null)
            return;

        event.setCancelled(true);

        try
        {
            ShopItem clickedItem = null;
            String name = item.getItemMeta().getDisplayName();
            if (name == null)
                return;


            for (ShopItem current: gameItems)
            {
                if ((current.name).equalsIgnoreCase(name) || name.equalsIgnoreCase(current.name))
                {
                    clickedItem = current;
                    break;
                }
            }

            if (clickedItem==null) {
                return;
            }

            ItemCategory category = clickedItem.category;

            if (ItemHelper.isPlaceHolder(category))
            {
                navigate(clickedItem, currentPlayer);
                return;
            }



            //accounts for team enchants.
            //This section is unfinished. We need to account for if the player shift-clicks the item.
            //(We remove it from quick buy in this case)
            QuickBuySection quickBuy = manager.getQuickBuy();

            if (quickBuy.getInventory().equals(clickedInv))
            {
                //if it is the quickbuy inventory, we have the option of replacing items.
                if (event.isShiftClick())
                    quickBuy.setItem(event.getRawSlot(), ShopItem.EMPTY_SLOT);
                else
                    ItemHelper.sellItem(clickedItem, currentPlayer, isInflated);
            }
            else if (event.isShiftClick()) {
                currentPlayer.getQuickEditor().setCurrentAdding(item);
                currentPlayer.getQuickEditor().display();
            }
            else
                ItemHelper.sellItem(clickedItem, currentPlayer, isInflated);



        }
        catch (IllegalArgumentException | NullPointerException e)
        {
            e.printStackTrace();
        }

    }



    /*
   @Author CAMM
   Unfinished. Still need to do the hotbar manager section.


   Takes an Inventory item, and a battle player.
   If the item is a navigation item, brings the player to a different inventory interface.
    */
    private static void navigate(ShopItem item, BattlePlayer player)
    {
        Player rawPlayer = player.getRawPlayer();
        PlayerInventoryManager manager = player.getShopManager();
        switch (item)
        {
            case ARMOR_NAV:
                rawPlayer.openInventory(manager.getArmorSection());
                break;

            case BLOCKS_NAV:
                rawPlayer.openInventory(manager.getBlockSection());
                break;

            case HOME_NAV:
                rawPlayer.openInventory(manager.getQuickBuy());
                break;

            case MELEE_NAV:
                rawPlayer.openInventory(manager.getMeleeSection());
                break;

            case TOOLS_NAV:
                rawPlayer.openInventory(manager.getToolsSection());
                break;

            case HOTBAR_NAV:
                player.getBarManager().display(rawPlayer);
                break;

            case TRACKER_NAV:
                player.sendMessage("[DEBUG] nav to tracker (**NOT IMPLEMENTED YET)");
                break;

            case RANGED_NAV:
                rawPlayer.openInventory(manager.getRangedSection());
                break;

            case POTIONS_NAV:
                rawPlayer.openInventory(manager.getPotionSection());
                break;

            case UTILITY_NAV:
                rawPlayer.openInventory(manager.getUtilitySection());
        }
    }
}
