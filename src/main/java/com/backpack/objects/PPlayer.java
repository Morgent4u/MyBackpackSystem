package com.backpack.objects;

import com.base.ancestor.Objekt;
import com.base.sys.Sys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PPlayer extends Objekt {
    private Player p;
    private String lastBackpackSerialNumber;
    private String[] inventorySerialnumbers = new String[0];
    private boolean coolDown;

    public PPlayer(Player p) {
        this.p = p;
    }

    public boolean of_invIsFull() {
        ItemStack[] items = p.getInventory().getStorageContents();

        for (ItemStack item : items) {
            if (item == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Is used to add a serialnumber to the backpack-serialNumber array.
     * To keep on track which inventory the player already had,
     * so we can store them into the database if the player leaves!
     * @param invSerialNumber The backpack-serialNumber.
     */
    public void of_addInventorySerialNumber(String invSerialNumber) {
        int index = Arrays.asList(inventorySerialnumbers).indexOf(invSerialNumber);

        if(index == -1) {
            inventorySerialnumbers = Sys.of_addArrayValue(inventorySerialnumbers, invSerialNumber);
        }
    }

    public void of_setCoolDown(boolean bool) {
        this.coolDown = bool;
    }

    /**
     * Is needed to identify the latest opened backpack by its
     * serial-number. (Necessary for the close-event!)
     * @param lastBackpackSerialNumber The backpack-serialNumber
     */
    public void of_setLastBackpackSerialNumber(String lastBackpackSerialNumber) {
        this.lastBackpackSerialNumber = lastBackpackSerialNumber;
    }

    public String[] of_getInventorySerialNumbers() {
        return inventorySerialnumbers;
    }

    public Player of_getPlayer() {
        return p;
    }

    public String of_getName() {
        return p.getName();
    }

    public String of_getLastBackpackSerialNumber() {
        return lastBackpackSerialNumber;
    }

    public boolean of_hasCoolDown() {
        return coolDown;
    }
}
