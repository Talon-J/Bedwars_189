package me.camm.productions.bedwars.Structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static me.camm.productions.bedwars.Structures.TowerParameter.*;
import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.*;

public class LineBuilder
{
    private final int xMultiplier;
    private final int zMultiplier;
    private final byte color;
    private final World world;
    private final Plugin plugin;







    public LineBuilder(int xMultiplier, int zMultiplier, byte color, World world, Plugin plugin)
    {
        this.plugin = plugin;
        this.xMultiplier = xMultiplier;
        this.zMultiplier = zMultiplier;
        this.world = world;
        this.color = color;
    }


//Draws a solid line along the x axis.
    public void drawSolidX(int length, Location loc, boolean skipFirst)
    {

        length = Math.abs(length);
        Location draw = loc.clone();
        final int finalLength = length;

        if (skipFirst)
            draw.add(xMultiplier, 0,0);

        new BukkitRunnable()
        {
            int currentLength = 0;
            public void run()
            {

                if (currentLength<finalLength)
                {
                    placeBlock(draw);
                    draw.add(xMultiplier,0,0);
                    currentLength++;
                }
                else
                    cancel();

            }
        }.runTaskTimer(plugin,0,PERIOD.getMeasurement());

    }


    //Draws a solid line along the z axis.
    public void drawSolidZ(int length, Location loc, boolean skipFirst)
    {

        length = Math.abs(length);
        Location draw = loc.clone();
        final int finalLength = length;

        if (skipFirst)
            draw.add(0,0,zMultiplier);

        new BukkitRunnable()
        {
            int currentLength = 0;

            public void run()
            {
                if (currentLength<finalLength)
                {
                    placeBlock(draw);
                    draw.add(0,0,zMultiplier);
                    currentLength++;
                }
                else
                    cancel();


            }
        }.runTaskTimer(plugin,0,PERIOD.getMeasurement());
    }

    //Draws a horizontal square with the x and z length parameters.
    public void drawRoundPerimeter(Location starting, int xLength, int zLength)
    {
        Location draw = starting.clone();

        drawSolidX(xLength,draw,true);  //square base for the battlements [1/2]
        drawSolidZ(zLength, draw, true);

        drawSolidX(xLength, draw.clone().add(0,0,(zLength+1)*zMultiplier),true);   //[2/2]
        drawSolidZ(zLength,draw.clone().add((xLength+1)*xMultiplier,0,0),true);

        Location battlementLocation = draw.clone().add(0,1,0);


        if (xLength>zLength)  //battlements
        {

            //1st half
            drawSegmentedX(battlementLocation.clone().add(xMultiplier,0,0), BATTLEMENT_SIZE.getMeasurement(), MAIN_BATTLEMENTS.getMeasurement());
            drawJumpedZ(SIDE_BATTLEMENTS.getMeasurement(),SIDE_BATTLEMENT_GAP.getMeasurement(),battlementLocation.clone().add(0,0,zMultiplier),false);



            drawSegmentedX(battlementLocation.clone().add(xMultiplier,0,PLATFORM_LENGTH.getMeasurement()*zMultiplier),BATTLEMENT_SIZE.getMeasurement(),MAIN_BATTLEMENTS.getMeasurement());
            drawJumpedZ(SIDE_BATTLEMENTS.getMeasurement(),SIDE_BATTLEMENT_GAP.getMeasurement(),battlementLocation.clone().add(LENGTH_WITH_BATTLEMENTS.getMeasurement()*xMultiplier,0,zMultiplier),false);


        }
        else
        {


            //1nd half
            drawJumpedX(SIDE_BATTLEMENTS.getMeasurement(),SIDE_BATTLEMENT_GAP.getMeasurement(),battlementLocation.clone().add(xMultiplier,0,0),false);
            drawSegmentedZ(battlementLocation.clone().add(0,0,zMultiplier),BATTLEMENT_SIZE.getMeasurement(),MAIN_BATTLEMENTS.getMeasurement());


            //segmented correct
            drawSegmentedZ(battlementLocation.clone().add(PLATFORM_LENGTH.getMeasurement()*xMultiplier,0,zMultiplier),BATTLEMENT_SIZE.getMeasurement(), MAIN_BATTLEMENTS.getMeasurement());
            drawJumpedX(SIDE_BATTLEMENTS.getMeasurement(),SIDE_BATTLEMENT_GAP.getMeasurement(),battlementLocation.clone().add(xMultiplier,0,LENGTH_WITH_BATTLEMENTS.getMeasurement()*zMultiplier),false);
        }




    }

// Draws the line with the hole in the tower where the ladder goes.
    public void drawHatch(Location start, boolean isXLarger)
    {
        Location draw = start.clone();

        final int end = 3;
        final int hatchRow = 1;

        new BukkitRunnable()
        {
            int rows = 0;


            public void run()
            {
                if (rows<PLATFORM_WIDTH.getMeasurement())
                {
                    switch (rows)
                    {
                        case 0:
                        case end:
                            Location primingLocation = draw.clone();

                            if (isXLarger)
                            {
                                primingLocation.add(-xMultiplier,0,0);
                                drawSolidX((PLATFORM_LENGTH.getMeasurement()+2),primingLocation,false);
                                draw.add(0,0,zMultiplier);
                            }
                            else
                            {
                                primingLocation.add(0,0,-zMultiplier);
                                drawSolidZ((PLATFORM_LENGTH.getMeasurement()+2),primingLocation,false);
                                draw.add(xMultiplier,0,0);
                            }
                            rows++;
                            break;

                        case hatchRow:
                            if (isXLarger)
                            {
                                drawSegmentedX(draw, 2, 2);
                                draw.add(0,0,zMultiplier);
                            }
                            else {
                                drawSegmentedZ(draw, 2, 2);
                                draw.add(xMultiplier,0,0);
                            }
                            rows ++;
                            break;


                        default:
                            if (isXLarger) //x is the length, z is the width
                            {
                                drawSolidX(PLATFORM_LENGTH.getMeasurement(),draw,false);
                                draw.add(0, 0, zMultiplier);
                            }
                            else
                            {
                                drawSolidZ(PLATFORM_LENGTH.getMeasurement(),draw,false);
                                draw.add(xMultiplier,0,0);
                            }
                            rows++;
                            break;
                    }
                }
            }
        }.runTaskTimer(plugin,0, PERIOD.getMeasurement());
    }

//Draws a line with segments spaced 1 block apart and with a specified length on the x axis.
    public void drawSegmentedX(Location loc, int segmentLength, int segments)
    {
        Location draw = loc.clone();

        final int finalSegments = segments;
        //final int finalLength = segmentLength;

        new BukkitRunnable()
        {
            int currentLength = 0;
            int currentSegments = 0;

            @Override
            public void run()
            {
                if (currentSegments<finalSegments)
                {
                    if (currentLength<segmentLength)
                    {
                         placeBlock(draw);
                        currentLength++;
                    }
                    else
                    {
                       // System.out.println("X segment");
                        currentLength = 0;
                        currentSegments++;
                    }
                    draw.add(xMultiplier, 0, 0);
                }
                else
                    cancel();


            }
        }.runTaskTimer(plugin, 0, PERIOD.getMeasurement());

    }

    public void drawSegmentedZ(Location loc, int segmentLength, int segments)
    {
        Location draw = loc.clone();

        final int finalSegments = segments;
        //final int finalLength = segmentLength;

        new BukkitRunnable()
        {
            int currentLength = 0;
            int currentSegments = 0;


            public void run()
            {
                if (currentSegments<finalSegments)
                {
                    if (currentLength<segmentLength)
                    {
                         placeBlock(draw);
                        currentLength++;
                    }
                    else
                    {
                     //   System.out.println("z segment");
                        currentLength = 0;
                        currentSegments++;
                    }
                    draw.add(0, 0, zMultiplier);
                }
                else
                    cancel();


              //  playSound(draw);
            }
        }.runTaskTimer(plugin, 0, PERIOD.getMeasurement());

    }


    public void drawJumpedX(int placeNumber, int period, Location loc, boolean skipOne)
    {

        Location draw = skipOne ? loc.clone().add(xMultiplier,0,0): loc.clone();

        new BukkitRunnable()
        {
            int placed = 0;
            int length = 0;

            public void run()
            {
                if (placed<placeNumber)
                {
                    length++;
                    if (length%period==0)
                    {
                        placeBlock(draw);
                        draw.add(xMultiplier*length, 0, 0);   //  [swapped pos w/ placeblock] [debug]
                        placed++;
                    }
                }
             //   playSound(draw);
            }
        }.runTaskTimer(plugin,0,PERIOD.getMeasurement());
    }



    public void drawJumpedZ(int placeNumber, int period, Location loc, boolean skipOne)
    {

        Location draw = skipOne ? loc.clone().add(0,0,zMultiplier): loc.clone();

        new BukkitRunnable()
        {
            int placed = 0;
            int length = 0;

            public void run()
            {
                if (placed<placeNumber)
                {
                    length++;
                    if (length%period==0)
                    {
                        placeBlock(draw);
                        draw.add(0, 0, length * zMultiplier);
                        placed++;
                    }
                }
             //   playSound(draw);
            }
        }.runTaskTimer(plugin,0,PERIOD.getMeasurement());


    }



    private byte faceToDirection(BlockFace face)
    {
        byte direction;

        switch (face)
        {
            case NORTH:
                direction = 3;
                break;


            case EAST:
                direction = 4;
                break;


            case WEST:
                direction = 5;
                break;

            default:
                direction = 0;

        }
        return direction;
    }



    @SuppressWarnings("deprecation")
    public void placeLadder(Location loc, BlockFace face)
    {
        new BukkitRunnable()
        {

            public void run()
            {

                Block block = loc.getBlock();
                if (block.getType()==Material.AIR)
                {
//ENTITY_ITEM_PICKUP  , ITEM_PICKUP

                    if (block.hasMetadata(ARENA.getData())&&    //if the loc is valid
                            !block.hasMetadata(GENERATOR.getData())&&
                            !block.hasMetadata(BASE.getData())
                    )
                    {
                       playSound(loc);
                        block.setType(Material.LADDER);
                        block.setData(faceToDirection(face));
                        removeData(block);
                    }
                }
            }
        }.runTaskLater(plugin, DELAY.getMeasurement());

    }

    @SuppressWarnings("deprecation")
    public void placeBlock(Location loc)
    {

        Block block = world.getBlockAt(loc);
        if (block.getType()==Material.AIR&&
                block.hasMetadata(ARENA.getData())&&
                !block.hasMetadata(GENERATOR.getData())&&
                !block.hasMetadata(BASE.getData())
        ) {
            playSound(loc);
            block.setType(Material.WOOL);
            block.setData(color);
            removeData(block);
        }
    }


    private void removeData(Block block)
    {
        block.removeMetadata(MAP.getData(),plugin);
        block.removeMetadata(BED.getData(),plugin);
        block.removeMetadata(CHEST.getData(),plugin);
    }

    private void playSound(Location loc)
    {
        Sound sound;

        try
        {
            sound = Sound.valueOf("ITEM_PICKUP");
        }
        catch (Exception e)
        {
            sound = Sound.valueOf("ENTITY_ITEM_PICKUP");
        }
        loc.getWorld().playSound(loc,sound,1,1);
    }





}
