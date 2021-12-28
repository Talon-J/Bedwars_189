package me.camm.productions.bedwars.Util.Locations.Boundaries;

public abstract class Boundary<T extends Number> {
    protected T x1, x2, y1, y2, z1, z2;
    // Note that if you wanted T[] bounds, you would need wrapper classes for the stringToolBox, etc.
    protected abstract void analyze();
    protected abstract void reArrange();
    protected abstract void dissectArray();
}
