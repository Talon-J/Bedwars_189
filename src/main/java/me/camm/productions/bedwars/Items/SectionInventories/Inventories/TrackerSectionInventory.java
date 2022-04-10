package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamColors;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;

import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TrackerSectionInventory extends CraftInventoryCustom
{

    private final Set<BattleTeam> entries;
    private static final int LENGTH = TeamColors.values().length;
    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final int ROW = InventoryProperty.LARGE_ROW_TWO_START.getValue();
    private static final ItemStack SEPARATOR = ItemHelper.createGlassPane(ItemCategory.SEPARATOR);

    public TrackerSectionInventory() throws IllegalStateException {
        super(null, InventoryProperty.MEDIUM_SHOP_SIZE.getValue(), InventoryName.TRACKER.getTitle());
      entries = new HashSet<>();

      if (SEPARATOR==null)
          throw new IllegalStateException("Separators should not be null!");
    }

    public void addEntry(@NotNull BattleTeam team){
        entries.add(team);
    }

    public void addEntries(Collection<BattleTeam> teams, BattleTeam exclusion){
        for (BattleTeam next : teams) {
            if (next.equals(exclusion))
                continue;

            addEntry(next);
        }
    }

    public void removeEntry(@NotNull BattleTeam team){
        entries.remove(team);
    }

    public void updateInventory(){
        Iterator<BattleTeam> iter = entries.iterator();

        int slot = 1;
        while (slot < LENGTH) {

            setItem(slot+ROW,AIR);
            if (iter.hasNext()) {
                BattleTeam team = iter.next();
                TeamColors color = team.getTeamColor();

                ItemStack trackStack = ItemHelper.createColoredGlass(Material.STAINED_GLASS_PANE,color.getDye());
              if (trackStack!=null) {
                  ItemMeta meta = trackStack.getItemMeta();
                  meta.setDisplayName(color.getChatColor()+color.getName());
                  trackStack.setItemMeta(meta);
                  setItem(slot+ROW, trackStack);
              }
            }

            slot++;


        }
    }


}
