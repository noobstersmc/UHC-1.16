package me.infinityz.minigame.locations;

import java.util.HashSet;

import org.bukkit.Location;

import me.infinityz.minigame.UHC;

public class LocationManager {
    //TODO: Have the scatter task work with this locs
    HashSet<Location> locs;

    public LocationManager(UHC instance){
        locs = new HashSet<>();
        
    }

    public HashSet<Location> getLocationsSet() {
        return locs;
    }
    
}