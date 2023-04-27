package com.backpack.inventory;

import com.backpack.utils.MBS;
import com.base.ancestor.Objekt;
import org.bukkit.inventory.ItemStack;

public class InventoryBackpack extends Objekt {
    private ItemStack backpackItemStack;
    private String inventoryName;
    private String patternSegment;
    private int inventorySize;

    @Override
    public String of_validate() {
        if(backpackItemStack == null) {
            return "Wrong Backpack-ItemStack! Please define a valid ItemStack!";
        } else {
            patternSegment = MBS.of_getInventoryService().of_getItemStackStringSegmentWithSpecificPattern(backpackItemStack, "%serialNumber%");

            if(patternSegment == null) {
                return "Wrong Backpack-ItemStack! The backpack-ItemStack needs to be contain the placeholder for the serial-number: %serialNumber%";
            } else if(patternSegment.replace("%serialNumber%", "").length() == 0) {
                return "Wrong Backpack-ItemStack! The backpack-ItemStack needs to have a text befor the serial-number, for example: '&aSerialnumber:&f %serialNumber%'";
            }
        }

        if(inventoryName == null || inventoryName.isEmpty()) {
            return "Wrong inventory-name! Please set a inventory-name which is not empty!";
        }

        if(inventorySize <= 0 || ( inventorySize != 9 && inventorySize != 18 && inventorySize != 27 && inventorySize != 36 && inventorySize != 54 && inventorySize != 64 )) {
            return "Wrong inventory-size! Please use one of the following sizes: 9, 18, 27, 36, 54 or 64!";
        }

        return null;
    }

    @Override
    public InventoryBackpack of_copyObject() {
        InventoryBackpack newInstance = new InventoryBackpack();
        newInstance.of_setBackPackItemStack(backpackItemStack);
        newInstance.of_setInventoryName(inventoryName);
        newInstance.of_setInventorySize(inventorySize);
        return newInstance;
    }

    public void of_setBackPackItemStack(ItemStack backpackItemStack) {
        this.backpackItemStack = backpackItemStack;
    }

    public void of_setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public void of_setInventorySize(int inventorySize) {
        this.inventorySize = inventorySize;
    }

    public ItemStack of_getBackpackItemStack() {
        return backpackItemStack;
    }

    public String of_getInventoryName() {
        return inventoryName;
    }

    public int of_getInventorySize() {
        return inventorySize;
    }

    public String of_getSerialNumberPatternSegment() {
        return patternSegment;
    }
}
