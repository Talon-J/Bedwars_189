package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameOwnable;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameTeamable;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Util.DataSets.DamageSet;
import me.camm.productions.bedwars.Util.PacketSound;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
    private static final ConcurrentHashMap<UUID, DamageSet> damageHistory;
    private static final ConcurrentHashMap<UUID, IGameTeamable> activeEntities;
    private final ConcurrentHashMap<UUID,BattlePlayer> arenaPlayers;
    private final PacketHandler handler;

    static {
       damageHistory = new ConcurrentHashMap<>();
        activeEntities = new ConcurrentHashMap<>();
    }


    private static void handleDeath(BattlePlayer victim, PacketHandler handler, ConcurrentHashMap<UUID,BattlePlayer> players, GameRunner runner)
    {
        Player hurt = victim.getRawPlayer();
        boolean isFinal = !victim.getTeam().doesBedExist();
        System.out.println("[DEBUG] Player death handle: dead:"+victim.getRawPlayer().getName()+" final:"+isFinal);

        if (damageHistory.containsKey(hurt.getUniqueId()))
        {
            DamageSet set = damageHistory.get(hurt.getUniqueId());
            long millis = set.getSystemTime();

            if (System.currentTimeMillis() - millis > 10000)
            {
                System.out.println("[DEBUG] Death handle: kill time time out handle");
                victim.handlePlayerIntoSpectator(handler, isFinal);
                runner.attemptEndGame();
                return;
            }

            //so it was direct player damage

            Entity damager = set.getEvent().getDamager();
            if (players.containsKey(damager.getUniqueId()))
            {
                System.out.println("[DEBUG] Death handle: pvp handle");
                BattlePlayer killer = players.get(set.getEvent().getDamager().getUniqueId());

                if (!victim.equals(killer))
                handleKillerStats(killer, isFinal);

                victim.handlePlayerIntoSpectator(handler,isFinal, killer.getRawPlayer());
                runner.attemptEndGame();
                return;
            }

            if (damager instanceof Projectile)
            {
                System.out.println("[DEBUG] death handle projectile death");
                ProjectileSource source = ((Projectile)damager).getShooter();
                if (source instanceof Player) {
                    BattlePlayer shooter = players.getOrDefault(((Player)source).getUniqueId(),null);

                    if (!victim.equals(shooter))
                    handleKillerStats(shooter, isFinal);
                    victim.handlePlayerIntoSpectator(handler,isFinal, shooter.getRawPlayer());
                    runner.attemptEndGame();
                    return;
                }
            }


            if (damager instanceof TNTPrimed)
            {
                String name = damager.getCustomName();
                System.out.println("[DEBUG] Death handle tnt, tnt name:"+name);
                if (name != null) {

                    BattlePlayer killer = null;
                    for (BattlePlayer player: players.values())
                    {
                        if (player.getRawPlayer().getUniqueId().toString().equalsIgnoreCase(name)){
                           killer = player;
                           break;
                        }
                    }

                    if (killer == null) {
                        victim.handlePlayerIntoSpectator(handler, isFinal);
                        runner.attemptEndGame();
                        return;
                    }

                    if (!victim.equals(killer))
                        handleKillerStats(killer, isFinal);

                    victim.handlePlayerIntoSpectator(handler,isFinal, killer.getRawPlayer());
                    runner.attemptEndGame();
                    return;
                }
            }


            if (activeEntities.containsKey(damager.getUniqueId())) {
                IGameTeamable teamable = activeEntities.get(damager.getUniqueId());
                if (teamable instanceof IGameOwnable) {
                    BattlePlayer owner = ((IGameOwnable)teamable).getOwner();
                    if (owner == null)
                    {
                        victim.handlePlayerIntoSpectator(handler, isFinal);
                        runner.attemptEndGame();
                        return;
                    }

                    if (!victim.equals(owner))
                    handleKillerStats(owner, isFinal);

                    victim.handlePlayerIntoSpectator(handler,isFinal, owner.getRawPlayer());
                    runner.attemptEndGame();
                    return;

                }
                return;
            }
        }
            victim.handlePlayerIntoSpectator(handler, isFinal);
        runner.attemptEndGame();
    }

    private static void handleKillerStats(BattlePlayer killer, boolean isFinal) {
        killer.playSound(PacketSound.DING);
        if (isFinal)
            killer.setFinals(killer.getFinals()+1);
        else
            killer.setKills(killer.getKills()+1);
    }

    private void updateHistoryDamage(BattlePlayer hurt, EntityDamageByEntityEvent event){
        if (damageHistory.containsKey(hurt.getUUID()))
            damageHistory.replace(hurt.getUUID(),new DamageSet(event, System.currentTimeMillis()));
        else
            damageHistory.put(hurt.getUUID(),new DamageSet(event, System.currentTimeMillis()));

    }


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

        arenaPlayers = arena.getPlayers();
        handler = runner.getPacketHandler();
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



    @EventHandler
    public void onPlayerIndirectDamage(EntityDamageEvent event)
    {
        Entity damaged = event.getEntity();

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
                    if (wouldPlayerSurvive(player, event))
                        return;

                    event.setCancelled(true);
                    handleDeath(hurt,handler,arenaPlayers,runner);
                    break;
            }
        }
    }


    //Unfinished. TODO
    //If 1 or 0 entities are players.
    private void handleIndirectEntityDamage(EntityDamageByEntityEvent event)
    {

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

        if (activeEntities.containsKey(damaged.getUniqueId()) && damager instanceof Player) {

            BattlePlayer hitter = arenaPlayers.getOrDefault(damager.getUniqueId(),null);
            if (hitter == null) {
                event.setCancelled(true);
                return;
            }

            IGameTeamable teamable = activeEntities.get(damaged.getUniqueId());
            if (teamable.getTeam().equals(hitter.getTeam())) {
                event.setCancelled(true);
                return;
            }

        }

        if (!arenaPlayers.containsKey(damaged.getUniqueId()))
            return;


        if (damager instanceof TNTPrimed) {
            String customName = damager.getCustomName();
            Player hit = (Player)damaged;

            if (customName == null)
            {
                event.setCancelled(true);
                return;
            }
                BattlePlayer playerHit = arenaPlayers.get(hit.getUniqueId());

                updateHistoryDamage(playerHit,event);

                BattlePlayer playerOwner = null;
                for (BattlePlayer player: arenaPlayers.values()) {
                    if (player.getRawPlayer().getUniqueId().toString().equalsIgnoreCase(customName)) {
                        playerOwner = player;
                        break;
                    }
                }


                if (playerOwner == null)
                {
                    event.setCancelled(true);
                    return;
                }

                if (wouldPlayerSurvive(playerHit.getRawPlayer(),event))
                    return;

                handleDeath(playerHit,handler,arenaPlayers,runner);
                return;
        }// if it is tnt

        if (activeEntities.containsKey(damager.getUniqueId()))
        {
            IGameTeamable teamable = activeEntities.get(damager.getUniqueId());
            BattlePlayer hit = arenaPlayers.get(damaged.getUniqueId());

            if (teamable.getTeam().equals(hit.getTeam())) {
                event.setCancelled(true);
                return;
            }

            updateHistoryDamage(hit,event);

            if (!wouldPlayerSurvive(hit.getRawPlayer(),event)) {
                handleDeath(hit,handler,arenaPlayers,runner);
            }
        }
        else
            event.setCancelled(true);



        /*
        Below this line, 1 is a player, 1 is an entity, and the damaging entity is
        not a projectile (Not a fireball, not an arrow),

        so it's either a golem, tnt, dragon, or silverfish.


         */
    }


    //TODO Please test this method.
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

        ConcurrentHashMap<UUID, BattlePlayer> registered = arena.getPlayers();
   if (!registered.containsKey(hurt.getUniqueId()))
       return;

        BattlePlayer hurtPlayer = registered.get(hurt.getUniqueId());

        if (!(source instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        if (!registered.containsKey(((Player)source).getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        BattlePlayer sourcePlayer = registered.get(((Player)source).getUniqueId());
        boolean isFinal = !hurtPlayer.getTeam().doesBedExist();



        if (damager instanceof Arrow)
        {
            plugin.getServer().broadcastMessage("[DEBUG] arrow hit player dmg");
            Arrow arrow = (Arrow)damager;


            if (hurtPlayer.equals(sourcePlayer)) {
                event.setCancelled(true);
                return;
            }

            double hp = hurtPlayer.getRawPlayer().getHealth()-event.getFinalDamage();
            hp *= 1000;
            hp = Math.round(hp);
            hp /= 1000.0;

            updateHistoryDamage(hurtPlayer,event);
      sourcePlayer.sendMessage(ChatColor.YELLOW+hurtPlayer.getRawPlayer().getName()+" is on "+(hp)+" hp!");

            if (!wouldPlayerSurvive((Player)hurt,event))
            {
                event.setCancelled(true);
                arrow.remove();


                handleDeath(hurtPlayer, handler, arenaPlayers,runner);

                //send a death message here.
                arena.sendMessage("[DEBUG] death detect (direct arrow death) "+hurtPlayer.getRawPlayer().getName()
                        +" was killed by "+sourcePlayer.getRawPlayer().getName()+" final:"+isFinal);
            }
            return;
        }



        if (damager instanceof Fireball)
        {
            updateHistoryDamage(hurtPlayer,event);
            plugin.getServer().broadcastMessage("[DEBUG] -DETECT - Fireball dmg");

            if (!wouldPlayerSurvive(hurtPlayer.getRawPlayer(), event)) {
                event.setCancelled(true);
               handleDeath(hurtPlayer,handler, arenaPlayers,runner);
                arena.sendMessage("[DEBUG]death detect (direct fireball death) "+hurtPlayer.getRawPlayer().getName()
                        +" was killed by "+sourcePlayer.getRawPlayer().getName()+" final:"+isFinal);
            }
        }
    }

    //If both entities are not players.
    private void handleNonPlayerEntityDamage(EntityDamageByEntityEvent event)
    {
        Entity hurt = event.getEntity();
        Entity damager = event.getDamager();

        if (damager instanceof Projectile)
        {
            Projectile hurter = (Projectile)damager;
            if (hurt instanceof Projectile)
                return;


            ProjectileSource source = hurter.getShooter();
            if (source instanceof Player && arenaPlayers.containsKey(((Player) source).getUniqueId())){
                BattlePlayer shot = arenaPlayers.get(((Player)source).getUniqueId());

                if (!activeEntities.containsKey(hurt.getUniqueId()))
                    return;

                IGameTeamable teamable = activeEntities.get(hurt.getUniqueId());
                if (shot.getTeam().equals(teamable.getTeam()))
                    event.setCancelled(true);


            }
        }

        /*
        //If both entities are not players.
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

        Player hit = (Player)damaged;
        Player hitter = (Player)damager;


        //If both players are not registered
        if (!arenaPlayers.containsKey(hit.getUniqueId()) && !arenaPlayers.containsKey(hitter.getUniqueId()))
            return;

        //If an unregistered player hits a registered one
        if (!arenaPlayers.containsKey(hitter.getUniqueId()))
        {
            event.setCancelled(true);
            return;
        }

        BattlePlayer hurt = arenaPlayers.get(hit.getUniqueId());

        updateHistoryDamage(hurt,event);

        //If both players are from the same team.
        if (wouldPlayerSurvive(hurt.getRawPlayer(),event))
            return;

        arena.sendMessage("[DEBUG]: Death detect - player");
        event.setCancelled(true);
        handleDeath(hurt,handler, arenaPlayers,runner);
        //debug for scoreboard
    }


    public synchronized void addEntity(IGameTeamable entity)
    {
        try {
            //TODO
            //So fireballs and tnt shouldn't be registered here. Check the gameTNT and Fireball classes.

            activeEntities.put(entity.getUUID(), entity);
        }
        catch (NullPointerException ignored)
        {

        }
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

    public synchronized IGameTeamable getEntity(UUID id){
        return activeEntities.getOrDefault(id, null);
    }

    private boolean wouldPlayerSurvive(Player player, EntityDamageEvent event) {
        return player.getHealth() - event.getFinalDamage() >= 1;
    }



    public static class NPCDisplayManager implements Runnable
    {
        private final ArrayList<ShopKeeper> keepers;
        private final Arena arena;
        private final Plugin plugin;
        private volatile boolean isRunning;
        private final PacketHandler handler;
        private final GameRunner runner;

        public NPCDisplayManager(Plugin plugin, Arena arena, ArrayList<ShopKeeper> keepers, PacketHandler handler, GameRunner runner)
        {
            this.arena = arena;
            this.keepers = keepers;
            this.plugin = plugin;
            this.isRunning = true;
            this.handler = handler;
            this.runner = runner;
        }

        public synchronized void setRunning(boolean isRunning)
        {
            this.isRunning = isRunning;
        }


        @Override
        public void run()
        {
            ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
            new BukkitRunnable()
            {
                public void run()
                {
                    if (!isRunning)
                    {
                        cancel();
                        return;
                    }

                       players.forEach((uuid, player) -> {
                           Player raw = player.getRawPlayer();


                            if (raw.getLocation().getY() <= arena.getVoidLevel())
                            {

                                VOID:
                                {
                                    if (damageHistory.containsKey(player.getUUID())) {

                                        DamageSet set = damageHistory.get(player.getUUID());
                                        long millis = set.getSystemTime();

                                        if (System.currentTimeMillis() - millis > 10000) {
                                            player.handlePlayerIntoSpectator(handler, !player.getTeam().doesBedExist());
                                            break VOID;
                                        }

                                        if (players.containsKey(set.getEvent().getDamager().getUniqueId())) {
                                            BattlePlayer killer = players.get(set.getEvent().getDamager().getUniqueId());
                                            boolean isFinal = !player.getTeam().doesBedExist();

                                             handleKillerStats(killer,isFinal);

                                            player.handlePlayerIntoSpectator(handler, isFinal, killer.getRawPlayer());
                                            break VOID;
                                        }
                                    }

                                    player.handlePlayerIntoSpectator(handler, !player.getTeam().doesBedExist());
                                }
                                runner.attemptEndGame();
                            }

                            keepers.forEach(keeper -> {
                                if ((raw.getLocation().distanceSquared(keeper.getLocation())>2304)&&(!player.containsNPC(keeper.getId())))
                                {
                                    keeper.removeNPC(raw);
                                    player.addResender(keeper);
                                }
                                //2304 is 48 ^2. We check if the distance is comparable to 48, but without the extra calculations of sqrt
                                else if (player.containsNPC(keeper.getId())&&raw.getLocation().distanceSquared(keeper.getLocation())<2304)
                                {

                                    player.removeResender(keeper.getId());
                                    keeper.sendNPC(raw);
                                    keeper.setRotation(raw);
                                }
                            });

                            raw.setFoodLevel(20);

                        });

                }
            }.runTaskTimer(plugin, 0,2);
        }
    }

}

