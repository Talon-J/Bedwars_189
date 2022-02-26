package me.camm.productions.bedwars.Arena.Players;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.Managers.HotbarManager;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.PlayerBoard;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamTitle;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.QuickBuyEditor;
import me.camm.productions.bedwars.Listeners.PacketHandler;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Files.FileCreators.PlayerFileCreator;
import me.camm.productions.bedwars.Files.FileStreams.PlayerFileReader;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.ItemDatabases.TieredItem;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import me.camm.productions.bedwars.Util.PacketSound;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

import static me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader.*;


public class BattlePlayer implements IPlayerUtil
{

    private final Arena arena;
    private volatile Player player;
    private volatile long lastMilk;

    //boolean values to determine if the player is eliminated or alive.
    //The alive value is for when they are in spectator mode and about to respawn.
    private boolean isEliminated;
    private volatile boolean isAlive;

    //The time the player has until next respawn.
    //Used by a player death counter and also for when bed destroyed while counting down.
    //we use the int for a significant subtitle.
    //E.g bed destroyed while respawn message is being played.
    //The significant title is only used for the bed and respawn messages, not for other messages  (E.g dragon spawn message)
    //we use it since if we don't, there will be a cutoff of what is being shown (nothing will be shown instead)


    private volatile int timeTillRespawn;





    private Team playerTeam;


    //Managers for their hotbar and their quickbuy
    private HotbarManager barManager;
    private PlayerInventoryManager shopManager;


    //Shopkeepers to send to them.
    private final HashMap<Integer, ShopKeeper> toResend;

    //Board object for their scores.
    private PlayerBoard board;

    //Their number in the game.
    private final int number;


    //The team they belong to.
    private BattleTeam team;

    //Int values for their kills, finals, and beds broken.
    private volatile int finals;
    private volatile int kills;
    private volatile int beds;



    //The possible persistent items a player can have.
    private ShopItem shears;
    private TieredItem pick;
    private TieredItem axe;
    private TieredItem armor;


    //editor
    private QuickBuyEditor quickEditor;



    private static final int eliminationTime;
    static {
        eliminationTime = 5;  //5
    }

    public BattlePlayer(Player player, BattleTeam team, Arena arena, int number)
    {
        this.arena = arena;
        this.team = team;

        this.number = number;
        this.player = player;

        this.isEliminated = false;
        this.timeTillRespawn = 0;
        this.isAlive = true;

        this.finals = 0;
        this.kills = 0;
        this.beds = 0;

        this.barManager = null;
        this.shopManager = null;


        pick = null;
        axe = null;
        shears = null;
        armor = TieredItem.LEATHER_ARMOR;

        this.toResend = new HashMap<>();

        createBoard();
        PlayerFileCreator creator = new PlayerFileCreator(this,arena);
        creator.createDirectory(); creator.createHotBarFile(); creator.createInventoryFile();



        this.lastMilk = 0;

    }


    @SuppressWarnings("deprecation")
    public void register()
    {
        Scoreboard healthBoard = arena.getHealthBoard();
        unregister(healthBoard);

        //registering a separate team for them.
        playerTeam = healthBoard.registerNewTeam(team.getTeamColor().getName()+number);
        playerTeam.setPrefix(team.getTeamPrefix());
        playerTeam.setSuffix(team.getTeamPostfix());
        player.setScoreboard(healthBoard);
        playerTeam.addPlayer(player);
    }

    @SuppressWarnings("deprecation")
    public void unregister(Scoreboard healthBoard)
    {
        try {
            playerTeam.removePlayer(player);
            healthBoard.getTeam(team.getTeamColor().getName() + number).unregister();
        }
        catch (NullPointerException | IllegalStateException | IllegalArgumentException ignored)
        {

        }
    }

    public void removeInvisibilityEffect() {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
      //  System.out.println("[DEBUG] Remove invis effect");
    }



    /*
    @author CAMM
    @param newPlayer: Takes in a new player object to replace the old one.

    Used to replace the player object and reset the scoreboard when the player rejoins into the game
    if they were previously registered.

    Every time a player leaves and joins, it's not the same player object that was previously referenced.
    Also the reason why every listener here must get the player list from the arena (centralized info area)
    and not keep a copy of the hashmap of players.

     */
    public synchronized void refactorPlayer(Player newPlayer)
    {
        this.player = newPlayer;
        board.unregisterRegardless();
        createBoard();
    }

    /*
    @Author CAMM
    Gets information on the player's configuration for their hotbar and quickbuy files.
     */
    public void instantiateConfig(boolean isInflated)
    {
        PlayerFileReader reader = new PlayerFileReader(arena.getPlugin(),this.player,isInflated);
        this.barManager = reader.readBarFile();
        this.shopManager = reader.readInvFile();

         quickEditor = new QuickBuyEditor(this);
        shopManager.setOwner(this);
    }

    /*
    @Author CAMM.
    Unfinished. Toggles whether the player should be invisible to other teams. Used when
    the player drinks an invisibility potion.

    Update: Should hopefully be all good. Still need to test.
     */
    public synchronized void togglePotionInvisibility(boolean isInvisible, PacketHandler handler)
    {
        if (isInvisible)
        {
            hideArmor();
            handler.addInvisiblePlayer(this.player);
            if (playerTeam!=null)
            playerTeam.setNameTagVisibility(NameTagVisibility.NEVER);
        }
        else
        {
            handler.removeInvisiblePlayer(this.player);
            if (playerTeam!=null)
                playerTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
          sendArmorUpdate();
        }
    }

    /*
    @Author CAMM
    Updates the scoreboard and armor stuff on the player's side, but does not account for the invisibility through
    the packet handler.
    refactor to be private
     */
    public synchronized void removeUnprocessedInvisibility()
    {
            if (playerTeam!=null)
                playerTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);

            sendArmorUpdate();
    }


    /*
    @Author CAMM.
    Gets the player's armor as nms items and returns them in an Item stack array.
    refactor to be private
     */
    public synchronized net.minecraft.server.v1_8_R3.ItemStack[] getNMSArmor()
    {
        net.minecraft.server.v1_8_R3.ItemStack[] items = new ItemStack[4];
        PlayerInventory inv = player.getInventory();
        items[3] = ItemHelper.toNMSItem(inv.getHelmet());
        items[2] = ItemHelper.toNMSItem(inv.getChestplate());
        items[1] = ItemHelper.toNMSItem(inv.getLeggings());
        items[0] = ItemHelper.toNMSItem(inv.getBoots());
        return items;
    }


   //Equips the current armor onto the player.
    public void equipArmor()
    {
      ItemHelper.setArmor(ItemHelper.inventoryItemToArmor(getArmor().getItem(),this),getRawPlayer());
    }


    /*
    @Author CAMM
    Sends a packet to all players on opposing teams.
     */
    public void sendOppositionPackets(Packet<?> packet)
    {
        arena.getPlayers().forEach((uuid, battlePlayer) -> {
            if (!battlePlayer.getTeam().equals(this.team))
                battlePlayer.sendPacket(packet);
        });
    }

    public void sendPacketsAllNonEqual(Packet<?> packet){
        arena.getPlayers().forEach((uuid,battlePlayer) -> {
            if (!battlePlayer.equals(this)) {
                battlePlayer.sendPacket(packet);
            }
        });
    }


    /*
    @Author CAMM
    Hides the player's armor by sending packets to the opposition.
     */
    public void hideArmor()
    {
        int id = this.player.getEntityId();
        int slot = 1;

        while (slot<5)
        {
            PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(id,slot,null);
            sendOppositionPackets(equipment);
            slot++;
        }
    }



    /*
    @Author CAMM
    Sends packets to all players except this one regarding this player's armor (Updating their information)
     */
    public void sendArmorUpdate()
    {
        //getting this player's armor
       final net.minecraft.server.v1_8_R3.ItemStack[] items = getNMSArmor();
       final int id = this.player.getEntityId();

        arena.getPlayers().forEach((uuid, battlePlayer) -> {
            if (!battlePlayer.equals(this))
            {
                //slot goes from boots to helmet (1-4)
                int slot = 0;

                for (ItemStack stack: items)
                    battlePlayer.sendPacket(new PacketPlayOutEntityEquipment(id,++slot,stack));

            }
        });
    }




    /*
    @Author CAMM
    Puts the player onto a different score on the packet scoreboard and refreshes their board.
    Problems with player numbers not showing up correctly should not be an issue here.
    Make sure to reflect the change on the scoreboards of the other players after changing teams since
    this method does not update scoreboards for other players.
     */
    public boolean changeTeam(BattleTeam newTeam)
    {

       if (!this.team.equals(newTeam)&&!newTeam.isEliminated())
       {
           board.setScoreName(CURRENT_TEAM.getPhrase(), getTeamStatus(this.team));
           //sets the score with the "you" to the default score name regarding a team.

           board.setScoreName(newTeam.getColor().getName(), getTeamStatus(newTeam) + CURRENT_TEAM.getPhrase());
           //Sets the default score to one with the "you"

           board.interchangeIdentifiers(CURRENT_TEAM.getPhrase(), this.team.getColor().getName(), newTeam.getColor().getName());
            //Interchanging identifiers. This makes it so that the scores keep the same positions.

           //removing the player from their previous team.
           this.team.removePlayer(this.player);

           //setting their team to the team they switched to.
           this.team = newTeam;

           //Adding the player to the new team.
           this.team.addPlayer(this);

           player.setScoreboard(arena.getHealthBoard());
           board.switchPrimaryBuffer();
           return true;
       }

       else
       {
           player.sendMessage(ChatColor.RED+"That Team is Invalid!");
           return false;
       }
    }

   /*
    @Author CAMM
    Sends a title and a subtitle to a player, with specified time for fading and stay.
    The string given must be in Minecraft's chat component format used like in command blocks.
    E.g  "{\"text\":\"abc\"}"

    Use another method to check that the titles given are properly formatted.
    */
    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut)
    {
        try {
            IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(title);
            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, component);
            PacketPlayOutTitle subPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(subTitle));
            PacketPlayOutTitle titleLength = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
            sendPacket(titlePacket);
            sendPacket(titleLength);
            sendPacket(subPacket);
        }
        catch (Exception ignored)
        {

        }
    }


    /*
    Use this method to send titles to the player involving respawn messages and time left before they respawn.
     */
    public void sendRespawnTitle(TeamTitle title, TeamTitle subTitle, int secondsRemaining, int fadeIn, int stay, int fadeOut)
    {
        String respawn = addRespawnNumber(subTitle,secondsRemaining);
        sendTitle(title.getMessage(), respawn, fadeIn, stay, fadeOut);
    }




    /*
    Unfinished. Need to account for the packet handler.
    Also, when they come out of spec, hide all spec players from them, and allow all alive players to see them.
    Also remember to account for potion effects and items, as well as nametag visibility.

    Maybe use:
    public void toggleSpectator(boolean isSpectator, boolean isFinal, PacketHandler handler)


    Toggles a player's spectator mode.
    Does not teleport the player anywhere
    Does not account for armor or persistent items.

    Refactor to be private.
     */
    private void toggleSpectator(boolean isSpectator, PacketHandler handler)
    {
        togglePotionInvisibility(false,handler);
        player.setFallDistance(0);
        player.setVelocity(new Vector(0,0,0));

        if (isSpectator)
        {



            for (BattlePlayer current: arena.getPlayers().values())
            {
               if (current.equals(this))
                  continue;

               if (current.getIsAlive())
                   current.getRawPlayer().hidePlayer(player);
               else
                   current.getRawPlayer().showPlayer(player);
            }
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setCanPickupItems(false);
            setAlive(false);
            ((CraftPlayer)player).getHandle().collidesWithEntities = false;
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,0,false,false));
        }
        else
        {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setCanPickupItems(true);
            setAlive(true);
            ((CraftPlayer)player).getHandle().collidesWithEntities = true;
            player.removePotionEffect(PotionEffectType.INVISIBILITY);

            for (BattlePlayer current: arena.getPlayers().values())
            {
                if (current.equals(this))
                    continue;

                //Show the current battleplayer to all other players.
                current.getRawPlayer().showPlayer(player);

                //If the other player is currently in spectator, hide the spectator from them.

                if (!current.isAlive)
                    player.hidePlayer(current.getRawPlayer());
            }
        }
    }

    //TODO use these methods to handle when the player dies and respawns.
    //Also make sure to empty their inv before setting to spec.






    public void handlePlayerIntoSpectator(PacketHandler handler, boolean isFinal, Player killer)
    {
        dropInventory(player.getLocation().clone(),killer);
        teleport(arena.getSpecSpawn());


        if (!isAlive || isEliminated) {
            clearInventory(this.player);
            sendPacketsAllNonEqual(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer)this.player).getHandle()));
            //  sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
            return;
        }

        toggleSpectator(true, handler);
        heal();


        if (isFinal)
        {
            setEliminated(true);
           boolean sendMessage = emptyEnderChest();
            sendPacketsAllNonEqual(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer)this.player).getHandle()));

           int remaining = team.getRemainingPlayers();
           if (remaining == 0)
               team.eliminate();

            if (killer !=null && sendMessage)
                killer.sendMessage("[PLACEHOLDER] - Items put from the enderchest of "+player.getName()+" into their forge.");

            return;
        }


        new BukkitRunnable()
        {
            int seconds = 0;
            public void run()
            {
                if (seconds < eliminationTime)
                {
                    setTimeTillRespawn(eliminationTime-seconds);

                    if (team.doesBedExist())
                        sendRespawnTitle(TeamTitle.YOU_DIED,TeamTitle.RESPAWN_AFTER,getTimeTillRespawn(),0,60,10);
                    else
                        sendRespawnTitle(TeamTitle.BED_DESTROYED,TeamTitle.RESPAWN_AFTER,getTimeTillRespawn(),0,60,10);
                    seconds ++;
                }
                else if (player.isOnline())
                {
                    setTimeTillRespawn(0);
                    handlePlayerRespawn(handler);
                    cancel();
                }
                else {
                    setTimeTillRespawn(0);
                    cancel();
                }
            }
        }.runTaskTimer(arena.getPlugin(),0,20);
    }

    public void handlePlayerIntoSpectator(PacketHandler handler, boolean isFinal)
    {
        handlePlayerIntoSpectator(handler,isFinal, null);
    }



    public void handlePlayerRespawn(PacketHandler handler)
    {
        setSurvival();
        setEliminated(false);
        team.teleportToBase(player);
        toggleSpectator(false, handler);


        player.getInventory().clear();

        if (getShears() != null)
        barManager.set(ItemHelper.toSoldItem(getShears(),this),getShears(),player);

        if (getPick() != null) {
            TieredItem worsePick = handlePersistentItemDegradation(getPick());
            setPickDownwards(worsePick);

            barManager.set(ItemHelper.toSoldItem(pick.getItem(), this), getPick().getItem(), player);
        }
        if (getAxe() != null) {
            TieredItem worseAxe = handlePersistentItemDegradation(getAxe());
            setAxeDownwards(worseAxe);

            barManager.set(ItemHelper.toSoldItem(axe.getItem(), this), getAxe().getItem(), player);
        }
        barManager.set(ItemHelper.toSoldItem(ShopItem.WOODEN_SWORD,this), ShopItem.WOODEN_SWORD,player);
        heal();
        equipArmor();

        sendTitle(TeamTitle.RESPAWNED.getMessage(), null,2,40,10);
        team.applyPlayerModifiersToPlayer(this);
    }



    private TieredItem handlePersistentItemDegradation(TieredItem current)
    {
        if (current == null)
            return null;

        TieredItem previousTier = ItemHelper.getPreviousTier(current);
        return previousTier == null ? current: previousTier;

    }

    public void handlePlayerFirstSpawn()
    {
        player.getInventory().clear();

        setSurvival();
        setEliminated(false);
        team.teleportToBase(player);
        heal();
        equipArmor();
        barManager.set(ItemHelper.toSoldItem(ShopItem.WOODEN_SWORD,this), ShopItem.WOODEN_SWORD,player);
    }


    //Convenience Helper methods
    /////////////////////////////////////////////

    /*
    @Author CAMM
    Convenience method for sending packets to the current player.
     */
    public void sendPacket(Packet<?> packet)
    {
        ( (CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    /*
    @Author CAMM
    Initializing the scoreboard for the player.
     */
    private void createBoard()
    {
        board = new PlayerBoard(this,arena);
    }

    /*
    @Author CAMM
    Convenience method for teleporting the player to a location.
     */
    public void teleport(Location loc)
    {
        player.teleport(loc);
    }

    private void heal()
    {
        player.setHealth(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.setRemainingAir(300);
        player.setFoodLevel(20);
    }

    /*
    Empties and clears the player's inventory.
    Currency items (gold, iron, etc) are dropped
     */
    public void dropInventory(final Location deathLocation, final Player killer)
    {
        Inventory inv = player.getInventory();
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                World w = player.getWorld();
                if (killer == null)
                Arrays.stream(inv.getContents()).filter(item -> Objects.nonNull(item)&&ItemHelper.isCurrencyItem(item)).forEach(item -> {
                    org.bukkit.entity.Item drop = w.dropItem(deathLocation,item);
                    drop.setPickupDelay(0);

                });
               else
                    Arrays.stream(inv.getContents()).filter(item -> Objects.nonNull(item)&&ItemHelper.isCurrencyItem(item)).forEach(item -> {
                                org.bukkit.entity.Item drop = w.dropItem(killer.getLocation(), item);
                                drop.setPickupDelay(0);
                            });

                clearInventory(player);
                cancel();
            }
        }.runTask(arena.getPlugin());

    }

      /*
        Empties and clears the player's enderchest.
        Currency items are dropped in their team's forge.

        If the enderchest was not empty, then returns true.
        Else returns false.
         */
    private boolean emptyEnderChest()
    {
        ArrayList<org.bukkit.inventory.ItemStack> toDrop = new ArrayList<>();
        Inventory chest = player.getEnderChest();
        Arrays.stream(chest.getContents()).filter(ItemHelper::isCurrencyItem).forEach(toDrop::add);
        chest.clear();

        new BukkitRunnable()
        {
            public void run()
            {
                Location loc = team.getForge().getForgeLocation();
                World world = loc.getWorld();
                for (org.bukkit.inventory.ItemStack item: toDrop)
                    world.dropItem(loc,item);

                cancel();
            }
        }.runTask(arena.getPlugin());

        return toDrop.size() > 0;
    }

    public void updatePlayerStatistics()
    {
        board.setScoreName(FINALS.getPhrase(), FINALS.getPhrase()+getFinals());
        board.setScoreName(KILLS.getPhrase(), KILLS.getPhrase()+getKills());
        board.setScoreName(BEDS.getPhrase(), BEDS.getPhrase()+getBeds());
    }



    /*
    @Author CAMM
    Convenience method for sending a message to the player.
     */
    public void sendMessage(String message)
    {
        this.player.sendMessage(message);
    }

    public void playSound(PacketSound sound)
    {
        Location loc = player.getLocation();
        sendPacket(new PacketPlayOutNamedSoundEffect(sound.getSoundName(),loc.getX(),loc.getY(),loc.getZ(),1,sound.getPitch()));
    }

    /*
    @Author CAMM
    Returns a boolean whether the given id is part of the hashmap for the npcs
    Who need to be refreshed in terms of packets.
     */
    public synchronized boolean containsNPC(int id)
    {
        return toResend.containsKey(id);
    }

    /*
    @Author CAMM
    Removes a npc to be resent to the player.
     */
    public synchronized void removeResender(int id)
    {
        toResend.remove(id);
    }

    public synchronized void addResender(ShopKeeper keeper)
    {
        toResend.putIfAbsent(keeper.getId(), keeper);
    }



    /*
    This is the code to make it so that the scoreboard is accurate in the tab and below the name. :D
     */
    public void sendHealthUpdatePackets()
    {
        PacketPlayOutScoreboardScore nameHealth =
        modify(new PacketPlayOutScoreboardScore(),player.getName(),HEALTH_CATEGORY.getPhrase(),Math.round((float)player.getHealth()));

        PacketPlayOutScoreboardScore tabHealth =
        modify(new PacketPlayOutScoreboardScore(),player.getName(),HEALTH_CATEGORY_TWO.getPhrase(),Math.round((float)player.getHealth()));


        arena.getPlayers().values().forEach(player -> {
            if (nameHealth != null)
                player.sendPacket(nameHealth);

            if (tabHealth != null)
                player.sendPacket(tabHealth);
        });
    }

    private PacketPlayOutScoreboardScore modify(PacketPlayOutScoreboardScore packet, String playerName, String objectiveName, int score)
    {
        try
        {
            Field fieldA = PacketPlayOutScoreboardScore.class.getDeclaredField("a");
            Field fieldB = PacketPlayOutScoreboardScore.class.getDeclaredField("b");
            Field fieldC = PacketPlayOutScoreboardScore.class.getDeclaredField("c");
            Field fieldD = PacketPlayOutScoreboardScore.class.getDeclaredField("d");

            fieldA.setAccessible(true);
            fieldB.setAccessible(true);
            fieldC.setAccessible(true);
            fieldD.setAccessible(true);

            fieldA.set(packet, playerName);
            fieldB.set(packet,objectiveName);
            fieldC.set(packet, score);
            fieldD.set(packet,PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);

            return packet;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /*
[22:41:32 INFO]: Field A:CAMM_H87
[22:41:32 INFO]: Field B:tabHealth
[22:41:32 INFO]: Field C:20
     */





    //Setter methods
    ////////////////////////////////////////////////////



    public void setSurvival()
    {
        this.player.setGameMode(GameMode.SURVIVAL);
    }

    public synchronized void setAlive(boolean isAlive)
    {
        this.isAlive = isAlive;
    }

    public synchronized void setTimeTillRespawn(int seconds)
    {
        this.timeTillRespawn = seconds;
    }

    public synchronized void setLastMilkTime(long time){
        this.lastMilk = time;
    }

    public long getLastMilk(){
        return lastMilk;
    }

    /*
    This value is later shown in their scoreboard, which is controlled externally.
     */
    public synchronized void setFinals(int newFinals)
    {
        this.finals = newFinals;
    }

    public synchronized void setShears()
    {
        shears = ShopItem.SHEARS;
    }

    /*
    This value is later shown in their scoreboard, which is controlled externally.
     */
    public synchronized void setKills(int newKills)
    {
        this.kills = newKills;
    }

    public synchronized void setPickUpwards(TieredItem pick) {
        this.pick = pick;
        TieredItem upgrade = ItemHelper.getNextTier(pick);
           shopManager.replaceItem(pick.getItem(),upgrade == null? pick.getItem() : upgrade.getItem());

    }

    public synchronized void setPickDownwards(TieredItem pick) {
        TieredItem pickCloned = this.pick;
        this.pick = pick;

        TieredItem prevUpgrade = ItemHelper.getNextTier(pickCloned);
        TieredItem currentUpgrade = ItemHelper.getNextTier(pick);

        shopManager.replaceItem(prevUpgrade == null ? pick.getItem(): prevUpgrade.getItem(),currentUpgrade == null ? pick.getItem() : currentUpgrade.getItem());

    }

    public synchronized void setAxeUpwards(TieredItem axe) {
        this.axe = axe;
        TieredItem upgrade = ItemHelper.getNextTier(axe);
        shopManager.replaceItem(axe.getItem(),upgrade == null? axe.getItem() : upgrade.getItem());
    }



    public synchronized void setAxeDownwards(TieredItem axe) {
        TieredItem axeCloned = this.axe;
        this.axe = axe;

        TieredItem prevUpgrade = ItemHelper.getNextTier(axeCloned);
        TieredItem currentUpgrade = ItemHelper.getNextTier(axe);

        shopManager.replaceItem(prevUpgrade == null ? axe.getItem(): prevUpgrade.getItem(),currentUpgrade == null ? axe.getItem() : currentUpgrade.getItem());

    }

    public void hideEliminatedPlayers(){
        Collection<BattlePlayer> players = arena.getPlayers().values();

        for (BattlePlayer player: players)
        {
            if (player.isEliminated && !player.equals(this))
              sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer)player.getRawPlayer()).getHandle()));
        }
    }

    /*
    @Author CAMM
    Setter method to set the number of beds the player has broken.
    This value is later shown in their scoreboard, which is controlled externally.
     */
    public synchronized void setBeds(int bedNumber)
    {
        this.beds = bedNumber;
    }

    /*
    @Author CAMM
    Setter method to set whether the player should be eliminated.
    This value is later shown in their scoreboard, which is controlled externally.
     */
    public synchronized void setEliminated(boolean isEliminated)
    {
        this.isEliminated = isEliminated;
    }

    public synchronized void setPurchasedArmor(TieredItem armor) {
        this.armor = armor;
    }





    //Getter methods
////////////////////////////////////////////////////


    public synchronized int getTimeTillRespawn() {
        return timeTillRespawn;
    }

    public synchronized boolean getIsAlive()
    {
        return this.isAlive;
    }

    public synchronized PlayerBoard getBoard()
    {
        return board;
    }

   public Player getRawPlayer()
   {
       return player;
   }

    public int getNumber()
    {
        return number;
    }

    public synchronized int getKills()
    {
        return kills;
    }

    public synchronized int getFinals()
    {
        return finals;
    }

    public synchronized int getBeds()
    {
        return beds;
    }

    public synchronized TieredItem getArmor() {
        return armor;
    }

    public synchronized ShopItem getShears() {
        return shears;
    }

    public synchronized TieredItem getPick() {
        return pick;
    }

    public synchronized TieredItem getAxe() {
        return axe;
    }

    public UUID getUUID()
    {
        return player.getUniqueId();
    }

    public BattleTeam getTeam()
    {
        return team;
    }

    public HotbarManager getBarManager()
    {
        return barManager;
    }

    public PlayerInventoryManager getShopManager()
    {
        return shopManager;
    }

    public synchronized boolean getIsEliminated()
    {
        return this.isEliminated;
    }

    public QuickBuyEditor getQuickEditor() {
        return quickEditor;
    }


}


