package me.infinityz.minigame.gui.types;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.CustomGui;
import net.noobsters.kern.paper.guis.RapidInv;

public class EnabledScenarios extends CustomGui {
    
    public EnabledScenarios(RapidInv gui) {
        super(gui);

        update();
        
    }

    @Override
    public void update(){
        /**
         * [1, 2, 3, 4, 5]
         * 
         */

        var gamemodes = UHC.getInstance().getGamemodeManager().getEnabledGamemodes();
        if(gamemodes.isEmpty()){
            //vanilla+
            
        }else{
            //gamemodes

        }
    }
    
}