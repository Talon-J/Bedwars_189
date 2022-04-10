package me.camm.productions.bedwars.Util.Locations.Boundaries;


public abstract class Boundary<T extends Number> {
    protected T x1, x2, y1, y2, z1, z2;
    protected abstract void analyze();
    protected abstract void reArrange();
    protected abstract void dissectArray();
    protected abstract T[] reset();

}
