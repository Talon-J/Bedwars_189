package me.camm.productions.bedwars.Util.Locations;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class PointWall extends SoakBoundary
{
    public PointWall(double[] bounds)
    {
        super(bounds);
    }

    public PointWall(int x1, int x2, int y1, int y2, int z1, int z2)
    {
        super(x1, x2, y1, y2, z1, z2);
    }

    private void verify()
    {
        double[] values = getValues();
        for (int slot=0;slot<values.length-1;slot++)
        {
            double pos1 = values[slot];
            double pos2 = values[slot+1];

            if (pos1==pos2)
                return;
        }
        collapse(WallFace.DOWN);
    }

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