package com.backpack.services;

import com.backpack.objects.PPlayer;
import com.backpack.utils.MBS;
import com.base.ancestor.Objekt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PPlayerService extends Objekt {

    private HashMap<String, PPlayer> storedPPlayers = new HashMap<>();

    @Override
    public int of_load() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            of_registerPlayerAsPPlayer(p);
        }
        return 1;
    }

    @Override
    public void of_unload() {
        for(PPlayer p : storedPPlayers.values()) {
            of_unregisterPlayerAsPPlayer(p);
        }
    }

    public void of_registerPlayerAsPPlayer(Player p) {
        if(storedPPlayers.size() == 0) {
            if(MBS.of_getSettings().of_isUsingSmartDisconnect()) {
                if(MBS.of_getSQL().of_createConnection() != 1) {
                    of_sendErrorMessage(null, "PPlayerService.of_registerPlayerAsPPlayer();", "Error while using the smart-disconnect function! Couldn't create a database connection!");
                    return;
                }
            }
         }

        if(!storedPPlayers.containsKey(p.getName())) {
            storedPPlayers.put(p.getName(), new PPlayer(p));
        }
    }

    public void of_unregisterPlayerAsPPlayer(PPlayer p) {
        if(!MBS.of_getSettings().of_isUsingAlwaysSync()) {
            String[] backpackSerialNumbers = p.of_getInventorySerialNumbers();

            if(backpackSerialNumbers.length > 0) {
                for (String backpackSerialNumber : backpackSerialNumbers) {
                    MBS.of_getBackpackService().of_storeInventoryToDatabase(p, backpackSerialNumber, true);
                }
            }
        }

        storedPPlayers.remove(p.of_getName());

        if(storedPPlayers.size() == 0) {
            if(MBS.of_getSettings().of_isUsingSmartDisconnect()) {
                MBS.of_getSQL().of_closeConnection();
            }
        }
    }

    public PPlayer of_getPPlayer(String playerName) {
        return storedPPlayers.get(playerName);
    }
}
