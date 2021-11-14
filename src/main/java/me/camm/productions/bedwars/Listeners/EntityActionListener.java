package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameTeamable;
import me.camm.productions.bedwars.Entities.PacketHandler;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Util.DataSets.DamageSet;
import me.camm.productions.bedwars.Util.Helpers.RunningTeamHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityActionListener implements Listener
{
    private final Plugin plugin;
    private final Arena arena;
    private final GameRunner runner;
    private final ConcurrentHashMap<UUID, DamageSet> damageHistory;
    private final ConcurrentHashMap<UUID, IGameTeamable> activeEntities;


    /*
    Unfinished.
    TODO:
    - Associate indirect damage with players or entities on teams. (Use a hashmap and damageset class in util)
    - also account for persistent items, and degradeable items. (This should be taken care of in the battleplayer class) [DONE! just need testing]

    We will most likely also need to merge this class with a player move listener (for the void), and associate it with traps
     and the heal pool as well.

     */

    public EntityActionListener(Arena arena, Plugin plugin, GameRunner runner)  //construct
    {
        this.arena = arena;
        this.plugin = plugin;
        this.runner = runner;
        this.damageHistory = new ConcurrentHashMap<>();
        this.activeEntities = new ConcurrentHashMap<>();
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if (isItem(damaged))
        {
            event.setCancelled(true);
            return;
        }

       if (damaged instanceof Player && damager instanceof Player)
           handlePVPDamage(event);
       else
           handleIndirectEntityDamage(event);
    }

    //Unfinished. TODO
    //If 1 or 0 entities are players.
    private void handleIndirectEntityDamage(EntityDamageByEntityEvent event)
    {
        //We need a list of active entities in the game that we can associate to.
        //If it's a projectile, handle it differently.

        /*
        Handler for:

        DirectEntity:
        minions (golem, silverfish)
        tnt
        dragons

        IndirectEntity:
        arrows. For arrows, we only need to check for the shooter.

        Fireballs. for fireballs, we will probably need to check if they're registered
        since they deal explosive damage, not impact damage.


        ///////////
        Also, for tnt:
        Make a refactor:

        Set yield to 0
        - use a different set of distances / equations for damage and velocity.
        + prevents need to account for the innate velocity. Makes it much easier to code the physics for it.

        ///////////////////




        Actually no, refactor that.

         So a tnt should be namable. The name of the tnt should be the
        uuid of the owner. The name should not be visible. We refer to the name of the tnt when attributing
        kills of the tnt.

        For fireballs, we can refer to the shooter.


        So the only entities that should be in the activeEntities hashmap should be the iron golems, dragons, and
        silverfish.



         */

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        //If 1 is a projectile, other is a player.
        if (damager instanceof Projectile && damaged instanceof Player) {
            handlePlayerIndirectProjectileDamage(event);
            return;
        }

        //If both entities are not players.
        if (!(damager instanceof Player) && !(damaged instanceof Player)) {
            handleNonPlayerEntityDamage(event);
            return;
        }



        /*
        Below this line, 1 is a player, 1 is an entity, and the damaging entity is
        not a projectile (Not a fireball, not an arrow),

        so it's either a golem, tnt, dragon, or silverfish.


         */
    }

    //If one entity is a player and the other is a projectile (Either fireball or arrow)
    private void handlePlayerIndirectProjectileDamage(EntityDamageByEntityEvent event)
    {
        Projectile damager = (Projectile) event.getDamager();
        ProjectileSource source = damager.getShooter();
        Entity hurt = event.getEntity();

        if (source == null) {
            event.setCancelled(true);
            return;
        }

        if (!(hurt instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        if (damager instanceof Arrow)
        {
            Arrow arrow  = (Arrow)damager;

            return;
        }

        if (damager instanceof Fireball)
        {
            Fireball ball = (Fireball) damager;
            plugin.getServer().broadcastMessage("[DEBUG] -DETECT - FIRE");


        }







    }

    //If both entities are not players.
    private void handleNonPlayerEntityDamage(EntityDamageByEntityEvent event)
    {
        /*
        Note that 1 could still be a projectile.
        So it could be a golem and a fireball for example,
         or a golem and another team's silverfish/ golem.
         */

    }



    /*
    This method handles direct pvp damage. (melee damage)
     */
    private void handlePVPDamage(EntityDamageByEntityEvent event)
    {
        plugin.getServer().broadcastMessage("[DEBUG]: Entity damage - Direct pvp");
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();

        Player hit = (Player)damaged;
        Player hitter = (Player)damager;


        //If both players are not registered
        if (!players.containsKey(hit.getUniqueId()) && !players.containsKey(hitter.getUniqueId()))
            return;

        //If an unregistered player hits a registered one
        if (!players.containsKey(hitter.getUniqueId()))
        {
            event.setCancelled(true);
            return;
        }

        //If both players are registered

        BattlePlayer hurt = players.get(hit.getUniqueId());
        BattlePlayer hurter = players.get(hitter.getUniqueId());

        //If both players are from the same team.
        if (hurt.getTeam().equals(hurter.getTeam()))
        {
            event.setCancelled(true);
            return;
        }


        ////////////////////////
        //below: the damage is actually applied to the player.


        //if they would have died.
        if (hit.getHealth() - event.getFinalDamage() >= 1)
            return;
        arena.sendMessage("[DEBUG]: Death detect - player");
        event.setCancelled(true);

        PacketHandler handler = runner.getPacketHandler();

        //if the kill wasn't final
        if (hurt.getTeam().doesBedExist())
        {
            arena.sendMessage("[DEBUG]: Death detect - player - not final ");
            hurter.setKills(hurter.getKills()+1);
            hurt.handlePlayerIntoSpectator(handler,false);
        }
        else
        {
            plugin.getServer().broadcastMessage("[DEBUG]: Death detect - player - final");
            hurter.setFinals(hurter.getFinals()+1);
            hurt.handlePlayerIntoSpectator(handler,true);

            BattleTeam team = hurt.getTeam();
            if (team.getRemainingPlayers()==0)
            {
                team.eliminate();
                BattleTeam candidate = RunningTeamHelper.isVictorFound(arena.getTeams().values());
                if (candidate==null)
                    return;

                runner.endGame(candidate);
            }
        }

        if (!event.isCancelled())
            damageHistory.put(hit.getUniqueId(),new DamageSet(event,System.currentTimeMillis()));
        //debug for scoreboard
    }





    @EventHandler
    public void onEntityHealthRegain(EntityRegainHealthEvent event)
    {
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
       Entity entity = event.getEntity();
       if (!(entity instanceof Player))
           return;

       if (!players.containsKey(entity.getUniqueId()))
           return;

       BattlePlayer player = players.get(entity.getUniqueId());

       //TODO unfinished.

    }


    @EventHandler
    public void onPlayerIndirectDamage(EntityDamageEvent event)
    {
        Entity damaged = event.getEntity();
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();

        if (isItem(damaged))
        {
            event.setCancelled(true);
            return;
        }


        if (damaged instanceof Player)
        {
            EntityDamageEvent.DamageCause cause = event.getCause();
            Player player = (Player)damaged;

            if (!arena.getPlayers().containsKey(player.getUniqueId()))
                return;

            BattlePlayer hurt = arena.getPlayers().get(player.getUniqueId());

            DamageSet history = null;
            Entity previousDamageSource;

            if (damageHistory.containsKey(player.getUniqueId())) {
                history = damageHistory.get(player.getUniqueId());
                damageHistory.remove(player.getUniqueId());
            }


            switch (cause)
            {
                case FALL:
                case FIRE:
                case MAGIC:
                case LAVA:
                case VOID:
                case DROWNING:
                case FIRE_TICK:
                case CONTACT:
                case SUFFOCATION:
                case FALLING_BLOCK:
                case LIGHTNING:
                case CUSTOM:
                case MELTING:
                case STARVATION:
                case POISON:
                case THORNS:
                case WITHER:
                case SUICIDE:
                    player.sendMessage("[DEBUG] - Indirect (non entity) Damage");


                    //If the player is a spectator
                    if (!hurt.getIsAlive() ||  hurt.getIsEliminated())
                    {
                        event.setCancelled(true);
                        return;
                    }


                    // if the player will not die from this event.
                    if (player.getHealth() - event.getFinalDamage() >= 1) {
                        return;
                    }

                    event.setCancelled(true);
                    boolean doesBedExist = hurt.getTeam().doesBedExist();

                    //if the history of damage is null, simply put them into spec
                    if (history == null) {
                        hurt.handlePlayerIntoSpectator(runner.getPacketHandler(), !doesBedExist);
                        return;
                    }


                    previousDamageSource =  history.getEvent().getDamager();



                    //If the previous entity damage source WAS a registered player
                    if (players.containsKey(previousDamageSource.getUniqueId()))
                    {
                        /*
                        If the entity is in the players hashmap, then we can be confident that it is a player.
                         */

                        BattlePlayer hurter = players.get(previousDamageSource.getUniqueId());
                        long timeElapsed = System.currentTimeMillis() - history.getSystemTime() / 1000;

                        if (timeElapsed <= 10) {
                            hurt.handlePlayerIntoSpectator(runner.getPacketHandler(), !doesBedExist,(Player)previousDamageSource);

                            //If it counts as a pvp death
                            if (doesBedExist)
                                hurter.setKills(hurter.getKills() + 1);
                            else
                                hurter.setFinals(hurter.getFinals() + 1);

                            return;
                        }

                        /*
                        Enough time has passed such that it does not count as a pvp death
                         */
                        hurt.handlePlayerIntoSpectator(runner.getPacketHandler(), !doesBedExist);
                    }

//hit.getHealth() - event.getFinalDamage() > 1
                    break;
            }
        }
    }

    public synchronized void addEntity(IGameTeamable entity)
    {
        activeEntities.put(entity.getUUID(),entity);
    }


    public synchronized void removeEntity(UUID id)
    {
        activeEntities.remove(id);
    }

    public synchronized boolean contains(UUID id)
    {
        return activeEntities.containsKey(id);
    }

    private boolean isItem(Entity entity)
    {
        return entity instanceof Item;
    }



    public static class NPCDisplayManager implements Runnable
    {
        private final ArrayList<ShopKeeper> keepers;
        private final Arena arena;
        private final Plugin plugin;
        private volatile boolean isRunning;
        private final PacketHandler handler;

        public NPCDisplayManager(Plugin plugin, Arena arena, ArrayList<ShopKeeper> keepers, PacketHandler handler)
        {
            this.arena = arena;
            this.keepers = keepers;
            this.plugin = plugin;
            this.isRunning = true;
            this.handler = handler;
        }

        public synchronized void setRunning(boolean isRunning)
        {
            this.isRunning = isRunning;
        }


        @Override
        public void run()
        {
            new BukkitRunnable()
            {
                public void run()
                {
                    if (!isRunning)
                    {
                        cancel();
                        return;
                    }

                        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();

                        Bukkit.getOnlinePlayers().stream().filter(player -> players.containsKey(player.getUniqueId())).forEach(player -> {
                            BattlePlayer current = players.get(player.getUniqueId());

                            if (player.getLocation().getY() <= arena.getVoidLevel())
                                current.handlePlayerIntoSpectator(handler,!current.getTeam().doesBedExist());

                            keepers.forEach(keeper -> {
                                if ((player.getLocation().distance(keeper.getLocation())>48)&&(!current.containsNPC(keeper.getId())))
                                {

                                    keeper.removeNPC(player);
                                    current.addResender(keeper);
                                }
                                else if (current.containsNPC(keeper.getId())&&player.getLocation().distance(keeper.getLocation())<48)
                                {

                                    current.removeResender(keeper.getId());
                                    keeper.sendNPC(player);
                                    keeper.setRotation(player);
                                }
                            });
                        });

                }
            }.runTaskTimer(plugin, 0,2);
        }
    }
}

