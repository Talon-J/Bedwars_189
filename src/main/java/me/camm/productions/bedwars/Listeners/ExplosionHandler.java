package me.camm.productions.bedwars.Listeners;


import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Explosions.ExplosionParticle;
import me.camm.productions.bedwars.Explosions.Vectors.ExplosionVector;
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
import java.util.Random;

public class ExplosionHandler implements Listener
{
    private final Plugin plugin;
    private final int[] colors;
    private final Arena arena;
    private final static int BLOCK_BREAK_RANGE;

    private final static Random rand;

    static {
        rand = new Random();
        BLOCK_BREAK_RANGE = 8;
    }


    public ExplosionHandler(Plugin plugin, Arena arena)
    {
        this.plugin = plugin;
        this.colors = arena.getTeamColorsAsInt();
        this.arena = arena;
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

        sendVectors(incendiary,entity);

            VelocityComponent velocity = new VelocityComponent(exploded);
            velocity.applyVelocity();

    }//method


    @SuppressWarnings("deprecation")
    public void sendVectors(boolean incendiary, Entity entity)
    {
        double xLocation = entity.getLocation().getX();
        double yLocation = entity.getLocation().getY();
        double zLocation = entity.getLocation().getZ();
        World world = entity.getWorld();

        new ExplosionParticle(entity.getLocation(),world,plugin);

        ArrayList<ExplosionVector> directions = new ArrayList<>();
        ArrayList<Block> fireCandidates = new ArrayList<>();

        for (double vertical=90;vertical>=-90;vertical-=11.25)
        {
            double yComponent = Math.tan(vertical); //y value
            for (double horizontal=0;horizontal<=360;horizontal+=11.25)
            {
                double xComponent = Math.sin(horizontal);  //x Value of the vector
                double zComponent = Math.cos(horizontal);  //z Value of the vector

                directions.add(new ExplosionVector(new Vector(xComponent,yComponent,zComponent),
                        new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));
            }
        }

        //Adding vectors separately so that there aren't 16 instances of the same one.
        directions.add(new ExplosionVector(new Vector(0,1,0),
                new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));

        directions.add(new ExplosionVector(new Vector(0,-1,0),
                new Vector(xLocation,yLocation,zLocation),world,incendiary,colors));



        double distance = 0;

        while  (distance<BLOCK_BREAK_RANGE)  //8 is arbitrarily the max distance tnt can break blocks.
        {
            //it doesn't really matter if we iterate forward or backwards...I just wanted backwards :D
            for (int rays=directions.size()-1;rays>0;rays--)
            {
                Block block = directions.get(rays).blockAtDistance(distance);
                boolean broken = directions.get(rays).conflict(block); //determine if the block should be broken.


                if (!broken)
                continue;

                VectorToolBox.breakAtPosition(directions.get(rays).blockAtDistance(distance)); //breaking the block

                if (incendiary)
                      fireCandidates.add(block);  //consider the block for fire [Might not be air]

                if (!directions.get(rays).validate())  //If the vector has lost power, then remove it to save resources.
                    directions.remove(rays);

            }//for
            //Advance the vectors and set blocks
            distance += 0.5;

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

    private void rollFireChance(Block block)
    {
        int setFire = rand.nextInt(11);
        if (setFire >= 9)
            block.setType(Material.FIRE);
    }

}//class
