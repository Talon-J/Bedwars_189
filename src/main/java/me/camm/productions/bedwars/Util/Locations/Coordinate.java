package me.camm.productions.bedwars.Util.Locations;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class Coordinate
{
    private double x,y,z, yaw;

    public Coordinate(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
    }

    public Coordinate(double x, double y, double z, double yaw)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }

    public Coordinate(Location loc)
    {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
    }

    public Coordinate(double[] values)
    {
        if (values==null) {
            defaultInstantiation();
            return;
        }

        if (values.length!=4&&values.length!=3)
            defaultInstantiation();

        this.x = values[0];
        this.y = values[1];
        this.z = values[2];

        try
        {
            this.yaw = values[3];
        }
        catch (IndexOutOfBoundsException e)
        {
            this.yaw  = 0;
        }
    }

    private void defaultInstantiation()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0;
    }


    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public double getYaw()
    {
        return yaw;
    }

    public double[] getCoordinates()
    {

        return new double[]{x,y,z,yaw};
    }

    public Location getAsLocation(World world)
    {
        return new Location(world, x, y, z,(float)yaw, 0);
    }

    public void registerBlock(World world, String type, Plugin plugin)
    {
        world.getBlockAt((int)x,(int)y,(int)z).setMetadata(type,new FixedMetadataValue(plugin,1));
    }

    //Debug method.
    public void print()
    {
         System.out.println("[DEBUG] Coords: "+x+"||"+y+"||"+z);
    }


    public SoakBoundary toBoundaryPoint()
    {
        return new SoakBoundary(x,x,y,y,z,z);
    }

}
