package me.camm.productions.bedwars.Arena.Teams;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Generators.Forge;
import me.camm.productions.bedwars.Items.ItemDatabases.BattleEnchantment;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import me.camm.productions.bedwars.Util.Locations.RegisterType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.BED_DESTROYED;
import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.LAST_LIFE_WARNING;
import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.*;


public class BattleTeam
{
    private final ConcurrentHashMap<UUID, BattlePlayer> players;

    private BattleEnchantment activeMeleeEnchant;
    private BattleEnchantment activeArmorEnchant;
    private final TeamColors teamColor;  //this is for the color of the team
    private final Arena arena;

    private final GameBoundary bed;
    private final Coordinate chest;
    private final GameBoundary box;
    private final GameBoundary aura;
    private final GameBoundary trapArea;

    private final Location quickBuy;
    private final Location teamBuy;
    private ShopKeeper teamQuickBuy;
    private ShopKeeper teamGroupBuy;

    private volatile boolean isEliminated;
    //private volatile boolean bedExists;

    private final Forge forge;
   // private final Team team;
    private boolean canStartForge;
    private final Location teamSpawn;


    private final String teamPrefix;
    private final static String teamPostfix;


    private static final byte[] bedBreakData;

    //The 0,1,2,3 are the datas of beds that can be broken (Orientation wise) without dropping the bed item.
    static {
        bedBreakData = new byte[] {0,1,2,3};
        teamPostfix = ChatColor.RESET+"";
    }



    public BattleTeam(Arena arena, TeamColors teamColor, Forge forge, Coordinate teamSpawn, GameBoundary bed, Coordinate chest, Coordinate quickBuy, Coordinate teamBuy, GameBoundary unbreakable, GameBoundary aura, GameBoundary trapArea) {

        this.teamColor  = teamColor;
        this.forge = forge;
        this.canStartForge = true;
        this.bed = bed;
        this.chest = chest;
        this.box = unbreakable;
        this.aura = aura;
        this.trapArea = trapArea;
        this.arena = arena;

        this.teamSpawn = teamSpawn.getAsLocation(arena.getWorld());
        this.teamBuy = teamBuy.getAsLocation(arena.getWorld());
        this.quickBuy = quickBuy.getAsLocation(arena.getWorld());

        this.players = new ConcurrentHashMap<>();

        this.isEliminated = false;

        this.activeArmorEnchant = null;
        this.activeMeleeEnchant = null;

        this.teamPrefix = teamColor.getChatColor()+"["+teamColor.getSymbol()+"]";

    }

    //init later so that we can get all players and set skins.
    //(Player appearance, Plugin plugin, Location loc, World world)
    public void initializeNPCs()
    {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
      Player appearanceOne = players.get((int)(Math.random()*players.size()));
      Player appearanceTwo = players.get((int)(Math.random()*players.size()));

        this.teamQuickBuy = new ShopKeeper(appearanceOne, arena.getPlugin(), quickBuy, arena.getWorld(),false, quickBuy.getYaw());
        this.teamGroupBuy = new ShopKeeper(appearanceTwo,arena.getPlugin(), teamBuy, arena.getWorld(),true, teamBuy.getYaw());
    }

    public void showNPCs()
    {
        teamQuickBuy.sendNPCToAll();
        teamGroupBuy.sendNPCToAll();
        teamQuickBuy.setRotationForAllPlayers();
        teamGroupBuy.setRotationForAllPlayers();
    }


    /*
    @Author CAMM
    Marks the current team as eliminated, and both breaks and unregisters the bed if it still exists.
    It is up to the calling method to update the player scoreboards.
     */
    public synchronized void eliminate()
    {
        if (this.isEliminated)
            return;

        this.isEliminated = true;

        bed.replace(Material.AIR,Material.BED_BLOCK,bedBreakData,arena.getWorld());
        bed.unregister(BED.getData(), arena.getWorld(), arena.getPlugin());
        arena.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "TEAM ELIMINATED >> " + ChatColor.RESET + teamColor.getChatColor() + getCapitalizedColor() + " Team" + ChatColor.RED + " has been eliminated!");
        players.values().forEach(player ->player.setEliminated(true));

    }



/*
@Author CAMM
Marks the current team as on their last lives.
It is up to the calling method to update the scoreboards of the players.
 */
    public synchronized void putOnLastStand()
    {
        if (isEliminated||!doesBedExist())
            return;

        sendTeamTitle(BED_DESTROYED.getMessage(), LAST_LIFE_WARNING.getMessage(), 10, 40,10);  //Say that their bed has been destroyed
        bed.replace(Material.AIR, Material.BED_BLOCK, bedBreakData, arena.getWorld());
        bed.unregister(BED.getData(), arena.getWorld(), arena.getPlugin());
    }


    /*
    @Author CAMM.
    Starts the forge if it hasn't started yet.
     */
    public void startForge() {
        if (canStartForge) {
            Thread forgeThread = new Thread(forge);
            forgeThread.start();
            canStartForge = false;
        }
    }


    /*
    @author CAMM
    Gets the entry to display on the scoreboard.
    E.g "R RED " <-- The team status is added on to here by external methods.
     */
    public String getDisplayScoreboardEntry()
    {
       return teamColor.getChatColor()+teamColor.getSymbol()+ChatColor.WHITE+" "+teamColor.getName()+ChatColor.GREEN;
    }


    /*
    @author CAMM
    Adds a player to a team and removes them from other teams.
     */
    public synchronized boolean addPlayer(BattlePlayer player)
    {

        if (!this.isEliminated && !players.containsKey(player.getUUID())) {
            player.getTeam().removePlayer(player.getRawPlayer());
            players.put(player.getUUID(), player);
            player.register();
            return true;
        }

        return false;

    }



    /*
    Removes the given player from the team if they are in the team.
    It is up to the calling method to update the scoreboards for the other players.
    @Author CAMM
     */
    public synchronized void removePlayer(Player player)
    {
       BattlePlayer removed = players.remove(player.getUniqueId());
       if (removed!=null)
           removed.unregister(arena.getHealthBoard());
    }

    /*
    @Author CAMM
    Teleports the given player to this team's base, if they are part of this team.
     */
    public void teleportToBase(Player player)
    {
        if (players.containsKey(player.getUniqueId()))
            player.teleport(teamSpawn);
    }


    /*
    @Author CAMM
    Readies the players on the team by setting them to survival mode and teleporting them to the team base.
     */
    public void readyPlayers()
    {
        players.forEach((uuid,player) -> player.handlePlayerFirstSpawn());
    }

    public void registerBase()
    {
        World world = arena.getWorld();
        Plugin plugin = arena.getPlugin();

        bed.register(world, BED.getData(), RegisterType.NOT_AIR.getType(),plugin);
        chest.registerBlock(world,CHEST.getData(),plugin);
        box.register(world, BASE.getData(),RegisterType.AIR_ONLY.getType(), plugin);
        aura.register(world, AURA.getData(), RegisterType.EVERYTHING.getType(),plugin);
        trapArea.register(world, TRAP.getData(),RegisterType.EVERYTHING.getType(),plugin);
    }

    public ConcurrentHashMap<UUID,BattlePlayer> getPlayers()
    {
        return players;
    }

    public boolean doesBedExist()
    {
      return bed.doesBoxContainBlock(Material.BED_BLOCK,arena.getWorld());
    }

    public synchronized int getRemainingPlayers()
    {
        int remaining = 0;
        System.out.println("[DEBUG]Team player Size: Color:"+teamColor.getName()+"  --> "+players.size());

        Collection<BattlePlayer> players = this.players.values();
        for (BattlePlayer player: players)
        {
            System.out.println("[DEBUG]Is elim: "+player.getIsEliminated());
            System.out.println("[DEBUG]Is online: "+player.getRawPlayer().isOnline());

            if (!player.getIsEliminated()&&player.getRawPlayer().isOnline())
                remaining++;
        }

        return remaining;
    }


    public void sendTeamMessage(String message)
    {
       players.forEach((uuid, battlePlayer) -> battlePlayer.sendMessage(message));
    }

    public void sendTeamTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut)
    {
        players.forEach( (uuid, battlePlayer) -> battlePlayer.sendTitle(title, subtitle, fadeIn, stay, fadeOut));
    }

    public synchronized void setMeleeEnchant(BattleEnchantment enchant)
    {
        this.activeMeleeEnchant = enchant;
    }

    public synchronized void setArmorEnchant(BattleEnchantment enchant)
    {
        this.activeArmorEnchant = enchant;
    }

    public synchronized BattleEnchantment getMeleeEnchant()
    {
        return this.activeMeleeEnchant;
    }

    public synchronized BattleEnchantment getArmorEnchant()
    {
        return this.activeArmorEnchant;
    }

    public TeamColors getColor()
    {
        return this.teamColor;
    }

    public synchronized boolean isEliminated()
    {
        return isEliminated;
    }

    public String getCapitalizedColor()
    {
       return this.teamColor.getName();
    }

    public ShopKeeper getTeamQuickBuy()
    {
        return teamQuickBuy;
    }

    public ShopKeeper getTeamGroupBuy()
    {
        return teamGroupBuy;
    }

    public Forge getForge() {
        return forge;
    }

    public Coordinate getChest() {
        return chest;
    }

    public String getTeamPrefix() {
        return teamPrefix;
    }

    public String getTeamPostfix()
    {
        return teamPostfix;
    }

    public TeamColors getTeamColor() {
        return teamColor;
    }

    public GameBoundary getBed()
    {
        return bed;
    }
}

