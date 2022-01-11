package me.camm.productions.bedwars.Arena.Teams;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.TeamTraps.Trap;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Generators.Forge;
import me.camm.productions.bedwars.Items.ItemDatabases.BattleEnchantment;
import me.camm.productions.bedwars.Items.ItemDatabases.TeamItem;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.TeamBuyInventory;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;
import me.camm.productions.bedwars.Util.ExecutableBoundaryLoader;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import me.camm.productions.bedwars.Util.Locations.RegisterType;
import me.camm.productions.bedwars.Util.PacketSound;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.BED_DESTROYED;
import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.LAST_LIFE_WARNING;
import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.*;
import static me.camm.productions.bedwars.Util.Locations.RegisterType.EVERYTHING;


public class BattleTeam
{
    private final ConcurrentHashMap<UUID, BattlePlayer> players;

    //////////////////////////////////////////////////////////////////

    //Make a hashmap here of upgrades in the team buy

    private final TeamBuyInventory teamInventory;

    private BattleEnchantment activeMeleeEnchant;
    private BattleEnchantment activeArmorEnchant;

    //team item - upgrade, int - index of upgrade
    private final ConcurrentHashMap<TeamItem,Integer> upgrades; //only for non renewable upgrades
    private volatile int dragonSpawnNumber;
    private ExecutableBoundaryLoader loader;
    private boolean auraActive;


    //////////////////////////////////////////////////////////////////



    private final TeamColors teamColor;  //this is for the color of the team
    private final Arena arena;

    private final GameBoundary bed;
    private final Coordinate chest;
    private final GameBoundary box;
    private final GameBoundary aura;
    private final GameBoundary trapArea;

    private final Trap[] traps;

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
        auraActive = false;


        this.trapArea = trapArea;
        this.arena = arena;

        this.teamSpawn = teamSpawn.getAsLocation(arena.getWorld());
        this.teamBuy = teamBuy.getAsLocation(arena.getWorld());
        this.quickBuy = quickBuy.getAsLocation(arena.getWorld());

        this.players = new ConcurrentHashMap<>();

        this.isEliminated = false;

        this.activeArmorEnchant = null;
        this.activeMeleeEnchant = null;
        loader = null;

        this.teamPrefix = teamColor.getChatColor()+"["+teamColor.getSymbol()+"]";
        this.traps = new Trap[3];
        this.teamInventory = new TeamBuyInventory();


        this.upgrades = new ConcurrentHashMap<>();
        for (TeamItem item: TeamItem.values())
        {
            if (!item.isRenewable() && !item.toString().contains("SLOT"))
                upgrades.put(item, 1);
        }

        this.dragonSpawnNumber = 1;
    }

    public void setLoader(ExecutableBoundaryLoader loader){
        this.loader = loader;
    }

    //return the next trap to activate and shift the queue down (we could use a queue here)
    private synchronized Trap shiftTrapsDown()
    {
        Trap current = traps[0];

        traps[0] = null;
        int index = 0;
        while (index < traps.length-1)
        {
            index ++;
            traps[index-1] = traps[index];
            traps[index] = null;
        }

        return current;
    }

    public int countTraps(){
        int traps = 0;

       for (Trap trap: this.traps)
           if (trap != null)
               traps ++;

        return traps;
    }

    public synchronized void addTrap(Trap trap)
    {
        for (int slot=0;slot<traps.length;slot++)
        {
            if (traps[slot] == null) {
                traps[slot] = trap;

                if (loader != null)
                    loader.load(this, true);

                return;
            }
        }
    }

    public synchronized Trap activateNextTrap() {
        Trap next = shiftTrapsDown();
        if (next == null)
            return null;

        updateTrapDisplay();
        next.activate();
        return next;
    }

    public synchronized void loadAura(){
        if (loader != null && (!auraActive)) {
            loader.load(this,false);
        }
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


    //updating the hashmap so when the display is updated, it is accurate
    public boolean updateUpgradeTeamModifier(TeamItem item)
    {
        if (upgrades.containsKey(item) && (upgrades.get(item) <= (item.getCost().length)))
        {
            upgrades.replace(item,upgrades.get(item) + 1);
            return true;
        }
        return false;
    }

    // -1 means it doesn't exist or something happened.
    public int getUpgradeIndex(TeamItem item){
        if (upgrades.containsKey(item))
            return upgrades.get(item);
        return -1;
    }

    //updating the inventory display
    public void updateModifierDisplay(TeamInventoryConfig item)
    {
        //if it's in the non - renewable upgrades
        if (upgrades.containsKey(item.getItems())) {
            int[] slots = item.getSlots();
            int index = upgrades.get(item.getItems());
          //  System.out.println("UpdateModDisplay ) [DEBUG] Updating for : "+item.name() +" and index = "+ index);

            for (int slot : slots) {
               // System.out.println("[DEBUG]UpdateModDisplay: item: "+item.name()+" index: "+index+" slot:"+slot);
                teamInventory.setItem(item, index, slot);

            }
        }
        else
          updateTrapDisplay();

    }

    public void updateTrapDisplay(){
        teamInventory.setItem((traps[0]== null) ? TeamInventoryConfig.TRAP_ONE : traps[0].getTrapConfig() ,1,TeamInventoryConfig.TRAP_ONE.getSlots()[0]);
        teamInventory.setItem((traps[1]== null) ? TeamInventoryConfig.TRAP_TWO : traps[1].getTrapConfig() ,1,TeamInventoryConfig.TRAP_TWO.getSlots()[0]);
        teamInventory.setItem((traps[2]== null) ? TeamInventoryConfig.TRAP_THREE : traps[2].getTrapConfig() ,1,TeamInventoryConfig.TRAP_THREE.getSlots()[0]);
    }

    //This is for applying modifiers that affect players, not the environment around them
    public void applyPlayerTeamModifiers()
    {
        BattleEnchantment sword;
        BattleEnchantment prot;

         sword = translateToSharpness();
         prot = translateToProtectionEnchantments();

         if (prot != null)
             setArmorEnchant(prot);


         if (sword != null)
             setMeleeEnchant(sword);

        players.forEach((uuid,player) -> this.applyPlayerModifiersToPlayer(player));

    }

    public void applyPlayerModifiersToPlayer(BattlePlayer player)
    {
        ItemHelper.setArmor(ItemHelper.addArmorEnchants(player.getRawPlayer().getInventory().getArmorContents(),activeArmorEnchant),player.getRawPlayer());
        Inventory inv = player.getRawPlayer().getInventory();
        for (int slot=0;slot<inv.getSize();slot++)
        {
            ItemStack current = inv.getItem(slot);
            if (ItemHelper.isItemInvalid(current))
                continue;
            if (ItemHelper.isSword(current.getType()))
                ItemHelper.enchant(current,activeMeleeEnchant);
        }

        int haste = upgrades.get(TeamItem.BUFF_HASTE)-1;
        System.out.println("[DEBUG] haste lvl: "+haste);
        if (haste == 1)
            player.getRawPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,Integer.MAX_VALUE,0,false));
         else if (haste == 2)
            player.getRawPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,Integer.MAX_VALUE,1,false));

    }





    public BattleEnchantment translateToProtectionEnchantments() {
        try {
            int enchantmentLevel = upgrades.get(TeamItem.UPGRADE_PROTECTION) -1;
            BattleEnchantment enchant = null;
            switch (enchantmentLevel)
            {
                case 1:
                    enchant = BattleEnchantment.PROTECTION_ONE;
                    break;

                case 2:
                    enchant = BattleEnchantment.PROTECTION_TWO;
                    break;

                case 3:
                    enchant = BattleEnchantment.PROTECTION_THREE;
                    break;

                case 4:
                    enchant = BattleEnchantment.PROTECTION_FOUR;
                    break;
            }
            return enchant;
        }
        catch (Exception ignored)
        {

        }

     return null;
    }

    public BattleEnchantment translateToSharpness(){
        try {
            int enchantLevel = upgrades.get(TeamItem.UPGRADE_SWORDS) -1;
            if (enchantLevel == 0)
                return null;
            return BattleEnchantment.SHARPNESS_ONE;

        }
        catch (Exception ignored)
        {

        }
        return null;
    }

    ////////////////////


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

        aura.register(world, AURA.getData(), EVERYTHING.getType(),plugin);
        aura.register(world, teamColor.getName(),EVERYTHING.getType(),plugin);

        trapArea.register(world, TRAP.getData(), EVERYTHING.getType(),plugin);
        trapArea.register(world, teamColor.getName(), EVERYTHING.getType(), plugin);
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

        Collection<BattlePlayer> players = this.players.values();
        for (BattlePlayer player: players)
        {
            if (!player.getIsEliminated()&&player.getRawPlayer().isOnline())
                remaining++;
        }

        return remaining;
    }

    public void sendTeamPacket(Packet<?> packet){
        players.forEach((uuid,player) -> player.sendPacket(packet));
    }


    public void sendTeamSoundPacket(PacketSound sound){
        players.forEach((uuid,player) -> player.playSound(sound));
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

    public Arena getArena()
    {
        return arena;
    }

    public GameBoundary getAura() {
        return aura;
    }

    public synchronized Trap nextTrap(){
        return traps[0];
    }

    public TeamBuyInventory getTeamInventory()
    {
        return teamInventory;
    }

    public int getCurrentUpgradeIndex(TeamItem item){
        return upgrades.getOrDefault(item, -1);
    }

    public int getTrapMaxStack(){
        return traps.length;
    }

    public GameBoundary getTrapArea() {
        return trapArea;
    }

    public synchronized void setDragonSpawnNumber(int number){
        this.dragonSpawnNumber = number;
    }

    public synchronized int getDragonSpawnNumber(){
        return dragonSpawnNumber;
    }

    public int getMaxTrapNumber(){
        return traps.length;
    }
}

