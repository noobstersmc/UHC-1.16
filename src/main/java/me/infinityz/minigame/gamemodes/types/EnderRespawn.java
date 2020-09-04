package me.infinityz.minigame.gamemodes.types;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

/**
 * EnderRespawn
 */
public class EnderRespawn extends IGamemode {

    public EnderRespawn(UHC instance) {
        super("EnderRespawn", "Respawn team leader with EnderCrystal.", null, new EnderRespawnListener(instance));
    }

    
}