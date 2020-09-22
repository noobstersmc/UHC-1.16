package me.infinityz.minigame.gamemodes.types;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class ShadouneMode extends IGamemode implements Listener {
    private UHC instance;

    public ShadouneMode(UHC instance) {
        super("Shadoune Mode", "Some extra rules might be active. Please ask the staff for now.");
        this.instance = instance;
        instance.getCommandManager().registerCommand(new InnerShadouneMode());
    }

    /**
     * InnerShadouneMode
     */

    @CommandAlias("shadoune")
    public class InnerShadouneMode extends BaseCommand {

        @Subcommand("set")
        public void setRuleDescription(CommandSender sender, String args) {
            setDescription(ChatColor.translateAlternateColorCodes('&', args));
            sender.sendMessage("Changed to: " + getDescription());
        }

        @Subcommand("reset")
        public void resetDescription(CommandSender sender) {
            setDescription("Some extra rules might be active. Please ask the staff for now.");
            sender.sendMessage("Changed to: " + getDescription());
        }

        @Subcommand("blank")
        public void blankDescription(CommandSender sender) {
            setDescription("");
            sender.sendMessage("Changed to: " + getDescription());
        }

        @Subcommand("append")
        public void appendDescription(CommandSender sender, String args) {
            setDescription(getDescription() + ChatColor.translateAlternateColorCodes('&', args));
            sender.sendMessage("Changed to: " + getDescription());
        }

        @Subcommand("add")
        public void addDescription(CommandSender sender, String args) {
            setDescription(getDescription() + "\n" + ChatColor.translateAlternateColorCodes('&', args));
            sender.sendMessage("Changed to: " + "\n" + getDescription());
        }

    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }
}
