package me.camm.productions.bedwars.Arena.GameRunning;

import me.camm.productions.bedwars.Arena.GameRunning.Events.ActionEvent;
import me.camm.productions.bedwars.Arena.GameRunning.Events.GameEndAction;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.IPlayerUtil;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.PlayerBoard;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.PacketHandler;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Generators.Generator;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.TeamJoinInventory;
import me.camm.productions.bedwars.Listeners.*;
import me.camm.productions.bedwars.Arena.GameRunning.Events.EventTime;
import me.camm.productions.bedwars.Util.Helpers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader.TIME;
import static me.camm.productions.bedwars.Arena.GameRunning.Events.EventTime.*;

public class GameRunner implements Listener, IArenaChatHelper, IArenaWorldHelper, IPlayerUtil
{
    private final Plugin plugin;
    private final Arena arena;
    private ArrayList<Generator> generators;

    private final int totalGameTime;
    private final int runnableFraction;


    private String playerHeader;

    static HashMap<String, InventoryName> titles = new HashMap<>();

    private volatile int headerTime;
    private int currentGameTime;
    private double generatorSpinTime;
    private volatile boolean isRunning;
    private final Inventory joinInventory;

    private final ConcurrentHashMap<UUID, BattlePlayer> registeredPlayers;
   // private ConcurrentHashMap<String, GameItem> items;

    //private HashMap<String, BattleTeam> teams;
    private PacketHandler packetHandler;
    private final ArrayList<ShopKeeper> keepers;

    private boolean isInflated;


    private BlockInteractListener blockListener;
    private ItemInteractListener itemListener;
    private ExplosionHandler explosionListener;
    private DroppedItemListener droppedListener;
    private EntityActionListener.NPCDisplayManager npcManager;
    private MobSpawnListener mobSpawnListener;
    private EntityActionListener damageListener;
    private LogListener playerLogListener;


    private  final ArrayList<ActionEvent> eventList;
    private int nextActivationTime;


    public GameRunner(Plugin plugin, Arena arena)
    {
        super();

        eventList = EventBuilder.build(this);


        InventoryName[] names = InventoryName.values();
        for (InventoryName name: names)
            titles.put(name.getTitle(),name);

        this.joinInventory = new TeamJoinInventory(arena).getInventory();
        this.registeredPlayers = new ConcurrentHashMap<>();
        this.keepers = new ArrayList<>();

      this.plugin = plugin;
      this.arena = arena;
      this.isInflated = false;


    this.runnableFraction = EventTime.RUNNABLE_PERIOD.getTime()/TICKS.getTime();

    this.currentGameTime = 0;
    this.generatorSpinTime = 0;
    this.totalGameTime = TOTAL_GAME_TIME.getTime();


    this.playerHeader = null;
    int index = 0;
    while (playerHeader == null && index < eventList.size())
    {
        playerHeader = eventList.get(index).getHeader();
        index ++;
    }


    headerTime = eventList.get(0).getTime();
    nextActivationTime = eventList.get(0).getActivationTime();

     isRunning = false;


    generators = new ArrayList<>();
     generators = arena.getGenerators();



    }//constructor.



    //Initialization and starting the game (Helper methods)
    /////////////////////////////////////////////////

    /*
    @Author CAMM
    This method prepares the game to start by performing post-constructor operations before
    the game actually starts.
     */
    public void prepareAndStart()
    {
        setIsRunning(true);

        int maxPlayers = 0;

        for (BattleTeam team: arena.getTeamList()) {
            maxPlayers = Math.max(maxPlayers, team.getPlayers().size());
            team.initializeNPCs();
            team.showNPCs();
            keepers.add(team.getTeamQuickBuy());
            keepers.add(team.getTeamGroupBuy());
        }
        this.packetHandler = new PacketHandler(keepers, arena);

        if (maxPlayers>2)
            isInflated = true;

        for (Generator generator: generators) {
            generator.spawnIntoWorld();
            generator.setPlayerNumber(maxPlayers);
        }

        for (BattleTeam team: arena.getTeams().values())  //looping through the teams
        {
            team.startForge();
            //if there are no players on there, then eliminate the team.
            if (team.getRemainingPlayers()==0) {
                team.eliminate();
            }
        }
        RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());

// InventoryClickEvent.getHandlerList().unregister(teamJoiner);


        //Initiating the listeners


        droppedListener = new DroppedItemListener(plugin, arena);
        plugin.getServer().getPluginManager().registerEvents(droppedListener,plugin);

        mobSpawnListener = new MobSpawnListener();
        plugin.getServer().getPluginManager().registerEvents(mobSpawnListener,plugin);

        playerLogListener = new LogListener(arena,this,keepers);
        plugin.getServer().getPluginManager().registerEvents(playerLogListener,plugin);




        damageListener = new EntityActionListener(arena,plugin,this);
        plugin.getServer().getPluginManager().registerEvents(damageListener,plugin);

        blockListener = new BlockInteractListener(plugin, arena);
        plugin.getServer().getPluginManager().registerEvents(blockListener,plugin);

        explosionListener = new ExplosionHandler(plugin, arena);
        plugin.getServer().getPluginManager().registerEvents(explosionListener,plugin);

        itemListener = new ItemInteractListener(plugin,arena,packetHandler,damageListener);
        plugin.getServer().getPluginManager().registerEvents(itemListener,plugin);


        Collection<BattlePlayer> players = registeredPlayers.values();
        for (BattlePlayer player: players)
        {
            player.instantiateConfig(isInflated);
            if (!packetHandler.contains(player.getRawPlayer()))
                packetHandler.addPlayer(player.getRawPlayer());
        }

        npcManager = new EntityActionListener.NPCDisplayManager(plugin,arena,keepers,getPacketHandler());
        Thread thread = new Thread(npcManager);
        thread.start();

        start();
        //register all the events here and the forges, generators, etc here

    }


    /*
    @Author CAMM
    This method starts and runs the game for the game's duration, or until a team wins.
     */
    private void start()
    {

     //   PacketPlayOutEntityEquipment

        sendMessage(ChatColor.AQUA+"[BEDWARS] The game is starting!",plugin);

        for (BattleTeam team: arena.getTeams().values())
            team.readyPlayers();

        new BukkitRunnable()
        {
            int fraction = 0;

            public void run()
            {
                if (!isRunning) {
                    cancel();
                    return;
                }
                generatorSpinTime += 0.1;
                for (Generator generator: generators)
                {
                    generator.setTimeCount(generator.getTimeCount()+1);
                    generator.setRotation(generatorSpinTime);

                    //The time count is a count in a fraction of a second. (n/1 second).
                    //We use this time to see if we should update the timer title armorstand on the generator,
                    //and see if we should spawn the product.
                    if (!(generator.getTimeCount()>=runnableFraction))
                        continue;

                    if (generator.updateSpawnTime()) //if the generator should spawn an item
                        generator.spawnItem();

                    generator.setTimeCount(0);
                    generator.setTimeTitle(generator.getNextSpawnTime());
                }//for

                // We increase the fraction. (Fraction of n/1 second) When it is equal to the whole, we increase the
                //player timer. (The fraction is for the generators since they don't move once per second.)
                fraction++;
               // System.out.println("[DEBUG]: fraction:"+fraction);
                if (fraction>=runnableFraction)
                {
                   // System.out.println("[DEBUG]: currentgameTime:"+currentGameTime);
                    fraction = 0;
                    advancePlayerTimer();  //advancing the player scoreboard
                }
            }
        }.runTaskTimer(plugin,0,TICKS.getTime());  //1 second  = 20 ticks
    }


    //Event Handlers
    ///////////////////////////////////////////





    /*
    @Author CAMM
    This method handles the case of a player clicking on one of the inventories present in the game.

    TODO
    - you need to also make sure that we return if the inventory clicked is a team chest inventory.

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
        if (InventoryOperationHelper.didTryToDragIn(event, section))
            event.setCancelled(true);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)  //for joining teams / other things
    {

        if (event.getClickedInventory()==null||event.getClickedInventory().getTitle()==null)
            return;

        String title = event.getClickedInventory().getTitle();


   //If the bottom inventory is not a shop inv.
        if (!titles.containsKey(title))
        {
            HumanEntity player = event.getWhoClicked();


            if (!player.getInventory().equals(event.getClickedInventory()))
                return;

            if (player.getEnderChest().equals(event.getClickedInventory()))
                return;

            //If the player has clicked their own inv or their enderchest inv.
            /*
         TODO
    - you need to also make sure that we return if the inventory clicked is a team chest inventory.
             */

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
                event.setCurrentItem(stack);
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
                return;
            }

            if (!registeredPlayers.containsKey(player.getUniqueId()))
                return;

            BattlePlayer battlePlayer = registeredPlayers.get(player.getUniqueId());

            //if it is a top inv
            Inventory topInventory = event.getInventory();
            Inventory sectionInv = battlePlayer.getShopManager().isSectionInventory(topInventory);
            if (sectionInv == null)
                return;

            if (InventoryOperationHelper.didTryToPlaceIn(event,sectionInv)) {
                event.setCancelled(true);
                return;
            }


        }

        InventoryName inventoryName = titles.get(title);
        if (inventoryName == null)
            return;

        switch (inventoryName)
        {
            case TEAM_JOIN:
                addPlayerToTeam(event);
                break;

            case TEAM_BUY:
                break;

                //TODO add the option for the team buy
            default:
                InventoryOperationHelper.doQuickBuy(event,arena,isInflated);
                //do the rest of the invs here.
        }

        //maybe use a switch statement here for the titles

    }//method

    //Helper methods
    //////////////////////////////////////////////




    /*
    @Author CAMM
    Advances the player's timer on their scoreboards, and checks for if a game
    event should be triggered.
    This method also refreshes the player's scoreboards.
     */
    private synchronized void advancePlayerTimer()
    {
        if (this.currentGameTime<totalGameTime)
        {
            currentGameTime++;
           checkForEvents(currentGameTime);

            for (BattlePlayer player: registeredPlayers.values())
            {
                PlayerBoard board = player.getBoard();
                if (board!=null) {
                    board.setScoreName(TIME.getPhrase(), getTimeFormatted());
                    board.switchPrimaryBuffer();
                    player.updatePlayerStatistics();
                    player.sendHealthUpdatePackets();
                }
            }
        }
    }

    /*
@Author CAMM
Initializes the time amount on the player scoreboards to a number, instead of being blank.
E.g) "Diamond II in 1:00" instead of "Diamond II in"
*/
    public void initializeTimeBoardHead(BattlePlayer player)
    {
        player.getBoard().setScoreName(TIME.getPhrase(), getTimeFormatted());
    }


/*
@Author CAMM
@Return A string displaying the header and time for the players. E.g "Diamond II in 1:00"
This method gets the time and header to be displayed on the player's scoreboards
as a string.
 */
    private synchronized String getTimeFormatted()
    {
        //Current event time is the time period before the next event.
        //The current event time should always be greater or equal to the current game time.
        int remainingTime = headerTime-currentGameTime;


        //If the time remainder is less than 10, then add a 0 to the start.
        String adder = remainingTime% TIME_IN_MINUTE.getTime()<10 ? "0": "";


        //Formatting the time for the player scoreboard.
        String time = (remainingTime/TIME_IN_MINUTE.getTime())+":"+adder+(remainingTime%TIME_IN_MINUTE.getTime());
        return playerHeader+time;
    }



   /*
   @Author CAMM
   @Param time: The value to check against when determining if an event should be executed. Positive int.
   This method checks for if an event should be executed by comparing a given time with the current
   event to launch.
    */
    private synchronized void checkForEvents(int time) {

        //if the thing is empty, then end the game
        if (eventList.size() == 0)
        {
            ActionEvent ending = new ActionEvent(0, new GameEndAction(this));
            ending.activateEvent();
        }

            ActionEvent nextEvent = eventList.get(0);

            if (time >= nextActivationTime)
            {
               nextEvent.activateEvent();

               if (eventList.size()>1) {
                   eventList.remove(0);
               }
               else
                   return;

               this.playerHeader = null;

                nextActivationTime = eventList.get(0).getActivationTime();

               int index = 0;
               while (playerHeader == null && index < eventList.size())
               {
                   playerHeader = eventList.get(index).getHeader();
                   headerTime = eventList.get(index).getTime();
                   index ++;
               }

               RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());
            }
    }

    /*
    @Author CAMM
    @Param candidate: If the candidate is null, invokes a tie sequence. If not, the given team is the
    winner.
    Ends the game, with a different outcome depending on the candidate given.
     */
    public synchronized void endGame(BattleTeam candidate)
    {
        Collection<BattleTeam> teams = arena.getTeams().values();

        //unregister listeners here.
       // BlockBreakEvent.getHandlerList().unregister(blockListener);
        //...

        setIsRunning(false);
        for (BattleTeam all : teams)
            all.getForge().disableForge();
        npcManager.setRunning(false);

        if (candidate!=null) {
            sendMessage(ChatColor.GOLD + "All other teams have been eliminated!",plugin);
            sendMessage(candidate.getColor().getChatColor() + candidate.getCapitalizedColor() + " team has won the game!",plugin);
        }
        else
        {
            sendMessage(ChatColor.YELLOW+"Tie detected between the following teams:",plugin);
            for (BattleTeam team : teams) {
                if (!team.isEliminated())
                    sendMessage(team.getColor().getChatColor()+"- "+team.getCapitalizedColor(),plugin);
            }
        }

        //Revealing all possible hidden players to other players.
        for (BattlePlayer player: arena.getPlayers().values())
        {
            for (Player possiblyHidden: Bukkit.getOnlinePlayers())
                player.getRawPlayer().showPlayer(possiblyHidden);
        }


        PlayerLoginEvent.getHandlerList().unregister(playerLogListener);
        PlayerQuitEvent.getHandlerList().unregister(playerLogListener);

        BlockBreakEvent.getHandlerList().unregister(blockListener);
        BlockPlaceEvent.getHandlerList().unregister(blockListener);
        BlockCanBuildEvent.getHandlerList().unregister(blockListener);
        BlockFromToEvent.getHandlerList().unregister(blockListener);
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

        if (isRunning)
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

        if (arena.getTeams().get(name)==null||(!arena.getTeams().containsKey(name)))
        {
            player.sendMessage(ChatColor.RED+"Could not find team. There might be a problem with configuration...");
            return;
        }

        player.closeInventory();
        BattlePlayer currentPlayer;
        HumanEntity whoClicked = event.getWhoClicked();

        if (registeredPlayers.containsKey(whoClicked.getUniqueId()))  //check if the player is registered
        {
            //if the player was on a team before
            currentPlayer = registeredPlayers.get(whoClicked.getUniqueId());
            //    BattleTeam oldTeam = currentPlayer.getTeam();

            try {
                boolean isChanged = registeredPlayers.get(whoClicked.getUniqueId()).changeTeam(arena.getTeams().get(name));
                if (isChanged)
                {
                    sendMessage(ChatColor.AQUA + currentPlayer.getRawPlayer().getName() + " changed their Team to " + currentPlayer.getTeam().getColor() + "!",plugin);
                    initializeTimeBoardHead(currentPlayer);
                    RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());
                }
            }
            catch (Exception e)
            {
                player.sendMessage(ChatColor.RED+"Could not change teams.");
            }

        }
        else  // If they were not in the team before.
        {

            currentPlayer = new BattlePlayer((Player) event.getWhoClicked(), arena.getTeams().get(name), arena, arena.assignPlayerNumber());
            //Since the player board is initialized before the player joins, we get the incorrect amount of players on the team initially.

            boolean isAdded = (arena.getTeams().get(name).addPlayer(currentPlayer));

            if (isAdded)
            {
                registeredPlayers.put(currentPlayer.getUUID(), currentPlayer);
                arena.addPlayer(event.getWhoClicked().getUniqueId(), currentPlayer);
                sendMessage(ChatColor.GOLD + currentPlayer.getRawPlayer().getName() + " Joined Team " + currentPlayer.getTeam().getColor(),plugin);
                initializeTimeBoardHead(currentPlayer);
                RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());
            } else
                event.getWhoClicked().sendMessage(ChatColor.RED + "Could not join the team!");
        }

    }



    //Getter and Setters
    ///////////////////////////////////////

    public synchronized void setIsRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }


    public PacketHandler getPacketHandler()
    {
        return packetHandler;
    }

    public synchronized boolean isRunning()
    {
        return this.isRunning;
    }


    public Inventory getJoinInventory()
    {
        return joinInventory;
    }

    public Arena getArena(){
        return arena;
    }
}
