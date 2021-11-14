package me.camm.productions.bedwars.Explosions;

public enum VectorParameter
{

    DROP_CHANCE(80),
    BLOCK_ADDITION(0.3),
    CEILING(101),
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
