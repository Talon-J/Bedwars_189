package me.camm.productions.bedwars.Explosions.Vectors;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public class VelocityVector extends GameVector
{
   private double distanceMagnitude;
   private Entity affected;
   private Vector finalVelocity;

    public VelocityVector(EntityExplodeEvent event, Entity affected) {
        origin = event.getLocation().toVector();
        Vector affectedVec = affected.getLocation().toVector();

        double x = affectedVec.getX() - origin.getX();
        double y = affectedVec.getY() - origin.getY();
        double z = affectedVec.getZ() - origin.getZ();

        direction = new Vector(x,y,z);
        this.distanceMagnitude = direction.length();
        this.affected = affected;

    }

    private void calculate()
    {

    }

    public void impart()
    {
        affected.setVelocity(affected.getVelocity().add(finalVelocity));
    }




}
