package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;

import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;

import org.bukkit.inventory.ItemStack;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.TEAM_BUY;


public class TeamBuyInventory extends CraftInventoryCustom
{

    public TeamBuyInventory() {
        super(null,54,TEAM_BUY.getTitle());
        setTemplateItems();
    }

    private void setTemplateItems()
    {
        TeamInventoryConfig[] config = TeamInventoryConfig.values();
        for (TeamInventoryConfig item: config)
        {
           ItemStack display = ItemHelper.toTeamDisplayItem(item.getItems(),1); //1 is put there for the initial description to be orange.
            for (int slot: item.getSlots())
                setItem(slot, display);
        }
    }


    public void setItem(TeamInventoryConfig config, int index){
        ItemStack display = ItemHelper.toTeamDisplayItem(config.getItems(),index);
        super.setItem(config.getSlots()[0],display);
    }

    public void setItem(TeamInventoryConfig config, int arrayDisplayIndex, int inventorySlotIndex)
    {
        try {
        ItemStack display = ItemHelper.toTeamDisplayItem(config.getItems(), arrayDisplayIndex);

        if (display != null) {
            System.out.println("Setting item: ========================");
            display.getItemMeta().getLore().forEach(string -> {
                System.out.println("Lore Phrase:"+string);
            });
        }

        super.setItem(inventorySlotIndex,display);
        System.out.println("===Set item end===");
          }
    catch(Exception e)
     {
         e.printStackTrace();
     }

    }
}
