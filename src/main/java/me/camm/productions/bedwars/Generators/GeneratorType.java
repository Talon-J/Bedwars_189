package me.camm.productions.bedwars.Generators;

import org.bukkit.Material;


/**
 * @author CAMM
 * Enum for types of generators
 */
public enum GeneratorType
{

    DIAMOND(Material.DIAMOND_BLOCK,Material.DIAMOND,"Diamond"),
    EMERALD(Material.EMERALD_BLOCK,Material.EMERALD,"Emerald");

    private final Material spinningBlockMaterial;
    private final Material productMaterial;
    private final String simpleName;

    GeneratorType(Material spinningBlockMaterial, Material productMaterial, String simpleName) {
        this.spinningBlockMaterial = spinningBlockMaterial;
        this.productMaterial = productMaterial;
        this.simpleName = simpleName;
    }

    public Material getSpinningBlockMaterial() {
        return spinningBlockMaterial;
    }

    public Material getProductMaterial() {
        return productMaterial;
    }

    public String getSimpleName() {
        return simpleName;
    }
}
