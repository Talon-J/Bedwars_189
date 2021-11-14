package me.camm.productions.bedwars.Arena.Players.Managers;

import me.camm.productions.bedwars.Items.SectionInventories.Inventories.*;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ISectionInventory;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/*
This class manages inventory navigation for players in the game by bringing them to different
inventories.
 */
public class PlayerInventoryManager
{
    private final boolean isInflated;



    //These inventories change for each player.
    private final ArmorSectionInventory armorSection;
    private final QuickBuySection quickBuy;
    private final ToolsSectionInventory toolsSection;

    //These inventories are universal for every player and thus don't need to be changed.
    //find a way (if possible) to do this.
    private  BlockSectionInventory blockSection;
    private  MeleeSectionInventory meleeSection;
    private  PotionSectionInventory potionSection;
    private  RangedSectionInventory rangedSection;
    private  UtilitySectionInventory utilitySection;


    public PlayerInventoryManager(boolean isInflated)
    {
       this(null,isInflated);
    }

    public PlayerInventoryManager(ArrayList<ItemSet> quickBuyConfiguration, boolean isInflated)
    {
        armorSection = new ArmorSectionInventory(isInflated);
        quickBuy = new QuickBuySection(isInflated,quickBuyConfiguration);
        toolsSection = new ToolsSectionInventory(isInflated);
        blockSection = new BlockSectionInventory(isInflated);
        meleeSection = new MeleeSectionInventory(isInflated);
        potionSection = new PotionSectionInventory(isInflated);
        rangedSection = new RangedSectionInventory(isInflated);
        utilitySection = new UtilitySectionInventory(isInflated);
        this.isInflated = isInflated;
    }



    //Maybe there is a better way at doing this???
    public ISectionInventory isSectionInventory(Inventory inv)
    {
       if (armorSection.getInventory().equals(inv))
           return armorSection;
       else if (quickBuy.getInventory().equals(inv))
           return quickBuy;
       else if (toolsSection.getInventory().equals(inv))
           return toolsSection;
       else if (blockSection.getInventory().equals(inv))
           return blockSection;
       else if (meleeSection.getInventory().equals(inv))
           return meleeSection;
       else if (potionSection.getInventory().equals(inv))
           return potionSection;
       else if (rangedSection.getInventory().equals(inv))
           return rangedSection;
       else if (utilitySection.getInventory().equals(inv))
           return utilitySection;
       else
        return null;
    }




    public ArmorSectionInventory getArmorSection() {
        return armorSection;
    }

    public QuickBuySection getQuickBuy() {
        return quickBuy;
    }

    public ToolsSectionInventory getToolsSection() {
        return toolsSection;
    }

    public boolean isInflated() {
        return isInflated;
    }

    public BlockSectionInventory getBlockSection() {
        return blockSection;
    }

    public MeleeSectionInventory getMeleeSection() {
        return meleeSection;
    }

    public PotionSectionInventory getPotionSection() {
        return potionSection;
    }

    public RangedSectionInventory getRangedSection() {
        return rangedSection;
    }

    public UtilitySectionInventory getUtilitySection() {
        return utilitySection;
    }
}
