package me.camm.productions.bedwars.Arena.Players.Managers;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.*;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ISectionInventory;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.InventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

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
    private static BlockSectionInventory blockSection;
    private static MeleeSectionInventory meleeSection;
    private static PotionSectionInventory potionSection;
    private static RangedSectionInventory rangedSection;
    private static UtilitySectionInventory utilitySection;


    public PlayerInventoryManager(boolean isInflated)
    {
       this(null,isInflated);
    }

    public PlayerInventoryManager(ArrayList<ItemSet> quickBuyConfiguration, boolean isInflated)
    {

        armorSection = new ArmorSectionInventory(isInflated);
        quickBuy = new QuickBuySection(isInflated,quickBuyConfiguration);
        toolsSection = new ToolsSectionInventory(isInflated);

        if (blockSection==null || meleeSection == null || potionSection == null || rangedSection == null || utilitySection == null) {
            blockSection = new BlockSectionInventory(isInflated);
            meleeSection = new MeleeSectionInventory(isInflated);
            potionSection = new PotionSectionInventory(isInflated);
            rangedSection = new RangedSectionInventory(isInflated);
            utilitySection = new UtilitySectionInventory(isInflated);
        }

        this.isInflated = isInflated;
    }



    //Maybe there is a better way at doing this???
    public boolean isSectionInventory(Inventory inv)
    {
      return inv instanceof InventorySetter;
    }

    public void replaceItem(GameItem toReplace, GameItem replacement)
    {
        searchAndReplace(armorSection,toReplace, replacement);
        searchAndReplace(quickBuy,toReplace, replacement);
        searchAndReplace(toolsSection,toReplace, replacement);
    }

    private void searchAndReplace(Inventory inv, GameItem toReplace, GameItem replacement)
    {
        ItemStack set = ItemHelper.toDisplayItem(replacement, isInflated);

        for (int i = 0;i< inv.getSize();i++)
        {
            ItemStack stack = inv.getItem(i);
            if (stack == null)
                continue;

            ItemMeta meta = stack.getItemMeta();
            if (meta == null)
                continue;

            if (meta.getDisplayName().equalsIgnoreCase(toReplace.name) && (stack.getType() == toReplace.sellMaterial))
                inv.setItem(i,set);
        }

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
