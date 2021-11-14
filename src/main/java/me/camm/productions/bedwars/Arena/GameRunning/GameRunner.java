package me.camm.productions.bedwars.Arena.GameRunning;

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
import me.camm.productions.bedwars.Util.GamePhase.EventTime;
import me.camm.productions.bedwars.Util.GamePhase.GameEvent;
import me.camm.productions.bedwars.Util.GamePhase.GameEventPair;
import me.camm.productions.bedwars.Util.Helpers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeaders.DIAMOND_TWO_HEADER;
import static me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeaders.TIME;
import static me.camm.productions.bedwars.Util.GamePhase.EventTime.*;

public class GameRunner implements Listener, IArenaChatHelper, IArenaWorldHelper, IPlayerUtil
{
    private final Plugin plugin;
    private final Arena arena;
    private ArrayList<Generator> generators;

    private final int totalGameTime;
    private final int runnableFraction;


    private String playerHeader;

    static HashMap<String, InventoryName> titles = new HashMap<>();

    private volatile int currentEventTime;
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


    private static final ArrayList<GameEvent> eventList;

    static {
        eventList = new ArrayList<>();
        for (GameEventPair pair: GameEventPair.values())
            eventList.add(new GameEvent(pair));
    }

    public GameRunner(Plugin plugin, Arena arena)
    {
        super();
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
    this.currentEventTime = DIAMOND_UPGRADE_TWO.getTime();
    this.currentGameTime = 0;
    this.generatorSpinTime = 0;
    this.totalGameTime = TOTAL_GAME_TIME.getTime();


    try {
       this.playerHeader = eventList.get(0).getEvent().getScoreBoardHeader();
    }
    catch (Exception e)
    {
        this.playerHeader = DIAMOND_TWO_HEADER.getPhrase();
    }

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

        blockListener = new BlockInteractListener(plugin, arena, damageListener);
        plugin.getServer().getPluginManager().registerEvents(blockListener,plugin);

        explosionListener = new ExplosionHandler(plugin, arena,damageListener);
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
                  //  checkForEvents(currentGameTime);
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
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)  //for joining teams
    {

        if (event.getClickedInventory()==null||event.getClickedInventory().getTitle()==null)
            return;


        String title = event.getClickedInventory().getTitle();
        if (!titles.containsKey(title))
        {
            HumanEntity player = event.getWhoClicked();
            if (!player.getInventory().equals(event.getClickedInventory()))
                return;
            //If the player has clicked their own inv.

            ItemStack stack = event.getCurrentItem();
            if (stack == null || stack.getItemMeta() == null)
                return;

            //If the player has attempted to take off their armor, cancel the event.
            if (ItemHelper.isArmor(stack.getType()))
                event.setCancelled(true);
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
        int remainingTime = currentEventTime-currentGameTime;


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
        if (eventList.size() <= 0)
        {
            GameEvent end = new GameEvent(GameEventPair.GAME_END_DECLARE);
            end.activate(arena,this);
            return;
        }

            int eventTime = eventList.get(0).getEvent().getTimePair().getTime();
            GameEvent currentEvent = eventList.get(0);

            if (time >= eventTime)
            {
                currentEvent.activate(arena, this);
                this.playerHeader = currentEvent.getEvent().getScoreBoardHeader();

                //So that we still have something to fall back on to display the current time for when a player re-logs.
                //This way it won't be an empty space.
                if (eventList.size()>1)
                    eventList.remove(0);


                //Getting the NEXT event time.
                if (eventList.size()>0)
                {
                    GameEventPair next = eventList.get(0).getEvent();

                    //If the event is only a message thing that doesn't affect the scoreboard, then we get the time of the NEXT NEXT
                    //event.
                    if (next.isSkip()&&eventList.size()>1)
                        currentEventTime = eventList.get(1).getEvent().getTimePair().getTime();
                    else
                        currentEventTime = next.getTimePair().getTime();
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
    }

    /*
    @Author CAMM
    Adds a player to a team, or changes their team if they are already on one.
     */
    private void addPlayerToTeam(InventoryClickEvent event)
    {
        Inventory inv = event.getClickedInventory();
        HumanEntity player = event.getWhoClicked();

        if (!inv.equals(joinInventory)||!ItemHelper.isItemInvalid(event.getCurrentItem()))
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
        if (ItemHelper.isItemInvalid(stack))
            return;

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
}
