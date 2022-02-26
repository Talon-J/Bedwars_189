package me.camm.productions.bedwars.Explosions;


import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class VelocityComponent {
    private final EntityExplodeEvent event;
    private boolean isFireball;

    public VelocityComponent(EntityExplodeEvent event)
    {
        this.event = event;
        EntityType type = event.getEntityType();
        switch (type)
        {
            case FIREBALL:
            case SMALL_FIREBALL:
                isFireball = true;
        }
    }


    public void applyVelocity()  //unfinished. Need to refactor since physics is not entirely accurate. Note added 2021-11m-16d
    {

        Entity exploded = this.event.getEntity();
        Location origin = exploded.getLocation();

        List<Entity> nearEntities = exploded.getNearbyEntities(exploded.getLocation().getX(), exploded.getLocation().getY(), exploded.getLocation().getZ());

        for (Entity e: nearEntities) //for all of the nearby entities to the explosion..
        {
            if (!VectorToolBox.isValidVelocityType(e))   //So if the entity can be affected by velocity
               continue;
            construct(origin, e.getLocation(),e);


        }//for nearby



    }//method


    public void construct(Location origin, Location destination, Entity target){

        double deltaX, deltaY, deltaZ;
        deltaX = destination.getX() - origin.getX();
        deltaY = (destination.getY() + (isFireball ? 1: 0.5)) - origin.getY();
        deltaZ = destination.getZ() - origin.getZ();

        double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        double percentX, percentY, percentZ;
        percentX = deltaX / magnitude;
        percentY = deltaY / magnitude;
        percentZ = deltaZ / magnitude;


        if (isFireball)
            magnitude = -0.362*magnitude+1.9;
        else
            magnitude = -0.225*magnitude+1.92;

        magnitude = Math.max(0,magnitude);
        if (magnitude == 0)
            return;


        deltaX = magnitude * percentX;
        deltaY = magnitude * percentY;
        deltaZ = magnitude * percentZ;

        deltaX *= 1.2;
        deltaZ *= 1.2;

        System.out.println("==========================================");
        System.out.println("Components: "+deltaX+" "+deltaY+" "+deltaZ);
        System.out.println(deltaX);
        System.out.println(deltaY);
        System.out.println(deltaZ);


        impartVelocity(deltaX, deltaY, deltaZ,target);

    }




    private void impartVelocity(double xComponent, double yComponent, double zComponent, Entity targeted)
    {
        Vector velocity = new Vector(xComponent,yComponent,zComponent);
        System.out.println("Targ Original velocity:"+targeted.getVelocity().getX()+" "+targeted.getVelocity().getY()+" "+targeted.getVelocity().getZ());
        targeted.setVelocity(targeted.getVelocity().clone().add(velocity));
    }

}

