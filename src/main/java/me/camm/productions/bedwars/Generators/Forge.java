package me.camm.productions.bedwars.Generators;

import me.camm.productions.bedwars.Arena.Teams.TeamColors;
import me.camm.productions.bedwars.Files.FileKeywords.TeamFileKeywords;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

public class Forge implements Runnable
{
    private final String type;
    private final World world;
    private final Location location;
    private final Plugin plugin;

    private final String color;

    private final long initialTime;
    private volatile long spawnTime;


    private volatile int tier;
    private volatile boolean isAlive;
    private int next;


    private final Random spawningTimeRand;
    private final Random spawningChance;

    private static final double PICKUP_DISTANCE;
    private static final int MAX_GOLD;
    private static final int MAX_IRON;

    static {
        PICKUP_DISTANCE = 1.5;
        MAX_GOLD = 16;
        MAX_IRON = 48;

    }

    /*

t1 = +50%
2: 100
3: spawn emerald
4: 200

--> 350%

Supposedly, /n = the percentage we need.
[Try set forge initial time to 1.5 secs.]
     */

//normal
  public Forge(double x, double y, double z, World world, TeamColors color, long initialTime, Plugin plugin)  //construct
  {
     this.location = new Location(world, x,y,z);
      this.color = color.getName();
      this.initialTime = initialTime;
      this.world = world;
      this.tier = 0;
      this.next = 0;
      this.plugin = plugin;
      this.type = TeamFileKeywords.FORGE_SPAWN.getKey();

      this.spawnTime = initialTime;
      this.isAlive = true;


      Chunk chunk = world.getChunkAt(location);
      if (!chunk.isLoaded())
          chunk.load();

      spawningChance = new Random();
      spawningTimeRand = new Random();

  }

  public Location getForgeLocation()
  {
      return location;
  }

  public double getDistance()
  {
      return PICKUP_DISTANCE;
  }

  public synchronized void disableForge()
  {
      this.isAlive = false;
  }

  public String getColor()
  {
      return color;
  }


    public synchronized void setTier(int newTier)  //you would check for the color first externally
    {
        this.tier = newTier;

        switch (tier)
        {
            case 1:
                spawnTime = (long)(initialTime/1.5);
                break;

            case 2:
                spawnTime = (long)(initialTime/2.5);
                break;

            case 4:
                spawnTime = (long)(initialTime/3.5);
                break;
        }
    }


//normal method
    public synchronized long randomize()
    {
       return (long)(spawnTime*(spawningTimeRand.nextDouble()*1.5));
    }

    public synchronized void spawnItem()
    {
       int chance = spawningChance.nextInt(101);  // 0 --> 100
        int freedom = verifyCount();
        Material spawnMaterial = null;
        next++;


        if (freedom<0) {
            return;
        }

            switch (tier) {
                case 0:
                case 1:
                case 2:
                    if (chance>=80)
                    {

                        if (freedom>0)  //freedom is either 1 or 2
                            spawnMaterial = Material.GOLD_INGOT;
                        else
                            spawnMaterial = Material.IRON_INGOT;  //freedom is 0
                    }
                    else if (freedom==0||freedom==2)
                    {
                        spawnMaterial = Material.IRON_INGOT;
                    }


                    break;

                case 3:
                case 4:
                    if (chance>=80)
                    {
                        if (freedom>0) //if freedom is 1 or 2
                        {
                            spawnMaterial = Material.GOLD_INGOT;
                        }
                        else if (next>=120)  //freedom = 0
                        {
                            next = 0;
                            spawnMaterial = Material.EMERALD;
                        }
                        else
                            spawnMaterial = Material.IRON_INGOT;
                    }
                    break;
            }

            if (spawnMaterial!=null)
                drop(spawnMaterial);

     //   plugin.getServer().broadcastMessage("[DEBUG:] color: "+color+" Freedom: "+freedom);

        }


        private void drop(Material mat)
        {
            if (!isAlive || !plugin.isEnabled())
                return;

            new BukkitRunnable()
            {
                public void run()
                {
                    Item spawned = world.dropItem(location, new ItemStack(mat, 1));
                    spawned.setVelocity(new Vector(0,0,0));
                    spawned.setMetadata(type,new FixedMetadataValue(plugin,1));
                    cancel();
                }
            }.runTask(plugin);

        }


        private int verifyCount()
        {
            int goldCount = 0;
            int ironCount = 0;
            Collection<Entity> nearby = world.getNearbyEntities(location, PICKUP_DISTANCE, PICKUP_DISTANCE, PICKUP_DISTANCE);
            for (Entity entity: nearby)
            {

                if (!(entity instanceof Item))
                    continue;


                    Item item = (Item)entity;
                    if (!item.hasMetadata(type))
                        continue;

                        ItemStack stack = item.getItemStack();
                        Material mat = stack.getType();

                        switch (mat)
                        {
                            case GOLD_INGOT:
                            goldCount+= stack.getAmount();
                                break;
                            case IRON_INGOT:
                               ironCount += stack.getAmount();
                        }

            }


         if (goldCount>=MAX_GOLD) //if gold is invalid
             return ironCount >= MAX_IRON?SpawningFreedom.NO_SPAWNING.getFreedom():SpawningFreedom.ONLY_IRON.getFreedom();
         else
             return ironCount >= MAX_IRON? SpawningFreedom.ONLY_GOLD.getFreedom():SpawningFreedom.FULL_SPAWNING.getFreedom();
        }


    @Override
    public void run()
    {
        while (isAlive)
        {
            try
            {
                Thread.sleep(randomize());
                spawnItem();
            }
            catch (InterruptedException e)
            {
               disableForge();
            }
        }

    }

    private enum SpawningFreedom {
      NO_SPAWNING(-1),  //Don't spawn anything
        ONLY_IRON(0),
        ONLY_GOLD(1),
        FULL_SPAWNING(2);  //spawn

      int freedom;

      SpawningFreedom(int freedom)
      {
          this.freedom = freedom;
      }

      int getFreedom()
      {
          return freedom;
      }


    }
}
