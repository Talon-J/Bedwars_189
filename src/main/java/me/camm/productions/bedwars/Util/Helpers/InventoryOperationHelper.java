package me.camm.productions.bedwars.Util.Helpers;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.QuickBuySection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
    @Author CAMM
    Unfinished. Refactor to include section inventories.
    Takes an inventory click event and determines whether action on the quick buy inventory should be
    executed to sell an item, etc.
     */
    public static void doQuickBuy(InventoryClickEvent event, Arena arena, boolean isInflated)
    {
        ConcurrentHashMap<UUID,BattlePlayer> registeredPlayers = arena.getPlayers();

        Inventory clickedInv = event.getClickedInventory();

        HumanEntity player = event.getWhoClicked();
        if (event.getClickedInventory()==null||clickedInv.getTitle()==null) {
            player.sendMessage("[DEBUG]inv is null");
            return;
        }

        if (!registeredPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage("[DEBUG]does not contain");
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item==null||item.getItemMeta()==null) {
            player.sendMessage("[DEBUG]meta is null");
            return;
        }

        BattlePlayer currentPlayer = registeredPlayers.get(player.getUniqueId());
        PlayerInventoryManager manager = currentPlayer.getShopManager();
        if (manager==null) {
            player.sendMessage("[DEBUG]mngr is null");
            return;
        }

        if (manager.isSectionInventory(clickedInv)==null) {
            player.sendMessage("[DEBUG]inv is not a quick buy shop inventory");
            return;
        }

        event.setCancelled(true);

        try
        {
            GameItem clickedItem = null;
            String name = item.getItemMeta().getDisplayName();


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
                if (event.getClick().isShiftClick())
                {
                    quickBuy.setItem(event.getRawSlot(),GameItem.EMPTY_SLOT);
                }
                else
                    ItemHelper.sellItem(clickedItem, currentPlayer, isInflated);
            }
            else
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
                rawPlayer.openInventory(manager.getArmorSection().getInventory());
                break;

            case BLOCKS_NAV:
                rawPlayer.openInventory(manager.getBlockSection().getInventory());
                break;

            case HOME_NAV:
                rawPlayer.openInventory(manager.getQuickBuy().getInventory());
                break;

            case MELEE_NAV:
                rawPlayer.openInventory(manager.getMeleeSection().getInventory());
                break;

            case TOOLS_NAV:
                rawPlayer.openInventory(manager.getToolsSection().getInventory());
                break;

            case HOTBAR_NAV:
                player.sendMessage("[DEBUG]nav to hotbar mngr");
                break;

            case RANGED_NAV:
                rawPlayer.openInventory(manager.getRangedSection().getInventory());
                break;

            case POTIONS_NAV:
                rawPlayer.openInventory(manager.getPotionSection().getInventory());
                break;

            case UTILITY_NAV:
                rawPlayer.openInventory(manager.getUtilitySection().getInventory());
        }
    }
}
