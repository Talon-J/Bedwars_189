package me.camm.productions.bedwars.Arena.Players.Managers;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.SectionInventories.Inventories.*;
import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;
import me.camm.productions.bedwars.Util.DataSets.ShopItemSet;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author CAMM
This class manages inventory navigation for players in the game by bringing them to different
inventories.
 */
public class PlayerInventoryManager
{
    private final boolean isInflated;
    private BattlePlayer owner;



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

    private final List<ShopInventorySetter> inventories;


    public PlayerInventoryManager(boolean isInflated)
    {
       this(null,isInflated);
    }

    public void setOwner(BattlePlayer player)
    {
        this.owner = player;
    }

    public PlayerInventoryManager(ArrayList<ShopItemSet> quickBuyConfiguration, boolean isInflated)
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
        //note: immutable. Convert to arraylist w/ new ArrayList<>(inventories)
        inventories = Arrays.asList(armorSection,quickBuy,toolsSection,
                blockSection,meleeSection,potionSection,rangedSection,utilitySection);

    }


    public Inventory isSectionInventory(Inventory inv)
    {
        if (inv == null)
            return null;

        //todo replace this with a set lookup instead of an arraylist iteration
      for (ShopInventorySetter i: inventories)
      {
          if (i.equals(inv))
              return i;
      }
      return null;
    }

    //the armor section, quickbuy, and the tools sections are the only sections that
    //can dynamically change
    public void replaceItem(ShopItem toReplace, ShopItem replacement)
    {
        searchAndReplace(armorSection,toReplace, replacement);
        searchAndReplace(quickBuy,toReplace, replacement);
        searchAndReplace(toolsSection,toReplace, replacement);
    }

    /*
    Searches and replaces all items that match the similarity of the toReplace item with
    the replacement item
     */
    private void searchAndReplace(Inventory inv, ShopItem toReplace, ShopItem replacement)
    {
        if (owner == null)
            return;


        ItemStack toBeSet = ItemHelper.toDisplayItem(replacement, isInflated);
        ItemStack toBeReplaced = ItemHelper.toDisplayItem(toReplace, isInflated);

        for (int i = 0;i< inv.getSize();i++)
        {
            ItemStack residing = inv.getItem(i);
            if (ItemHelper.isItemInvalid(residing))
                continue;

            //Enchantments can change the name, so don't use displayName().equalsIgnoreCase()...
            if (residing.isSimilar(toBeReplaced)) {
                inv.setItem(i, toBeSet);
            }
        }
    }



  /////////////////////////
    //getters
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
