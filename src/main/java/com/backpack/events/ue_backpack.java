package com.backpack.events;

import com.backpack.main.main;
import com.backpack.objects.PPlayer;
import com.backpack.utils.MBS;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.Objects;

public class ue_backpack implements Listener {

    @EventHandler
    public void ue_playerJoin4MBS(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        MBS.of_getPPlayerService().of_registerPlayerAsPPlayer(p);
    }

    @EventHandler
    public void ue_playerQuit4MBS(PlayerQuitEvent e) {
        PPlayer p = MBS.of_getPPlayerService().of_getPPlayer(e.getPlayer().getName());

        if(p != null) {
            MBS.of_getPPlayerService().of_unregisterPlayerAsPPlayer(p);
        }
    }

    @EventHandler
    public void ue_playerInteract4MBS(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if(Objects.requireNonNull(p.getItemInHand()).getType() != Material.AIR) {
            PPlayer pp = MBS.of_getPPlayerService().of_getPPlayer(p.getName());

            if(pp != null) {
                e.setCancelled(MBS.of_getBackpackService().of_check4BackpackInteractionItem(pp, p.getItemInHand()));
            }
        }
    }

    @EventHandler
    public void ue_inventoryClose4MBS(InventoryCloseEvent e) {
        PPlayer p = MBS.of_getPPlayerService().of_getPPlayer(e.getPlayer().getName());

        if(p != null) {
            String lastBackpackSerialNumber = p.of_getLastBackpackSerialNumber();

            if(lastBackpackSerialNumber != null) {
                p.of_setLastBackpackSerialNumber(null);

                //  Check if the user tried to story his own backpack inside it...
                if(MBS.of_getBackpackService().of_isBackpackInventory(e.getView().getTitle())) {
                    Inventory inv = e.getInventory();

                    for(int i = 0; i < inv.getSize(); i++) {
                        ItemStack item = inv.getItem(i);
                        if(item != null) {
                            String serialNumber = MBS.of_getBackpackService().of_getBackpackSerialNumberByAnyInventoryBackpackStructure(item);

                            if(serialNumber != null && serialNumber.equals(lastBackpackSerialNumber)) {
                                inv.setItem(i, null);

                                if(p.of_invIsFull()) {
                                    p.of_getPlayer().getWorld().dropItemNaturally(p.of_getPlayer().getLocation(), item);
                                } else {
                                    p.of_getPlayer().getInventory().addItem(item);
                                }

                                MBS.of_getMessage().of_sendMessage2PlayerByKey(p.of_getPlayer(), "Messages.MBS.BackpackInBackpackSelf");
                                break;
                            }
                        }
                    }

                    MBS.of_getBackpackService().of_updateBackpackInventory(lastBackpackSerialNumber, inv);

                    //  Check if we need to update the database immediately...
                    if(MBS.of_getSettings().of_isUsingAlwaysSync()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                MBS.of_getBackpackService().of_storeInventoryToDatabase(p, lastBackpackSerialNumber, false);
                            }
                        }.runTaskAsynchronously(main.of_getPlugin());
                    }
                }
            }
        }
    }

    @EventHandler
    public void ue_playerDeath4MBS(PlayerDeathEvent e) {
        List<ItemStack> itemStackList = e.getDrops();

        if(itemStackList.size() > 0) {
            PPlayer p = MBS.of_getPPlayerService().of_getPPlayer(e.getEntity().getName());

            if(p != null) {
                itemStackList = MBS.of_getBackpackService().of_checkForBackpackItemOnDeathItems(p, itemStackList);

                for(int i = 0; i < itemStackList.size(); i++) {
                    e.getDrops().set(i, itemStackList.get(i));
                }
            }
        }
    }

    @EventHandler
    public void ue_dropItem4MBS(PlayerDropItemEvent e) {
        PPlayer p = MBS.of_getPPlayerService().of_getPPlayer(e.getPlayer().getName());

        if(p != null) {
            e.getItemDrop().setItemStack(MBS.of_getBackpackService().of_getOtherBackpackItemByBackpack(p, e.getItemDrop().getItemStack()));
        }
    }
}
