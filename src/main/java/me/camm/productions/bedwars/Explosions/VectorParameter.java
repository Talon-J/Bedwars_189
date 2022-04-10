package me.camm.productions.bedwars.Explosions;


/**
 *
 * @author CAMM
 * Enumeration for parameters that a vector can use
 */
public enum VectorParameter
{

    DROP_CHANCE(80),
    BLOCK_ADDITION(0.3),

    CEILING(101),// the ceiling value for a random number from n to 100

    BLOCK_DIVISOR(3);

    private final double value;

    VectorParameter(double value)
    {
        this.value = value;
    }

    public double getValue()
    {
        return value;
    }
}
