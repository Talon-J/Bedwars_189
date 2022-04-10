package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.TeamJoinInventory;
import me.camm.productions.bedwars.Util.Helpers.ChatSender;
import me.camm.productions.bedwars.Util.Helpers.InventoryOperationHelper;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import me.camm.productions.bedwars.Util.Helpers.TeamHelper;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryListener implements Listener {

    private final Arena arena;
    private final GameRunner runner;
    private final HashMap<String, InventoryName> titles;
    private final Map<UUID, BattlePlayer> registeredPlayers;
    private final Inventory joinInventory;
    private final ChatSender sender;

    public InventoryListener(GameRunner runner){
        sender = ChatSender.getInstance();
        this.arena = runner.getArena();
        this.runner = runner;
        registeredPlayers = arena.getPlayers();
        titles = new HashMap<>();
        InventoryName[] names = InventoryName.values();
        for (InventoryName name: names)
            titles.put(name.getTitle(),name);
        this.joinInventory = new TeamJoinInventory(arena).getInventory();
    }

    public Inventory getJoinInventory(){
        return joinInventory;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)  //for joining teams / other things
    {

        if (event.getClickedInventory()==null||event.getClickedInventory().getTitle()==null) {
            return;
        }

        String title = event.getClickedInventory().getTitle();


        //If the clicked inventory is not registered as a known inventory
        if (!titles.containsKey(title))
        {

            HumanEntity player = event.getWhoClicked();
            if (!registeredPlayers.containsKey(player.getUniqueId()))
                return;

            BattlePlayer battlePlayer = registeredPlayers.get(player.getUniqueId());


            //operate on restrictions to ensure that they didn't put a restricted item somewhere
            InventoryOperationHelper.operateRestrictions(event, arena);

            //if the player's inventory is not the clicked inventory, then return.
            if (!player.getInventory().equals(event.getClickedInventory()))
                return;

            //if the enderchest is the clicked inv, then return.
            if (player.getEnderChest().equals(event.getClickedInventory()))
                return;

            //If the player has clicked their own inv or their enderchest inv.

            ItemStack stack = event.getCurrentItem();
            if (ItemHelper.isItemInvalid(stack))
                return;

            //If the player has attempted to take off their armor, cancel the event.
            //So it seems that there is a glitch with players being
            //able to take it off in creative.
            //Shouldn't be an issue though, since everyone should be
            //in survival.
            if (ItemHelper.isArmor(stack.getType()))
            {
                GameMode mode = player.getGameMode();
                if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR) {
                    event.setCancelled(true);
                    return;
                }
            }


            //if it is a top inv
            Inventory topInventory = event.getInventory();

            boolean valid = false;
            if (joinInventory.equals(topInventory)){
                event.setCancelled(true);
                valid = true;
            }


            PlayerInventoryManager sectionManager = battlePlayer.getShopManager();
            if (sectionManager == null)
                return;

            Inventory sectionInv = sectionManager.isSectionInventory(topInventory);

            if (sectionInv == null && !valid)
                return;

            if (InventoryOperationHelper.didTryToPlaceIn(event,sectionInv)) {
                event.setCancelled(true);
                return;
            }


        }

        InventoryName inventoryName = titles.get(title);
        if (inventoryName == null)
            return;

        if (inventoryName == InventoryName.TEAM_JOIN) {
                addPlayerToTeam(event);
        }
        else {
            switch (inventoryName) {

                case TEAM_BUY:
                    InventoryOperationHelper.doTeamBuy(event, arena);
                    break;

                case EDIT_QUICKBUY:
                    InventoryOperationHelper.operateInventoryEdit(event, arena);
                    break;

                case HOTBAR_MANAGER:
                    InventoryOperationHelper.operateHotBarClick(event, arena);
                    break;

                case TRACKER:
                    break;

                default:
                    InventoryOperationHelper.doQuickBuy(event, arena, runner.isInflated());
                    //do the rest of the invs here.
            }
        }

        //maybe use a switch statement here for the titles

    }//method




        /*
    @Author CAMM
    This method handles the case of a player clicking on one of the inventories present in the game.
     */


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event)
    {
        if (InventoryOperationHelper.didTryToDragIn(event, joinInventory)) {
            event.setCancelled(true);
        }

        HumanEntity entity = event.getWhoClicked();
        if (!registeredPlayers.containsKey(entity.getUniqueId()))
            return;

        BattlePlayer player = registeredPlayers.get(entity.getUniqueId());
        Inventory inv = event.getInventory();

        Inventory section = player.getShopManager().isSectionInventory(inv);

        if (InventoryOperationHelper.didTryToDragIn(event, section)) {
            event.setCancelled(true);
            return;
        }

        if (InventoryOperationHelper.didTryToDragIn(event, player.getTeam().getTeamInventory())) {
            event.setCancelled(true);
            return;
        }

        if (InventoryOperationHelper.didTryToDragIn(event, player.getQuickEditor().getEditor())) {
            event.setCancelled(true);
            return;
        }

        InventoryOperationHelper.operateRestrictions(event,arena);

        if (player.getBarManager().invEquals(event.getView().getTopInventory()))
            InventoryOperationHelper.operateHotBarDrag(event, arena);

    }



    /*
    @Author CAMM
    Adds a player to a team, or changes their team if they are already on one.
     */
    private void addPlayerToTeam(InventoryClickEvent event)
    {
        Inventory inv = event.getClickedInventory();
        HumanEntity player = event.getWhoClicked();

        if (InventoryOperationHelper.didTryToPlaceIn(event,joinInventory))
            event.setCancelled(true);

        if (!inv.equals(joinInventory)||ItemHelper.isItemInvalid(event.getCurrentItem()))
            return;

        if (runner.isRunning())
        {
            player.sendMessage(ChatColor.YELLOW+"Wait for the current game to finish!");
            player.closeInventory();
            return;
        }

        if (event.getCurrentItem().getType() != Material.WOOL)
            return;

        ItemStack stack = event.getCurrentItem();

        String name = stack.getItemMeta().getDisplayName();
        event.setCancelled(true);

        HashMap<String, BattleTeam> arenaRegistered = arena.getTeams();
        BattleTeam picked = arenaRegistered.getOrDefault(name, null);
        player.closeInventory();
        if (picked == null)
        {
            player.sendMessage(ChatColor.RED+"Could not find team. There might be a problem with configuration...");
            return;
        }


        BattlePlayer currentPlayer;
        HumanEntity whoClicked = event.getWhoClicked();

        if (registeredPlayers.containsKey(whoClicked.getUniqueId()))  //check if the player is registered
        {

            currentPlayer = registeredPlayers.get(whoClicked.getUniqueId());

            try {
                //this may throw an exception
                boolean isChanged = registeredPlayers.get(whoClicked.getUniqueId()).changeTeam(arena.getTeams().get(name));


                if (isChanged)
                {
                    sender.sendMessage(currentPlayer.getRawPlayer().getName() + " changed their Team to " + currentPlayer.getTeam().getTeamColor() + "!");
                    runner.initializeTimeBoardHead(currentPlayer);
                    TeamHelper.updateTeamBoardStatus(registeredPlayers.values());
                }


            }
            catch (RuntimeException e)
            {
                player.sendMessage(ChatColor.RED+"Could not change teams!");
            }

        }
        else  // If they were not in the team before.
        {
            BattleTeam team = arena.getTeams().get(name);
            currentPlayer = new BattlePlayer((Player) event.getWhoClicked(), team, arena, arena.assignPlayerNumber());
            //Since the player board is initialized before the player joins, we get the incorrect amount of players on the team initially.

            boolean isAdded = team.addPlayer(currentPlayer);

            if (isAdded)
            {
             //   registeredPlayers.put(currentPlayer.getUUID(), currentPlayer);
                arena.addPlayer(whoClicked.getUniqueId(), currentPlayer);
                sender.sendMessage(ChatColor.GOLD + whoClicked.getName() + " Joined Team " + team.getTeamColor());
                runner.initializeTimeBoardHead(currentPlayer);
                TeamHelper.updateTeamBoardStatus(registeredPlayers.values());
            } else
                whoClicked.sendMessage(ChatColor.RED + "Could not join the team!");
        }

    }

}
