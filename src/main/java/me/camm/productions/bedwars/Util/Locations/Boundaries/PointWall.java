package me.camm.productions.bedwars.Util.Locations.Boundaries;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class PointWall extends SoakBoundary
{
    public PointWall(double[] bounds)
    {
        super(bounds);
    }

    /*
    We're basically just iterating through a series of points here in a cube [the surface area basically]
     */
    public ArrayList<Location> iterate(double precision, World world)
    {
        double[] values = getValues();
        if (values==null||values.length !=6)
            return new ArrayList<>();

        ArrayList<Location> points = new ArrayList<>();

        for (double x1=values[0];x1<=values[1];x1 += precision)
        {
            for (double y1=values[2];y1<=values[3];y1 += precision)
            {
                for (double z1=values[4]; z1 <= values[5]; z1 += precision)
                    points.add(new Location(world, x1,y1,z1));
            }
        }
        return points;
    }

}