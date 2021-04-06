package me.infinityz.minigame.gui.types;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class ConfigGui extends CustomGui {
    UHC instance = UHC.getInstance();
    DecimalFormat numberFormat = new DecimalFormat("#0.0");
    String permissionConfig = "uhc.config.cmd";

    SwitchGui appleRateSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "Apple rate"), 0.1f);
    SwitchGui flintRateSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "Flint rate"), 0.1f);
    
    public ConfigGui(RapidInv gui) {
        super(gui);

        var apple = new ItemBuilder(Material.APPLE).name(ChatColor.YELLOW + "Apple rate").lore(ChatColor.GREEN + "Confirm").build();
        appleRateSwitch.getGui().setItem(2, apple, action->{
            var player = (Player) action.getWhoClicked();
            if(instance.getGame().getAppleRate() != appleRateSwitch.getValue()){
                Bukkit.dispatchCommand(player, "config apple-rate "+ appleRateSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            }else{
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);
            
        });

        var flint = new ItemBuilder(Material.FLINT).name(ChatColor.YELLOW + "Flint rate").lore(ChatColor.GREEN + "Confirm").build();
        flintRateSwitch.getGui().setItem(2, flint, action->{
            var player = (Player) action.getWhoClicked();
            if(instance.getGame().getFlintRate() != flintRateSwitch.getValue()){
                Bukkit.dispatchCommand(player, "config flint-rate "+ flintRateSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            }else{
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);
            
        });

        gui.setItem(17, new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GREEN + "UHC Game").build(), action -> {
            var player = (Player) action.getWhoClicked();
            instance.getGuiManager().getMainGui().open((Player) action.getWhoClicked());
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
        });
        
        update();

    }

    @Override
    public void update() {

        updateAppleRate();
        updateFlintRate();
        updateNether();
        updateAdvancements();
        updateHorses();
        updateBeds();
        updateBedsNerf();
        updateStrength();
        updateStrengthNerf();
        updatePotions();
        updateTrident();
        updateItemsBurn();
        updateTears();
        updateTrades();
    }

    public void updateTrades(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.EMERALD).name(ChatColor.YELLOW + "Trades")
            .addLore((game.isTrades() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isTrades()).build();

        gui.setItem(13, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle trades"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config trades "+ !instance.getGame().isTrades());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateTears(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.GHAST_TEAR).name(ChatColor.YELLOW + "Ghast tears")
            .addLore((game.isTears() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isTears()).build();

            
        gui.setItem(12, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle ghast tears"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config tears "+ !instance.getGame().isTears());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateItemsBurn(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.FLINT_AND_STEEL).name(ChatColor.YELLOW + "Items burn")
            .addLore((game.isItemsBurn() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isItemsBurn()).build();

        gui.setItem(11, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle items burn"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config items-burn "+ !instance.getGame().isItemsBurn());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateTrident(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.TRIDENT).name(ChatColor.YELLOW + "Trident")
            .addLore(ChatColor.GREEN + (game.isTrident() ? "100% drop" : "Vanilla")).flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(10, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Change trident rate"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config trident "+ !instance.getGame().isTrident());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updatePotions(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.BREWING_STAND).name(ChatColor.YELLOW + "Potions")
            .addLore((game.isPotions() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isPotions()).flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(9, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle potions"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config potions "+ !instance.getGame().isPotions());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateStrengthNerf(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.SPLASH_POTION).name(ChatColor.YELLOW + "Nerfed strength 50%")
            .addLore((game.isStrengthNerf() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isStrengthNerf())
                .meta(PotionMeta.class, meta-> meta.setBasePotionData(new PotionData(PotionType.STRENGTH, false, false))).flags(ItemFlag.HIDE_POTION_EFFECTS).build();


        gui.setItem(8, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Change strength nerf"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config strength-nerf "+ !instance.getGame().isStrengthNerf());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateStrength(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.POTION).name(ChatColor.YELLOW + "Strength")
            .addLore((game.isStrength() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isStrength())
                .meta(PotionMeta.class, meta-> meta.setBasePotionData(new PotionData(PotionType.STRENGTH, false, false))).flags(ItemFlag.HIDE_POTION_EFFECTS).build();

        gui.setItem(7, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle strength"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config strength "+ !instance.getGame().isStrength());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateBedsNerf(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.LIGHT_BLUE_BED).name(ChatColor.YELLOW + "Nerfed bed explosion")
            .addLore((game.isBedsNerf() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isBedsNerf()).build();

        gui.setItem(6, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Change nerfed bed explosion"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config beds-nerf "+ !instance.getGame().isBedsNerf());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateBeds(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.BLUE_BED).name(ChatColor.YELLOW + "Beds")
            .addLore((game.isBeds() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isBeds()).build();

        gui.setItem(5, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle beds"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config beds "+ !instance.getGame().isBeds());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateHorses(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.SADDLE).name(ChatColor.YELLOW + "Horses")
            .addLore((game.isHorses() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isHorses()).build();

        gui.setItem(4, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle horses"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config horses "+ !instance.getGame().isHorses());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateAdvancements(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.YELLOW + "Advancements")
            .addLore((game.isAdvancements() ? ChatColor.GREEN : ChatColor.RED) + "" + game.isAdvancements()).build();

        gui.setItem(3, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle advancements"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config advancements "+ !instance.getGame().isAdvancements());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateNether(){
        var gui = getGui();
        var game = instance.getGame();
        var item = new ItemBuilder(Material.CRYING_OBSIDIAN).name(ChatColor.YELLOW + "Nether")
            .addLore((game.isNether() ? ChatColor.GREEN : ChatColor.RED) + "" + instance.getGame().isNether()).build();

        gui.setItem(2, item, action->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                
                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Toggle nether"), 
                
                confirm ->{
                    Bukkit.dispatchCommand(player, "config nether "+ !instance.getGame().isNether());
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
                }, deny->{
                    gui.open(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                });

                confirmationGui.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });

    }

    public void updateFlintRate(){
        var gui = getGui();
        var formatted = numberFormat.format(instance.getGame().getFlintRate());
        var item = new ItemBuilder(Material.FLINT).name(ChatColor.YELLOW + "Flint rate").addLore(ChatColor.GREEN + "" + formatted + "%").build();

        gui.setItem(1, item, action ->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                flintRateSwitch.setValue(instance.getGame().getFlintRate());
                flintRateSwitch.update();
                flintRateSwitch.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

    public void updateAppleRate(){
        var gui = getGui();
        var formatted = numberFormat.format(instance.getGame().getAppleRate());
        var item = new ItemBuilder(Material.APPLE).name(ChatColor.YELLOW + "Apple rate").addLore(ChatColor.GREEN + "" + formatted + "%").build();

        gui.setItem(0, item, action ->{
            var player = (Player) action.getWhoClicked();
            if(action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)){

                appleRateSwitch.setValue(instance.getGame().getAppleRate());
                appleRateSwitch.update();
                appleRateSwitch.open(player);
                
            }else{
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

}