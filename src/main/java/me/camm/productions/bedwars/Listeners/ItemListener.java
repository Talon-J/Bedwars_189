package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Generators.Forge;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.ItemDatabases.TieredItem;

import me.camm.productions.bedwars.Util.Helpers.InventoryOperationHelper;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;
import org.bukkit.Location;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import org.bukkit.inventory.ItemStack;



import java.util.HashMap;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Files.FileKeywords.TeamFileKeywords.FORGE_SPAWN;
import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.ARENA;

public class ItemListener implements Listener
{
    private final Arena arena;
    private final HashMap<String, Forge> forges;

    public ItemListener(Arena arena)
    {
        this.arena = arena;
        forges = new HashMap<>();

        for (BattleTeam team: arena.getTeams().values()) {
            Forge forge = team.getForge();
            forges.put(forge.getId().toString(),forge);
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event)
    {
        if (event.getEntity().hasMetadata(FORGE_SPAWN.getKey())||event.getTarget().hasMetadata(FORGE_SPAWN.getKey()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event)
    {
        if (event.getLocation().getBlock().hasMetadata(ARENA.getData())) {
            event.setCancelled(true);
            Item item = event.getEntity();
            item.setTicksLived(1);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event)
    {
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
        UUID id = event.getPlayer().getUniqueId();


        if (!players.containsKey(id))
            return;

            BattlePlayer player = players.get(id);

            if (!player.getIsAlive() || player.getIsEliminated())
            {
                event.setCancelled(true);
                return;
            }

            String name = event.getItem().getName();
            ItemStack picked = event.getItem().getItemStack().clone();


            if (ItemHelper.isItemInvalid(picked))
                return;

            TieredItem tiered = ItemHelper.isTieredItem(ItemHelper.getAssociate(picked));


            if (tiered != null)
            {
                if (ItemHelper.isSword(tiered.getItem())) {
                    ItemHelper.clearAll(ShopItem.WOODEN_SWORD.sellMaterial,player.getRawPlayer().getInventory());
                }

            }
            else if (ItemHelper.isCurrencyItem(picked))
            {

                Location loc = player.getTeam().getForge().getForgeLocation();
                double distanceSquared = player.getTeam().getForge().getDistance();
                distanceSquared *= distanceSquared;

                Item pickup = event.getItem();
                if (!pickup.hasMetadata(FORGE_SPAWN.getKey()))
                    return;

                Forge forge = null;
                if (name != null) {
                    forge = forges.getOrDefault(name, null);
                }

                if (forge != null)
                    forge.updateChildren(picked.getType(),picked.getAmount());

                for (BattlePlayer current : player.getTeam().getPlayers().values()) {
                    if (!(current.getTeam().equals(player.getTeam()) && !current.equals(player))) {
                    continue;
                    }

                    if (current.getRawPlayer().getLocation().distanceSquared(loc) > distanceSquared)
                        continue;

                        if (current.getRawPlayer().getInventory().firstEmpty() != -1 &&
                                (current.getIsAlive() && !current.getIsEliminated()))
                            current.getRawPlayer().getInventory().addItem(picked);


                }
            }

    }



    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        ConcurrentHashMap<UUID, BattlePlayer> registered = arena.getPlayers();
        if (!registered.containsKey(player.getUniqueId()))
            return;

        Item dropped = event.getItemDrop();

        ItemStack stack = dropped.getItemStack();
        if (stack==null || stack.getItemMeta() == null)
            return;

        BattlePlayer current = registered.get(player.getUniqueId());
        ShopItem item = ItemHelper.getAssociate(stack);

        if ( (ItemHelper.isAxe(stack) || ItemHelper.isPick(stack))) {
            event.setCancelled(true);
            return;
        }

        /*
        Navigators should not be dropped, but they also should not end up in the
        player inv.
         */
        if (ItemHelper.getNavigator(stack) != null) {
            event.getItemDrop().remove();
            return;
        }

        if (!ItemHelper.isSword(item))
            return;

        if (ItemHelper.isInventoryPlaceRestrict(stack)) {
            event.setCancelled(true);
            return;
        }

        InventoryOperationHelper.operateSwordCount(current);

    }
}
