package me.camm.productions.bedwars.Generators;

import me.camm.productions.bedwars.Arena.Teams.TeamColors;
import me.camm.productions.bedwars.Files.FileKeywords.TeamFileKeywords;
import me.camm.productions.bedwars.Util.Randoms.WeightedItem;
import me.camm.productions.bedwars.Util.Randoms.WeightedRandom;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
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

    private final Random spawningTimeRand;

    private static final double PICKUP_DISTANCE;
    private static final int MAX_GOLD;
    private static final int MAX_IRON;

    private final WeightedRandom<WeightedItem<Material>> spawningRandom;
    private final WeightedItem<Material> emeraldChance;
    private final WeightedItem<Material> goldChance;

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
      this.plugin = plugin;
      this.type = TeamFileKeywords.FORGE_SPAWN.getKey();

      this.spawnTime = initialTime;
      this.isAlive = true;


      Chunk chunk = world.getChunkAt(location);
      if (!chunk.isLoaded())
          chunk.load();

      spawningTimeRand = new Random();
      emeraldChance = new WeightedItem<>(Material.EMERALD,0);
      goldChance = new WeightedItem<>(Material.GOLD_INGOT,0);

      ArrayList<WeightedItem<Material>> materials = new ArrayList<>();
      materials.add(new WeightedItem<>(Material.IRON_INGOT,0.8));
      materials.add(goldChance);
      materials.add(emeraldChance);
      spawningRandom = new WeightedRandom<>(materials);

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

            case 3:
                emeraldChance.setWeight(0.005);
                break;

            case 4:
                spawnTime = (long)(initialTime/3.5);
                emeraldChance.setWeight(0.005);
                break;
        }
    }

    public long randomize()
    {
       return (long)(spawnTime*(spawningTimeRand.nextDouble()*1.5));
    }

    public synchronized void spawnItem()
    {
        int freedom = verifyCount();
        Material mat;

        switch (freedom) {
            case -1:
                mat = null;
                break;

            case 0:
                  mat = Material.IRON_INGOT;
                break;

            case 1:
                mat = Material.GOLD_INGOT;
                break;

            default:
                 mat = spawningRandom.getNext().getItem();

        }

        if (mat == null)
            return;

        drop(mat);
    }


        private void drop(Material mat)
        {
            if (!isAlive || !plugin.isEnabled())
                return;

            goldChance.setWeight(Math.min(goldChance.getWeight()+0.01, 0.2));

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
            try {
                int goldCount = 0;
                int ironCount = 0;

                Collection<Entity> nearby = world.getNearbyEntities(location, PICKUP_DISTANCE, PICKUP_DISTANCE, PICKUP_DISTANCE);
                for (Entity entity : nearby) {

                    if (!(entity instanceof Item))
                        continue;


                    Item item = (Item) entity;
                    if (!item.hasMetadata(type))
                        continue;

                    ItemStack stack = item.getItemStack();
                    Material mat = stack.getType();

                    switch (mat) {
                        case GOLD_INGOT:
                            goldCount += stack.getAmount();
                            break;
                        case IRON_INGOT:
                            ironCount += stack.getAmount();
                    }

                }


                if (goldCount >= MAX_GOLD) //if gold is invalid
                    return ironCount >= MAX_IRON ? SpawningFreedom.NO_SPAWNING.getFreedom() : SpawningFreedom.ONLY_IRON.getFreedom();
                else
                    return ironCount >= MAX_IRON ? SpawningFreedom.ONLY_GOLD.getFreedom() : SpawningFreedom.FULL_SPAWNING.getFreedom();
            }
            catch (NoSuchElementException e)  //this is thrown by getNearByentities() in world.
            {
                e.printStackTrace();
                plugin.getServer().broadcastMessage("[WARN] - Forge for team "+color+" encountered an exception. It might not work as intended.");
                return -1;
            }
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

    public synchronized int getTier(){
      return tier;
    }

    private enum SpawningFreedom {
      NO_SPAWNING(-1),  //Don't spawn anything
        ONLY_IRON(0),
        ONLY_GOLD(1),
        FULL_SPAWNING(2);  //spawn everything

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
