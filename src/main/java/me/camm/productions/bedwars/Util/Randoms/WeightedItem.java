package me.camm.productions.bedwars.Util.Randoms;

//@Author CAMM
public class WeightedItem<T>
{
    private final T item;
    private double weight;

    public WeightedItem(T item, double weight){
        this.item = item;
        this.weight = weight;
    }

    public T getItem() {
        return item;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

}