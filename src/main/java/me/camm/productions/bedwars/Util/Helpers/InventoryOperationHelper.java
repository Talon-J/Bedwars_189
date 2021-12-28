package me.camm.productions.bedwars.Util.Helpers;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.QuickBuySection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryOperationHelper
{

    private final static GameItem[] gameItems;
    static {
        gameItems = GameItem.values();
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

        /*
        if the clicked inventory equals the player's inventory and the
        top inventory is not the shop inventory
         */
        if (clickedInventory.equals(playerInventory) && !(topInventory.equals(restrictedInventory))) {
            return false;
        }

        if (clickedInventory.equals(playerInventory) &&
                !(name.contains("MOVE") || name.contains("SWAP") || name.contains("COLLECT"))) {
            return false;
        }

        return name.contains("HOTBAR") || name.contains("PLACE")||name.contains("MOVE")||
                name.contains("COLLECT") || name.contains("SWAP");

    }

    public static boolean didTryToDragIn(InventoryDragEvent event, Inventory restrictedInventory)
    {
        return event.getInventory().equals(restrictedInventory);
    }



    /*
    @Author CAMM
    Unfinished. Refactor to include section inventories.
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
            GameItem clickedItem = null;
            String name = item.getItemMeta().getDisplayName();
            if (name == null)
                return;


            for (GameItem current: gameItems)
            {
                if ((current.name).equalsIgnoreCase(name) || name.equalsIgnoreCase(current.name))
                {
                    clickedItem = current;
                    break;
                }
            }

            if (clickedItem==null) {
                player.sendMessage("[DEBUG]-----cannot resolve item");
                return;
            }

            ItemCategory category = clickedItem.category;

            if (ItemHelper.isPlaceHolder(category))
            {
                player.sendMessage("[DEBUG]is nav item");
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
                if (event.getClick().isShiftClick())
                {
                    quickBuy.setItem(event.getRawSlot(),GameItem.EMPTY_SLOT);
                }
                else
                    ItemHelper.sellItem(clickedItem, currentPlayer, isInflated);
            }
            else
                //otherwise we only have the option of selling items.
                ItemHelper.sellItem(clickedItem, currentPlayer, isInflated);


        }
        catch (IllegalArgumentException | NullPointerException e)
        {
            e.printStackTrace();
            player.sendMessage("[DEBUG]cannot resolve item");
        }

    }



    /*
   @Author CAMM
   Unfinished. Still need to do the hotbar manager section.


   Takes an Inventory item, and a battle player.
   If the item is a navigation item, brings the player to a different inventory interface.
    */
    private static void navigate(GameItem item, BattlePlayer player)
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
                player.sendMessage("[DEBUG]nav to hotbar mngr");
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
