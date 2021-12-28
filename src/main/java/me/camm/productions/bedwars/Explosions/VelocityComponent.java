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

        List<Entity> nearEntities = exploded.getNearbyEntities(exploded.getLocation().getX(), exploded.getLocation().getY(), exploded.getLocation().getZ());

        for (Entity e: nearEntities) //for all of the nearby entities to the explosion..
        {
            if (!VectorToolBox.isValidVelocityType(e))   //So if the entity can be affected by velocity
            continue;


                if ((exploded.getLocation().distance(e.getLocation()) <= 5) && isFireball)  //if the distance is <= 5 and is fireball
                    constructFireVector(exploded.getLocation(), e.getLocation(),e);

                // otherwise test if it is a tnt with a distance of less than or equal to 8 blocks...
                else if ((exploded.getLocation().distance(e.getLocation()) <= 8) && (!isFireball))
                    constructTNTVector(exploded.getLocation(), e.getLocation(), e);

        }//for nearby



    }//method








    /*
    What the old code is doing in terms of the vector construction:

    1 - get the distances from the entity to the location of the explosion
    2 - determine the angle that exists between the entity and the explosion location
    3 - use the distances in step 1 to determine the magnitude of the initial vector based on location
    4 - using a formula to alter the magnitude based on distance (So that it is proportional)
    5 - getting the final magnitudes of the horizontal, and vertical components
    6 - using those components to construct a Bukkit vector (Which actually is used to set the velocity of the entity)

    We need to refactor this why?
    - physics are not exact enough for the game
    - crude not really readible (But does look really smart though)
    - we basically just used trial and error. that's not good enough

     */

    public void constructFireVector(Location origin, Location destination, Entity target)/////////////////tnt
    {

        double distanceX, distanceY, distanceZ;
        boolean xNegative = false, yNegative = false, zNegative = false;
        boolean horizontalValid = true;

        double xComponent, yComponent, zComponent, horizontalComponent;
        boolean deconstruct = true;

        double horizontalAngle, verticalAngle;
        double horizontalDistance, totalDistance;

        //getting the distances
        distanceX = constructDistance(origin.getX(), destination.getX());
        distanceY = constructDistance(origin.getY(), destination.getY() + 0.5);  //Account for hitbox [0.5]
        distanceZ = constructDistance(origin.getZ(), destination.getZ());


        //if any are negative
        if (distanceX < 0)  //Adjacent [horizontal]
        {
            distanceX = Math.abs(distanceX);
            xNegative = true;
        }

        if (distanceY < 0) {
            distanceY = Math.abs(distanceY);  //tangent
            yNegative = true;
        }

        if (distanceZ < 0) //Opposite [horizontal]
        {
            distanceZ = Math.abs(distanceZ);
            zNegative = true;
        }

        /*
                            /  |
                           /   | (z axis)
                          /    |
           hypotenuse    /     |  opposite
   (horizontal axis)    /      |
                       /       |
                      /        |
                     /---------\
                      adjacent  \
                  (x axis)       \ (y axis)
                                  \ tangent

         */

        //horizontal section
        if (distanceX != 0 && distanceZ != 0)  //if the distance is not 0 on the horizontal axis
        {
            //basically the angle that the entity should blast off in
            horizontalAngle = Math.toDegrees(Math.atan(distanceZ / distanceX));
            horizontalDistance = Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));
        }
        else  //if there is at least zero in the horizontal axis
        {
            if (distanceX == 0 && distanceZ == 0)
            {
                horizontalDistance = 0;  //the entity should travel directly up
                horizontalValid = false;  //so we know not to use trig to find the ratios
                horizontalAngle = 0;
            }
            else  //if the only 1 distance is 0, the other is not [travel in 1 direction,but not the other]
            {
                if (distanceX == 0) //Adjacent [horizontal]
                {
                    horizontalAngle = 90;  // because cos90 = 0, but sin90 = 1
                    horizontalDistance = distanceZ;
                } else  //if distanceX is not 0, then distanceZ is
                {
                    horizontalDistance = distanceX;
                    horizontalAngle = 0;  // b/c cos0 = 1, but sin0  = 0
                }

            }
        }

        //vertical
        if (distanceY != 0 && horizontalDistance != 0)  //if the vertical and horizontal distances are not 0
        {
            verticalAngle = Math.toDegrees(Math.atan(distanceY / horizontalDistance));

            totalDistance = Math.sqrt((distanceY * distanceY) + (horizontalDistance * horizontalDistance));  //pytha. theorem
        } else  //at least 1 is 0
        {
            if (distanceY == 0 && horizontalDistance == 0)  //if both are 0
            {
                //there is no knockback in this case.
                totalDistance = 0;
                verticalAngle = 0;
                horizontalAngle = 0;
                deconstruct = false;  //we should not deconstruct the vector. It is fine as is.

            } else  //just 1 is 0
            {
                if (distanceY == 0)  //if the y component is 0     --> hsin90 = 0
                {
                    // System.out.println("Y distance is 0");
                    verticalAngle = 0;
                    totalDistance = horizontalDistance;   //there is no vertical component
                } else  //if the y distance not 0, then the h distance is
                {
                    //  System.out.println("Case 2: Y distance not 0");
                    verticalAngle = 90; //hcos0 = 1
                    totalDistance = distanceY;
                }
            }
        }


        if (deconstruct)  //if we should deconstruct the vector [deconstruct is true] Since it won't mess with trig
        {
            /////////////////////////////////////////////////
            //FORMULA
            ////////////////////////////////////////////////

            //This is just a formula that makes a slope starting from roughly (0,2) to (10,0)
            //Use desmos to see the graph of the function.
            //Yeah no, this was just trial and error. Not good.
            //Although, worth holding on to though.
            totalDistance = 1.65 / ((0.01 * Math.pow(totalDistance, (0.5 * (Math.pow(totalDistance, 0.5)) + 2))) + 1);


            yComponent = ((totalDistance * (Math.sin(Math.toRadians(verticalAngle)))));

            double placeHold;

            if (horizontalValid)  //if the velocity is not directly upwards
            {
                horizontalComponent = totalDistance * (Math.cos(Math.toRadians(verticalAngle)));
                xComponent = (horizontalComponent * (Math.cos(Math.toRadians(horizontalAngle))));
                zComponent = (horizontalComponent * (Math.sin(Math.toRadians(horizontalAngle))));

                //Alter the velocities, make vertical more substantial
                //Magnitude shift.
                /*
                Basically from some simple measurements, we found that a player fireball jumping
                launched 12 blocks in the air, while the horizontal distance was less than that.
                so that's why we are altering magnitudes.

                This...isn't really true physics, functions more like it.


                 */
                placeHold = xComponent;  //placehold

                xComponent *= 0.65;
                yComponent = yComponent + (placeHold - xComponent);  //magnitude [Take the recipricol]

                placeHold = zComponent;
                zComponent *= 0.65;
                yComponent = yComponent + (placeHold - zComponent);  //magnitude


                if (yNegative)
                    yComponent *= -1;

                if (xNegative)
                    xComponent *= -1;

                if (zNegative)
                    zComponent *= -1;


                impartVelocity(xComponent, yComponent, zComponent, target);
            }
            else
            {
                if (yNegative)
                    yComponent *= -1;

                 //the only knockback is upwards.
                impartVelocity(0, yComponent, 0, target);
            }
        } else {

            //there is no knockback in this case
            impartVelocity(0, 0, 0, target);

        }
    }


    public void constructTNTVector(Location origin, Location destination, Entity target) /////////////////tnt
    {
        //System.out.println("Using tnt velocity formula");
        double distanceX, distanceY, distanceZ;
        boolean xNegative = false, yNegative = false, zNegative = false;
        boolean horizontalValid = true;

        double xComponent, yComponent, zComponent, horizontalComponent;
        boolean deconstruct = true;

        double horizontalAngle, verticalAngle;
        double horizontalDistance, totalDistance;

        //getting the distances
        distanceX = constructDistance(origin.getX(), destination.getX());
        distanceY = constructDistance(origin.getY(), destination.getY() + 0.5);  //Account for hitbox [0.5]
        distanceZ = constructDistance(origin.getZ(), destination.getZ());


        //if any are negative
        if (distanceX < 0)  //Adjacent [horizontal]
        {
            distanceX = Math.abs(distanceX);
            xNegative = true;
        }

        if (distanceY < 0) {
            distanceY = Math.abs(distanceY);  //tangent
            yNegative = true;
        }

        if (distanceZ < 0) //Opposite [horizontal]
        {
            distanceZ = Math.abs(distanceZ);
            zNegative = true;
        }

        //horizontal
        if (distanceX != 0 && distanceZ != 0)  //if both are not 0
        {
            horizontalAngle = Math.toDegrees(Math.atan(distanceZ / distanceX));
            horizontalDistance = Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));
        } else  //if there is at least zero
        {
            if (distanceX == 0 && distanceZ == 0) {
                horizontalDistance = 0;  //the entity should travel directly up
                horizontalValid = false;  //so we know not to use trig to find the ratios
                horizontalAngle = 0;
            } else  //if the 2 distances are not 0, but 1 is [travel in 1 direction,but not the other]
            {
                if (distanceX == 0) //Adjacent [horizontal]
                {
                    horizontalAngle = 90;  // b/c cos90 = 0, but sin90 = 1
                    horizontalDistance = distanceZ;
                } else  //if distanceX is not 0, then distanceZ is
                {
                    horizontalDistance = distanceX;
                    horizontalAngle = 0;  // b/c cos0 = 1, but sin0  = 0
                }

            }
        }

        //vertical
        if (distanceY != 0 && horizontalDistance != 0)  //if the vertical and horizontal distances are not 0
        {
            verticalAngle = Math.toDegrees(Math.atan(distanceY / horizontalDistance));
            totalDistance = Math.sqrt((distanceY * distanceY) + (horizontalDistance * horizontalDistance));  //pytha. theorem
        } else  //at least 1 is 0
        {
            if (distanceY == 0 && horizontalDistance == 0)  //if both are 0
            {
                //there is no knockback in this case.
                totalDistance = 0;
                verticalAngle = 0;
                horizontalAngle = 0;
                deconstruct = false;  //we should not deconstruct the vector. It is fine as is.

            } else  //just 1 is 0
            {
                if (distanceY == 0)  //if the y component is 0     --> hsin90 = 0
                {
                    verticalAngle = 90;
                    totalDistance = horizontalDistance;   //there is no vertical component
                } else  //if the y distance not 0, then the h distance is
                {
                    verticalAngle = 0; //hcos0 = 1
                    totalDistance = distanceY;
                }
            }
        }

        if (deconstruct)  //if we should deconstruct the vector [deconstruct is true]
        {
            /////////////////////////////////////////////////
            //FORMULA
            ////////////////////////////////////////////////
            totalDistance = 1.65 / ((0.04 * Math.pow(totalDistance, (0.4 * (Math.pow(totalDistance, 0.5)) + 2))) + 1);

            // endX = y*(Math.cos(Math.toRadians(angle)));
            yComponent = ((totalDistance * (Math.sin(Math.toRadians(verticalAngle)))));

            double placeHold;

            if (horizontalValid)  //if the velocity is not directly upwards
            {
                horizontalComponent = totalDistance * (Math.cos(Math.toRadians(verticalAngle)));
                xComponent = (horizontalComponent * (Math.cos(Math.toRadians(horizontalAngle))));
                zComponent = (horizontalComponent * (Math.sin(Math.toRadians(horizontalAngle))));

                //Alter the velocities, make vertical more substantial
                //Magnitude shift.
                placeHold = xComponent;  //placehold

                xComponent *= 0.65;
                yComponent = yComponent + (placeHold - xComponent);  //magnitude [Take the recipricol]
                xComponent = placeHold * 0.8;


                placeHold = zComponent;
                zComponent *= 0.65;
                yComponent = yComponent + (placeHold - zComponent);  //magnitude
                zComponent = placeHold * 0.8;

                if (yNegative)
                    yComponent *= -1;

                if (xNegative)
                    xComponent *= -1;

                if (zNegative)
                    zComponent *= -1;

                impartVelocity(xComponent, yComponent, zComponent, target);
            } else {
                if (yNegative)
                    yComponent *= -1;

                impartVelocity(0, yComponent, 0, target);
            }
        } else {
            impartVelocity(0, 0, 0, target);
        }

    }/////////////////tnt


    private void impartVelocity(double xComponent, double yComponent, double zComponent, Entity targeted)
    {
        Vector velocity = new Vector(xComponent,yComponent,zComponent);
        targeted.setVelocity(velocity);
        //We should add the given velocity to the velocity that they already have.
        /*
        Please confirm. further testing required.
        (try falling some distance while tnt jumping? Cause some people are saying that it adds instead of sets
        - explains some of the movements they are able to do at least in Hypixel.)
         */
    }


    public double constructDistance(double origin, double destination)
    {
        return destination - origin;
    }

}

