package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryName.TEAM_JOIN;


/**
 * @author CAMM
 * This inventory models the inventory for joining teams
 */
//This class is not part of the other shop inventories. Not an instance of Inventory.
public class TeamJoinInventory   //class to make inv for players to join teams
{
    private Inventory inventory;
    private final Collection<BattleTeam> teams;

    public TeamJoinInventory(Arena arena)
    {
        teams = arena.getTeams().values();
        createInventory();
    }

    @SuppressWarnings("deprecation")
    private void createInventory()
    {
        //public ItemStack(final Material type, final int amount, final short damage, final Byte data)

        int slots = (((teams.size()/9))+1)*9;
        inventory = Bukkit.createInventory(null, slots, TEAM_JOIN.getTitle());



        int slot = 0;
       for (BattleTeam currentTeam: teams)
       {
           ItemStack wool = new ItemStack(Material.WOOL,1,(short)0,(byte)(currentTeam.getTeamColor().getValue()));
           ItemMeta woolMeta = wool.getItemMeta();

           woolMeta.setDisplayName(currentTeam.getTeamColor().getName());
           wool.setItemMeta(woolMeta);
           inventory.setItem(slot,wool);
           slot++;
       }
    }

    public Inventory getInventory()
    {
        return inventory;
    }
}

