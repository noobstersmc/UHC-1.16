package me.infinityz.minigame.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.players.serializers.ItemStackSerializers;
import me.infinityz.minigame.teams.objects.Team;

@RequiredArgsConstructor
@CommandAlias("restore")
public class GameRestoreCMD extends BaseCommand {
    private @Getter @NonNull UHC instance;

    @Subcommand("json")
    @CommandPermission("uhc.admin")
    public void toJson(CommandSender sender, String args) {
        if (!instance.getDataFolder().exists())
            instance.getDataFolder().mkdir();
        String path = instance.getDataFolder().getPath() + File.separator + "teams.json";

        // Write JSON file
        if (args.startsWith("save")) {
            try (FileWriter file = new FileWriter(path)) {
                File f = new File(path);
                if (!f.exists()) {
                    f.createNewFile();
                }
                var gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(instance.getTeamManger().getTeamMap(), file);
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader(path)) {
                var gson = new Gson();
                var tt = new TypeToken<THashMap<UUID, Team>>() {
                }.getType();
                THashMap<UUID, Team> json = gson.fromJson(reader, tt);
                sender.sendMessage("Teams have been restored from JSON file.");
                instance.getTeamManger().setTeamMap(json);
                instance.getTeamManger().clearCache();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Subcommand("data")
    @CommandPermission("uhc.admin")
    public void playerData(CommandSender sender, String args) {
        if (!instance.getDataFolder().exists())
            instance.getDataFolder().mkdir();
        String path = instance.getDataFolder().getPath() + File.separator + "data.json";

        // Write JSON file
        if (args.startsWith("save")) {
            try (FileWriter file = new FileWriter(path)) {
                File f = new File(path);
                if (!f.exists()) {
                    f.createNewFile();
                }
                var gson = new GsonBuilder().setPrettyPrinting()
                        .registerTypeAdapter(ItemStack.class, ItemStackSerializers.getItemStackSerializer())
                        .registerTypeAdapter(ItemStack[].class, ItemStackSerializers.getItemStackArraySerializer())
                        .create();
                gson.toJson(instance.getPlayerManager().getUhcPlayerMap(), file);
                file.flush();
                System.out.println(gson);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader(path)) {
                var gson = new Gson();
                var tt = new TypeToken<THashMap<Long, UHCPlayer>>() {
                }.getType();
                THashMap<Long, UHCPlayer> json = gson.fromJson(reader, tt);

                instance.getPlayerManager().setUhcPlayerMap(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Subcommand("game")
    @CommandPermission("uhc.admin")
    public void oi(CommandSender sender, String args) {
        try {
            System.out.println(instance.getGame().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!instance.getDataFolder().exists())
            instance.getDataFolder().mkdir();
        String path = instance.getDataFolder().getPath() + File.separator + "game.json";

        // Write JSON file
        if (args.startsWith("save")) {
            try (FileWriter file = new FileWriter(path)) {
                File f = new File(path);
                if (!f.exists()) {
                    f.createNewFile();
                }
                var gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(instance.getGame(), file);
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader(path)) {
                var gson = new Gson();
                var tt = new TypeToken<Game>() {
                }.getType();
                Game json = gson.fromJson(reader, tt);
                Game actualGame = instance.getGame();
                actualGame.setStartTime(json.getStartTime());
                actualGame.setEnd(json.isEnd());
                actualGame.setGameID(json.getGameID());
                actualGame.setPvp(actualGame.isPvp());
                actualGame.setNether(actualGame.isNether());

                sender.sendMessage("Game was restored from JSON file.");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}