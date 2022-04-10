package me.camm.productions.bedwars;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.Commands.CommandKeyword;
import me.camm.productions.bedwars.Arena.GameRunning.Commands.GameIntializer;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.Managers.HotbarManager;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Entities.ActiveEntities.GameDragon;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Listeners.PacketHandler;
import me.camm.productions.bedwars.Files.FileCreators.DirectoryCreator;
import me.camm.productions.bedwars.Files.FileStreams.GameFileWriter;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.QuickBuySection;
import me.camm.productions.bedwars.Util.DataSets.ShopItemSet;
import me.camm.productions.bedwars.Util.Helpers.ChatSender;
import me.camm.productions.bedwars.Util.Helpers.StringHelper;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

public final class BedWars extends JavaPlugin
{
    private GameIntializer initialization;
    private final String DRAGON_NAME = "EnderDragon";
    private final int DRAGON_ID = 63;

    private static Plugin plugin;

    public static Plugin getPlugin(){
        return plugin;
    }



    @Override @SuppressWarnings("unchecked")
    public void onEnable()
    {

        plugin = this;
        ChatSender sender = ChatSender.getInstance();

        //we init it right away so that we can use it anywhere.

        sender.sendMessage(ChatColor.GREEN+"STARTING UP");
        sender.sendMessage(ChatColor.AQUA+"It is recommended that you make a backup of this world before starting the game, if you haven't done so.");


        DirectoryCreator fileCreator = new DirectoryCreator(this);
       if (!fileCreator.createFolders())
       {
           sender.sendPlayerMessage("FILES ARE NOT CONFIGURED. CANNOT CONTINUE UNTIL CONFIGURATION IS COMPLETE.", ChatSender.GameState.ERROR);
           sender.sendMessage("PLEASE RELOAD THE PLUGIN AFTER CONFIGURATION IS COMPLETE.");
       }
       else
       {

           try {
               Field c = EntityTypes.class.getDeclaredField("c");
               c.setAccessible(true);
               Map<String, Class<? extends Entity>> cMap = (Map<String, Class<? extends Entity>>) c.get(EntityTypes.class);
               cMap.remove(DRAGON_NAME);

               Field e = EntityTypes.class.getDeclaredField("e");
               e.setAccessible(true);
               Map<Integer, Class<? extends Entity>> eMap = (Map<Integer, Class<? extends Entity>>) e.get(EntityTypes.class);
               eMap.remove(DRAGON_ID);

               Method aMethod = EntityTypes.class.getDeclaredMethod("a",Class.class,String.class,int.class);
               aMethod.setAccessible(true);
               aMethod.invoke(EntityTypes.class, GameDragon.class, DRAGON_NAME, DRAGON_ID);

               initialization = new GameIntializer(this);

               for (CommandKeyword word: CommandKeyword.values()) {
                   getCommand(word.getWord()).setExecutor(initialization);
               }
           }
           catch (Exception e)
           {
               sender.sendPlayerMessage("Unable to register the Ender dragon. The game cannot proceed at this point.", ChatSender.GameState.ERROR);
           }
       }



    }

    @Override @SuppressWarnings("unchecked")
    public void onDisable()
    {
        ChatSender sender = ChatSender.getInstance();
        try {
            Field c = EntityTypes.class.getDeclaredField("c");
            c.setAccessible(true);
            Map<String, Class<? extends Entity>> cMap = (Map<String, Class<? extends Entity>>) c.get(EntityTypes.class);
            cMap.remove(DRAGON_NAME);

            Field e = EntityTypes.class.getDeclaredField("e");
            e.setAccessible(true);
            Map<Integer, Class<? extends Entity>> eMap = (Map<Integer, Class<? extends Entity>>) e.get(EntityTypes.class);
            eMap.remove(DRAGON_ID);

            Method aMethod = EntityTypes.class.getDeclaredMethod("a",Class.class,String.class,int.class);
            aMethod.setAccessible(true);
            aMethod.invoke(EntityTypes.class, EntityEnderDragon.class, DRAGON_NAME, DRAGON_ID);

        }
        catch (Exception e)
        {

            sender.sendPlayerMessage("Failed to unregister the Ender Dragon.", ChatSender.GameState.ERROR);
            e.printStackTrace();
        }
        



        if (initialization==null)
            return;

        if (initialization.getRunner()==null)
            return;

        GameRunner runner = initialization.getRunner();

        runner.setRunning(false);
        initialization.getArena().getTeams().forEach((string, team) -> {
            if (team!=null&&team.getForge()!=null)
            team.getForge().disableForge();
        });
      PacketHandler handler = runner.getPacketHandler();

      if (handler!=null) {
          for (Player player : Bukkit.getOnlinePlayers())
              handler.removePlayer(player);
      }

      if (runner.getLoader() != null)
      runner.getLoader().stop();

        World world = initialization.getArena().getWorld();
        Scoreboard initial = ((CraftWorld)world).getHandle().getScoreboard();
        Collection<ScoreboardObjective> objectives = initial.getObjectives();
        for (ScoreboardObjective objective: objectives)
        {
            for (Player player: Bukkit.getOnlinePlayers()) {
                try {
                    initial.unregisterObjective(objective);
                    initial.handleObjectiveRemoved(objective);
                    send(new PacketPlayOutScoreboardObjective(objective, 1), player);
                } catch (IllegalArgumentException | IllegalStateException e) {
                 sender.sendConsoleMessage("Error occurred trying to unregister objective "+objective.getName()+" for "+player.getName(), Level.WARNING);
                }
            }
        }


        Arena arena = initialization.getArena();
        if (arena == null)
            return;


            StringHelper box = new StringHelper(this);
            Collection<BattlePlayer> registered = arena.getPlayers().values();

        //writing to bar file
            registered.forEach(battlePlayer -> {

                HotbarManager barManager = battlePlayer.getBarManager();
                if (barManager != null) {
                    ItemCategory[] barItems = barManager.getLayout();

                    GameFileWriter barWriter = new GameFileWriter(box.getHotBarPath(battlePlayer.getRawPlayer()), this);
                    barWriter.clear();
                    ArrayList<String> valueList = new ArrayList<>();

                    Arrays.stream(barItems).forEach(item -> valueList.add(
                            item == null ? null : item.toString()));
                    barWriter.write(valueList, false);
                }


               //writing to shop file
                PlayerInventoryManager invManager = battlePlayer.getShopManager();
                if (invManager!=null) {
                    QuickBuySection playerShop = invManager.getQuickBuy();
                    ArrayList<ShopItemSet> shopSet = playerShop.packageInventory();

                    GameFileWriter shopWriter = new GameFileWriter(box.getInventoryPath(battlePlayer.getRawPlayer()), this);
                   shopWriter.clear();
                    ArrayList<String> shopList = new ArrayList<>();
                    shopSet.forEach(pack -> shopList.add(pack == null ? ShopItem.EMPTY_SLOT.name() : pack.toString()));
                    shopWriter.write(shopList, false);
                }
            });


        //Save all the players things here and put them into their files.
    }

    private void send(Packet<?> packet, Player player)
    {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }


    /*
TODO: (In the very near future)
 - Find a better solution to the explosives problem. [IN PROGRESS]
  + fireballs, accurate in terms of block damage.
   + fireball explosions (where they explode) are shifted. Bad.
  + tnt is not accurate
 - add tracker shop & quick comms
 - HB manager: If you use the number keys to buy items, then it goes to that number slot, not the set slot
 - Fire still spreads! <-- bad (Doesn't fade or burn out though)
  + Also, might wanna make sure that you don't have fire floating around on air


 NOTE: The check for opposition in the CLASS "Setup" is currently ----   ---- disabled for testing purposes.


active debugging changes:
 */



}
