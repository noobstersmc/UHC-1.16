package me.noobsters.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class FlorPoderosa extends IGamemode implements Listener {
    private UHC instance;
    private ArrayList<Material> possibleDrops = new ArrayList<>();
    private Random random = new Random();

    public FlorPoderosa(UHC instance) {
        super("FlowerPower", "Las flores dropean cosas poderosas.", Material.CORNFLOWER);
        this.instance = instance;

        for (var materials : Material.values()) {
            var mString = materials.toString();
            if (!materials.isItem() || mString.contains("LEGACY") || isBanned(materials))
                continue;
            possibleDrops.add(materials);
        }
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled()) {
            return false;
        }
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled()) {
            return false;
        }
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        // Test for and find if block broken was a flower
        // Call getRandomDropWithBooks to get the drop
        switch (e.getBlock().getType()) {
            case ROSE_BUSH:
            case LILAC:
            case PEONY:
            case SUNFLOWER: {
                var relative = e.getBlock().getRelative(BlockFace.DOWN);
                if (relative.getType() == e.getBlock().getType()) {
                    relative.setType(Material.AIR);
                }
            }
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case CORNFLOWER:
            case LILY_OF_THE_VALLEY:
            case WITHER_ROSE:
            case DEAD_BUSH: {
                e.getBlock().setType(Material.AIR);
                e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                        getRandomDropWithMeta());

                break;
            }
            default:
                break;
        }

    }

    private boolean isFlower(Material material) {
        switch (material) {
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case CORNFLOWER:
            case LILY_OF_THE_VALLEY:
            case WITHER_ROSE:
            case DEAD_BUSH:
                return true;
            default:
                return false;
        }
    }

    private boolean isBanned(Material material) {
        if (isFlower(material) || isDoubleFlower(material))
            return true;
        switch (material) {
            case STRUCTURE_BLOCK:
            case JIGSAW:
            case DEBUG_STICK:
            case SPAWNER:
            case BARRIER:
            case COMMAND_BLOCK:
            case COMMAND_BLOCK_MINECART:
            case CHAIN_COMMAND_BLOCK:
            case BEDROCK:
            case STRUCTURE_VOID:
            case REPEATING_COMMAND_BLOCK:
                return true;
            default:
                return false;
        }
    }

    private boolean isDoubleFlower(Material material) {
        switch (material) {
            case ROSE_BUSH:
            case LILAC:
            case PEONY:
            case SUNFLOWER:
                return true;
            default:
                return false;
        }
    }

    private ItemStack getRandomDropWithMeta() {
        var randomDrop = getRandomDrop();
        var meta = randomDrop.getItemMeta();

        if (meta instanceof EnchantmentStorageMeta) {
            var enchMeta = (EnchantmentStorageMeta) meta;
            var enchant = getRandomEnchant();
            enchMeta.addStoredEnchant(enchant, random.nextInt(enchant.getMaxLevel()), true);
            randomDrop.setItemMeta(enchMeta);
        } else if (meta instanceof PotionMeta) {
            var potMeta = (PotionMeta) meta;
            potMeta.setBasePotionData(getRandomPotData());
            randomDrop.setItemMeta(potMeta);
        }
        return randomDrop;
    }

    private PotionData getRandomPotData() {
        var bool = random.nextBoolean();
        var potType = getRandomPotionType();
        if (potType.isExtendable() && bool) {
            return new PotionData(potType, bool, false);
        } else if (potType.isUpgradeable() && bool) {
            return new PotionData(potType, false, bool);
        } else if (potType.isExtendable() && potType.isUpgradeable()) {
            return new PotionData(potType, bool, random.nextBoolean());
        }
        return new PotionData(potType, false, false);
    }

    private PotionType getRandomPotionType() {
        return PotionType.values()[random.nextInt(PotionType.values().length)];
    }

    private Enchantment getRandomEnchant() {
        return Enchantment.values()[random.nextInt(Enchantment.values().length)];
    }

    private ItemStack getRandomDrop() {
        var randomMaterial = possibleDrops.get(random.nextInt(possibleDrops.size()));
        return new ItemStack(randomMaterial,
                random.nextInt(randomMaterial.getMaxStackSize() >= 8 ? 8 : randomMaterial.getMaxStackSize()) + 1);
    }

}
