package me.noobsters.minigame.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.enums.Stage;
import me.noobsters.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("host.perm")
@CommandAlias("tool|tools")
public @RequiredArgsConstructor class ToolCMD extends BaseCommand {

    private @NonNull UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    @Subcommand("proximity")
    @CommandAlias("proximity")
    @CommandCompletion("@onlineplayers")
    public void proximityCMD(Player sender, @Flags("other") Player target, @Optional Integer distance) {
        if (sender.getGameMode() != GameMode.SPECTATOR && !sender.hasPermission("uhc.admin")) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode to do this.");
        } else {
            var dist = distance == null ? 100 : distance;
            var players = target.getPlayer().getLocation().getNearbyPlayers(dist,
                    player -> player.getUniqueId() != target.getUniqueId()
                            && player.getGameMode() == GameMode.SURVIVAL);

            var sb = new StringBuilder();
            sb.append(ChatColor.GREEN + "Players near " + target.getPlayer().getName() + " in a radius of " + dist
                    + " blocks: " + ChatColor.YELLOW);

            if (!players.isEmpty()) {
                players.forEach(player -> {
                    sb.append("" + player.getName().toString() + " ");
                });
                sender.sendMessage(sb.toString());
            } else
                sender.sendMessage(ChatColor.RED + "There are no players near "
                        + target.getPlayer().getName().toString() + " in a radius of " + dist + " blocks.");

        }
    }

    @Subcommand("ores")
    @CommandAlias("ores")
    @CommandCompletion("@onlineplayers")
    public void ores(Player sender, @Flags("other") Player target) {
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        if (sender.getGameMode() != GameMode.SPECTATOR && !sender.hasPermission("uhc.admin")) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode to do this.");
        } else {
            sender.sendMessage(ChatColor.GRAY + target.getName().toString() + "'s mined ores: " + ChatColor.AQUA
                    + "DIAMOND: " + uhcPlayer.getMinedDiamonds() + ChatColor.GOLD + " GOLD: " + uhcPlayer.getMinedGold()
                    + ChatColor.of("#95562F") + " ANCIENT DEBRIS: " + uhcPlayer.getMinedAncientDebris());
        }
    }

    @Subcommand("specinfo")
    @CommandAlias("specinfo")
    @CommandCompletion("@onlineplayers")
    public void specInfo(Player sender, @Flags("other") Player target) {
        if (instance.getGame().getGameStage() != Stage.INGAME) {
            sender.sendMessage(ChatColor.RED + "You must be in game stage.");
            return;
        }
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        uhcPlayer.setSpecInfo(!uhcPlayer.isSpecInfo());
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.GREEN + target.getName() + "'s SpecInfo has been set to "
                + uhcPlayer.isSpecInfo(), "host.info");

    }

    @Subcommand("spme")
    @CommandAlias("spme")
    public void spme(Player sender) {
        var playerManager = instance.getPlayerManager();
        var uhcPlayer = playerManager.getPlayer(sender.getUniqueId());
        Bukkit.dispatchCommand(sender, "specinfo " + sender.getName().toString());
        uhcPlayer.getPlayer().damage(100);

    }

    @CommandPermission("staff.perm")
    @Subcommand("togglespec|ts")
    @CommandAlias("togglespec|ts")
    public void onToggleSpec(Player sender) {
        toggleGm(sender);
        var senderName = ChatColor.GRAY + "[" + sender.getName() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + ChatColor.GRAY
                + (sender.getGameMode() == GameMode.SPECTATOR ? "Temporal Spectator Enabled."
                        : "Temporal Spectator Disabled."),
                permissionDebug);

    }

    @CommandCompletion("@onlineplayers")
    @Subcommand("t")
    @CommandAlias("t")
    public void teleportCMD(Player sender, @Flags("other") Player target) {
        if (sender.getGameMode() != GameMode.SPECTATOR) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode.");
            return;
        }
        sender.teleportAsync(target.getLocation());
        sender.sendActionBar(ChatColor.GRAY + "Teleported to " + target.getName());

    }

    @CommandPermission("staff.perm")
    @Subcommand("tpworld")
    @CommandAlias("tpworld")
    @CommandCompletion("@worlds")
    public void tpWorld(Player player, World world) {
        player.teleport(world.getSpawnLocation());
        player.sendActionBar(ChatColor.GRAY + "Teleported to world " + world.getName());
    }

    @CommandPermission("staff.perm")
    @Subcommand("tpHereTeam")
    @CommandAlias("tpHereTeam")
    @CommandCompletion("@onlineplayers")
    public void tpTeam(Player player, @Flags("other") Player target) {
        var team = instance.getTeamManger().getPlayerTeam(target.getUniqueId());
        if (team != null) {
            if (team.getAliveMembers(instance).isEmpty()) {
                player.sendActionBar(ChatColor.RED + "All players are dead.");
                return;
            }
            team.getAliveMembers(instance).forEach(mate -> {
                var p = Bukkit.getPlayer(mate.getUUID());
                if (p != null && p.isOnline())
                    p.teleport(player.getLocation());
            });
            player.sendActionBar(ChatColor.GRAY + "Teleported " + target.getName() + "'s team to you.");
        } else {
            player.sendActionBar(ChatColor.RED + "");
        }
    }

    @CommandPermission("guest.cmd")
    @CommandCompletion("@onlineplayers")
    @Subcommand("guest")
    @CommandAlias("guest")
    public void guestCMD(Player sender, @Flags("other") Player target) {
        if (!target.hasPermission("group.guest")) {
            sender.sendMessage(ChatColor.GREEN + target.getName().toString() + " is now guest of this UHC!");
            target.addAttachment(instance).setPermission("group.guest", true);
            target.updateCommands();
        }
    }

    public boolean toggleGm(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SPECTATOR);
            return true;
        } else if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.CREATIVE);
            return false;
        }
        return false;
    }

    @CommandPermission("admin.perm")
    @CommandCompletion("@onlineplayers")
    @Subcommand("max-health")
    @CommandAlias("max-health")
    public void maxHealth(CommandSender sender, @Flags("other") Player target, Float value) {
        target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + ChatColor.of("#7ab83c") + target.getName().toString()
                + " changed max health to " + value, permissionDebug);
    }

    @CommandPermission("admin.perm")
    @Subcommand("test")
    @CommandAlias("test")
    public void test(Player player) {
        var loc = player.getLocation();
        var entity = loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        var villager = (Villager) entity;
        villager.setProfession(Profession.LIBRARIAN);
        Merchant merchant = (Merchant) entity;
        entity.setCustomName("King");
        List<MerchantRecipe> list = new ArrayList<>();
        MerchantRecipe rec = new MerchantRecipe(new ItemStack(Material.DIAMOND, 1), 32);
        // List<ItemStack> ingredients = new ArrayList<ItemStack>();
        // ingredients.add(new ItemStack(Material.CARROT, 12));
        // ingredients.add(new ItemStack(Material.POTATO, 12));
        rec.addIngredient(new ItemStack(Material.NETHER_STAR, 1));
        // rec.setIngredients(ingredients);
        list.add(rec);
        merchant.setRecipes(list);

    }

}