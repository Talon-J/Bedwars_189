package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Entities.ActiveEntities.Golem;
import me.camm.productions.bedwars.Entities.ActiveEntities.ThrownFireball;
import me.camm.productions.bedwars.Entities.PacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class ItemInteractListener implements Listener
{
    private final Plugin plugin;
    private final PacketHandler handler;
    private final EntityActionListener entityListener;


    private final HashMap<UUID, Long> coolDown;
    private final Arena arena;

    private static final int DELAY;
    private static final int DIVISION;

    static {
        DELAY = 500;
        DIVISION = 1000;
    }


    public ItemInteractListener(Plugin plugin, Arena arena, PacketHandler handler, EntityActionListener entityListener)
    {
        this.plugin = plugin;
        this.coolDown = new HashMap<>();
        this.arena = arena;
        this.handler = handler;
        this.entityListener = entityListener;

    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event)
    {
        //For when a player drinks an invis potion.
        ItemStack stack = event.getItem();
        Player player = event.getPlayer();

        if (stack==null || stack.getType()==null || stack.getItemMeta() == null)
            return;

        if (stack.getType()!=Material.POTION)
            return;

        if (!isRegistered(player))
            return;

        //If it is an invis potion.

        //Unfinished. Use packets to see if the player should be set to visible
        //and also do it in the damage listener.
        PotionMeta meta = (PotionMeta)stack.getItemMeta();
       if  (meta.hasCustomEffect(PotionEffectType.INVISIBILITY))
       {
           BattlePlayer battlePlayer = arena.getPlayers().get(player.getUniqueId());
           battlePlayer.togglePotionInvisibility(true,handler);
       }
    }



    @EventHandler
    public void onItemInteract(PlayerInteractEvent event)
    {

        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();
        Block block = event.getClickedBlock();

        if (!players.containsKey(player.getUniqueId()))
            return;

        BattlePlayer currentPlayer = players.get(player.getUniqueId());



        if (stack == null)
            return;

        Material mat = stack.getType();

        if (mat == null)
            return;

        Action action = event.getAction();
        switch (mat)
        {
            case FIREBALL:
               if (action==Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
               {
                   event.setCancelled(true);
                   updateMap(event.getPlayer());
               }
                break;

            case MONSTER_EGG:
                if (action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() != null)
                {
                    EntityType type = ((SpawnEgg) stack.getData()).getSpawnedType();
                    if (type != EntityType.IRON_GOLEM)
                        return;

                    Golem golem = new Golem(currentPlayer.getTeam(), currentPlayer,arena,event.getClickedBlock().getLocation(),entityListener);
                    golem.spawn();
                    entityListener.addEntity(golem);
                    updateInventory(player,Material.MONSTER_EGG);
                }
                break;
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event)
    {
        Entity clicked = event.getRightClicked();
        if (clicked.getType()!= EntityType.ARMOR_STAND)
        {
            if (event.getPlayer().getInventory().getItemInHand().getType()==Material.FIREBALL&&isRegistered(event.getPlayer())) {
                event.setCancelled(true);
                //updating the hashmap and also shooting a fireball if possible.
                updateMap(event.getPlayer());
            }
        }

        //add code here for the golems and the snowballs and enderpearls.
        //also put in above method.
    }



    public void updateMap(Player player)
    {
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
        if (!players.containsKey(player.getUniqueId()))
            return;

        BattlePlayer currentlyRegistered = players.get(player.getUniqueId());


        if (!coolDown.containsKey(player.getUniqueId())) //if shot for first time
        {
            coolDown.put(player.getUniqueId(),(System.currentTimeMillis()+DELAY));  //add cooldown


            new ThrownFireball(plugin,currentlyRegistered); //create a fireball
            updateInventory(player,Material.FIREBALL);

        }
        else  //if the map contains the player
        {
            Long value = coolDown.get(player.getUniqueId());
            if (System.currentTimeMillis()>=value)  //if the cooldown has run out [system time is greater]
            {
                coolDown.remove(player.getUniqueId());
                entityListener.addEntity(new ThrownFireball(plugin,currentlyRegistered));
                updateInventory(player,Material.FIREBALL);
                coolDown.put(player.getUniqueId(),System.currentTimeMillis()+DELAY);
            }
            else //otherwise if the cooldown has not run out yet
            {
                player.sendMessage(ChatColor.RED+"You must wait "+(((double)(coolDown.get(player.getUniqueId())-System.currentTimeMillis()))
                        /DIVISION)+" seconds first!");
            }
        }

    }






    public void updateInventory(Player player, Material toDecrease)
    {

        PlayerInventory inv = player.getInventory();


        if (inv.getItemInHand()!=null&&inv.getItemInHand().getItemMeta()!=null) {
            ItemStack update = player.getInventory().getItemInHand();

            if (update.getType() == toDecrease)
            {
                update.setAmount(update.getAmount() - 1);
                player.setItemInHand(update);

            }
            else
            {
                for (int slot=0;slot<player.getInventory().getSize();slot++)
                {
                    if (player.getInventory().getItem(slot).getType()==toDecrease)
                    {
                        update = player.getInventory().getItem(slot);
                        update.setAmount(update.getAmount()-1);
                        player.getInventory().setItem(slot,update);
                        break;
                    }
                }
            }//else
            player.updateInventory();


        }//if not null

    }//method

    private synchronized boolean isRegistered(Player player)
    {
        return arena.getPlayers().containsKey(player.getUniqueId());
    }
}
