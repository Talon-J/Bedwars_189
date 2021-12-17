package me.camm.productions.bedwars.Explosions.Vectors;

import org.bukkit.World;
import org.bukkit.util.Vector;

public abstract class GameVector
{
    protected Vector direction;
    protected Vector origin;
    protected World world;


    public Vector getPosition(double blocks) //Should be fixed now...I think?
    {
        return direction.clone().multiply(blocks).add(origin.clone());
    }

}
