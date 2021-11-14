package me.camm.productions.bedwars.Listeners;


import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Explosions.ExplosionParticle;
import me.camm.productions.bedwars.Explosions.TraceVector;
import me.camm.productions.bedwars.Explosions.VectorToolBox;
import me.camm.productions.bedwars.Explosions.VelocityComponent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ExplosionHandler extends VectorToolBox implements Listener
{
    private final Plugin plugin;
    private final int[] colors;
    private final Arena arena;
    private final EntityActionListener listener;


    public ExplosionHandler(Plugin plugin, Arena arena, EntityActionListener listener)
    {
        this.plugin = plugin;
        this.colors = arena.getTeamColorsAsInt();
        this.arena = arena;
        this.listener = listener;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent exploded)
    {

        Entity entity = exploded.getEntity();
        boolean doCalculation = false;
        World world = entity.getWorld();
        boolean incendiary = false;

        if (entity.getType()== EntityType.PRIMED_TNT||entity.getType()==EntityType.FIREBALL||entity.getType()==EntityType.SMALL_FIREBALL) {
            exploded.setCancelled(true);
            doCalculation = true;
            new ExplosionParticle(entity.getLocation(),world,plugin);

            listener.removeEntity(entity.getUniqueId());
            plugin.getServer().broadcastMessage("[DEBUG] -REMOVE");
        }

        if (entity.getType()==EntityType.FIREBALL||entity.getType()==EntityType.SMALL_FIREBALL)
            incendiary = true;




        if (doCalculation)
        {
           // System.out.println("Exploded is tnt");

            double xLocation = entity.getLocation().getX();
            double yLocation = entity.getLocation().getY();
            double zLocation = entity.getLocation().getZ();

            ArrayList<TraceVector> directions = new ArrayList<>();
            ArrayList<Block> fireCandidates = new ArrayList<>();

            for (double vertical=90;vertical>=-90;vertical-=11.25)
            {
                double yComponent = Math.tan(vertical); //y value
                for (double horizontal=0;horizontal<=360;horizontal+=11.25)
                {
                    double xComponent = Math.sin(horizontal);  //x Value of the vector
                    double zComponent = Math.cos(horizontal);  //z Value of the vector

                    directions.add(new TraceVector(new Vector(xComponent,yComponent,zComponent), new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));
                }
            }

            //Adding vectors separately so that there aren't 16 instances of the same one.
            directions.add(new TraceVector(new Vector(0,1,0),new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));
            directions.add(new TraceVector(new Vector(0,-1,0),new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));


            //Might need external loop here for vector expansion
            double distance = 0;

            while  (distance<8)
            {
                for (int rays=directions.size()-1;rays>0;rays--)
                {
                    Block block = directions.get(rays).blockAtDistance(distance);
                    boolean broken = directions.get(rays).conflict(block);


                    if (broken)
                    {
                        directions.get(rays).breakAtPosition(directions.get(rays).blockAtDistance(distance));

                        if (incendiary)
                            fireCandidates.add(block);  //consider the block for fire [Might not be air]
                    }

                    if (!directions.get(rays).validate())  //If the vector has lost power, then remove it to save resources.
                        directions.remove(rays);

                }//for
                //Advance the vectors and set blocks
                distance += 0.5;

            } //while


            if (incendiary)  //if a fireball
            {
                for (Block fireCandidate : fireCandidates) {
                    //getting block below the air block in the arraylist
                    Block testBlock = fireCandidate.getLocation().subtract(0, 1, 0).getBlock();
                    Material currentMaterial = testBlock.getType();

                    boolean valid = false; //false --> can't be set on fire


                    switch (currentMaterial) {
                        case WOOD: {
                            if (confirmData(testBlock.getData(), false, testBlock, colors))
                                valid = true; //can be set on fire
                        }

                        case STAINED_CLAY:
                        case WOOL: {
                            if (confirmData(testBlock.getData(), true, testBlock, colors))
                                valid = true;  // can be set on fire
                        }

                    }
                    //woodData,false,block

                    if (valid) {
                        int chance = (int) (Math.random() * 51);
                        if (chance > 48) {
                            fireCandidate.getLocation().getBlock().setType(Material.FIRE);
                        }
                    }//if valid

                } //for
            }//if incend

            VelocityComponent velocity = new VelocityComponent(exploded);
            velocity.doCalculation();

        }//if (doCal)

    }//method

}//class
