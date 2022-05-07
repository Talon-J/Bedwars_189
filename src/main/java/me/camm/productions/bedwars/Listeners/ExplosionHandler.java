package me.camm.productions.bedwars.Listeners;


import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Entities.ActiveEntities.GameDragon;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameTeamable;
import me.camm.productions.bedwars.Explosions.ExplosionParticle;
import me.camm.productions.bedwars.Explosions.Vectors.ExplosionVector;
import me.camm.productions.bedwars.Explosions.VectorToolBox;
import me.camm.productions.bedwars.Explosions.Vectors.TracerVector;
import me.camm.productions.bedwars.Explosions.VelocityComponent;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Explosion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;


public class ExplosionHandler implements Listener
{
    private final Plugin plugin;
    private final int[] colors;
    private final Arena arena;
    private final static int BLOCK_BREAK_RANGE;
    private final EntityActionListener actionListener;

    private final static Random rand;

    static {
        rand = new Random();
        BLOCK_BREAK_RANGE = 12;
    }


    public ExplosionHandler(Plugin plugin, Arena arena, EntityActionListener listener)
    {
        this.plugin = plugin;
        this.colors = arena.getTeamColorsAsInt();
        this.arena = arena;
        this.actionListener = listener;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent exploded)
    {

        Entity entity = exploded.getEntity();
        boolean doCalculation = false;
        boolean incendiary = false;

        EntityType type = entity.getType();
        switch (type)
        {
            case PRIMED_TNT:
                exploded.setCancelled(true);
                doCalculation = true;
                break;

            case FIREBALL:
            case SMALL_FIREBALL:
                exploded.setCancelled(true);
                doCalculation = true;
                incendiary = true;
                break;
        }

        if (!doCalculation)
        return;

        sendVectors(incendiary,entity,exploded);

    }//method


    @SuppressWarnings("deprecation")
    public void sendVectors(boolean incendiary, Entity exploded, EntityExplodeEvent explodeEvent)
    {
        World world = exploded.getWorld();

        //playing the explosion particle effect
        new ExplosionParticle(exploded.getLocation(),world,plugin);

        ArrayList<ExplosionVector> directions = getBlockBreakingVectors(world, incendiary, exploded);
        ArrayList<Block> fireCandidates = new ArrayList<>();

        Location explosionCenter = exploded.getLocation();
        double damageDistance = incendiary ? 6:8;


        Collection<Entity> entities = explosionCenter.getWorld().getNearbyEntities(explosionCenter,damageDistance,damageDistance,damageDistance);

        //now we calculate the damage for the entities first since we need to account for the blocks that might be
        //in the way protecting them.


        Vector origin = explosionCenter.toVector();
                entities.forEach(entity -> {

                    IS_LIVING:
                    {

                        if (!(entity instanceof LivingEntity) || (!VectorToolBox.isValidDamageType(entity)))
                            break IS_LIVING;

                        Vector location = entity.getLocation().toVector();
                        Vector direction = location.clone().subtract(origin.clone());
                        double length = direction.length();

                        TracerVector tracer = new TracerVector(direction.clone().normalize(), origin.clone(), length, world);
                        ArrayList<Material> obstructions = tracer.getObstructionLayers();

                            double damage = -0.25*((0.5*length)) +1;

                    //    damage *= -0.1 * (0.5*length + 0.3*obstructions.size()) + 1;  // this is a function


                        if (damage < 0)
                            damage = 0;


                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(exploded, entity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, damage);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled())
                        {
                            UUID id = entity.getUniqueId();
                            if (actionListener.contains(id))
                            {

                                //accounting for enderdragons.
                              IGameTeamable teamEntity = actionListener.getEntity(id);
                              if (teamEntity instanceof GameDragon)
                              {
                                  //Explosion(World world, Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1)
                                  Explosion explosion = new Explosion(((CraftWorld)world).getHandle(),((CraftEntity)exploded).getHandle(),location.getX(), location.getY(), location.getZ(),0,false, false);
                                  ((GameDragon)teamEntity).dealRawDamage(DamageSource.explosion(explosion),(float)event.getFinalDamage());
                              }
                              else
                              {
                                  ((LivingEntity)entity).damage(event.getFinalDamage(), exploded);
                                  entity.setLastDamageCause(event);
                              }
                              return;
                            }

                            if (arena.getPlayers().containsKey(entity.getUniqueId())) {
                                ((LivingEntity)entity).damage(event.getFinalDamage(), exploded);
                            }
                        }

                        if (!VectorToolBox.isValidVelocityType(entity))
                            break IS_LIVING;

                        //unfinished. velocity needs reworking.
                        VelocityComponent component = new VelocityComponent(explodeEvent);
                        component.applyVelocity();
           }
        });

        double distance = 0;

        while  (distance<BLOCK_BREAK_RANGE)  //8 is arbitrarily the max distance tnt can break blocks.
        {

            for (int rays=directions.size()-1;rays>0;rays--)
            {
                Block block = directions.get(rays).blockAtDistance(distance);
                boolean broken = directions.get(rays).conflict(block); //determine if the block should be broken.

                if (!broken) {
                    if (!directions.get(rays).validate())  //If the vector has lost power, then remove it to save resources.
                        directions.remove(rays);
                    continue;
                }

                VectorToolBox.breakAtPosition(directions.get(rays).blockAtDistance(distance)); //breaking the block

                if (incendiary)
                      fireCandidates.add(block);  //consider the block for fire [Might not be air]

                if (!directions.get(rays).validate())  //If the vector has lost power, then remove it to save resources.
                    directions.remove(rays);

            }//for

            //Advance the vectors
            distance += 0.1;

        } //while


        if (!incendiary)  //if a fireball
            return;


            for (Block fireCandidate : fireCandidates) {
                //getting block below the air block in the arraylist
                Block testBlock = fireCandidate.getLocation().subtract(0, 1, 0).getBlock();
                Material currentMaterial = testBlock.getType();

                boolean valid = false; //false --> can't be set on fire

                switch (currentMaterial) {
                    case WOOD: {
                        if (VectorToolBox.isDataDestructable(testBlock.getData(), false, testBlock, colors))
                            valid = true; //can be set on fire
                    }
                    break;

                    case STAINED_CLAY:
                    case WOOL: {
                        if (VectorToolBox.isDataDestructable(testBlock.getData(), true, testBlock, colors))
                            valid = true;  // can be set on fire
                    }

                }
                //woodData,false,block

                if (valid) {
                    rollFireChance(fireCandidate);
                     }//if valid
            }


    }

    //we should probably change this to be better. (I.e use final variables, make it more exact, such as a 90.5% chance instead of 90%)
    private void rollFireChance(Block block)
    {
        int setFire = rand.nextInt(11);
        if (setFire >= 9)
            block.setType(Material.FIRE);
    }

    private ArrayList<ExplosionVector> getBlockBreakingVectors(World world, boolean incendiary, Entity exploded)
    {
        ArrayList<ExplosionVector> directions = new ArrayList<>();
        Location loc = exploded.getLocation();
        double xLocation = loc.getX();
        double yLocation = loc.getY();
        double zLocation = loc.getZ();

        //Creating the vectors based on angles.
        //minecraft angles for the vertical pitch go from -90 to 90, where -90 is directly up and 90 is directly down.
        //minecraft angles are in radians though, but that's fine since math.sin and cos take care of that.

        //unless you wanna go through the trouble of decompiling, translating, and registering a subclass of entity tnt,
        // we're doing this instead.
        for (double vertical=90;vertical>=-90;vertical-=5.625)
        {
            double yComponent = Math.tan(vertical); //y value

            //vectors going out in 360 degrees by intervals of 5.625 degrees
            //5.625 degrees is an arbitrary amount such that most, if not all blocks in a radius are hit by vectors similar
            //to regular tnt
            for (double horizontal=0;horizontal<=360;horizontal+=5.625)
            {
                double xComponent = Math.sin(horizontal);  //x Value of the vector
                double zComponent = Math.cos(horizontal);  //z Value of the vector

                directions.add(new ExplosionVector(new Vector(xComponent,yComponent,zComponent),
                        new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));
            }
        }

        //Adding vectors separately so that there aren't many instances of the same one.
        //this is for the vectors going directly up and down cause going 360 degrees in the x and z axis when they are
        // both 0 make a bunch of traces
        directions.add(new ExplosionVector(new Vector(0,1,0),
                new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));

        directions.add(new ExplosionVector(new Vector(0,-1,0),
                new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));
        return directions;
    }

}//class
