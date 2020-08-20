package me.infinityz.minigame.game;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.mrmicky.fastinv.FastInv;
import lombok.NonNull;

public class UpdatableInventory extends FastInv {
    // Add a consumer of tasks that will run while the inv is open and cancel them
    // when it closes.
    private BukkitTask updateTask;

    public UpdatableInventory(int size, String title) {
        super(size, title);

    }

    public void addUpdateTask(@NonNull BukkitRunnable runnable, @NonNull Plugin plugin, long delay, long period,
            boolean isAsync) {

        this.addOpenHandler(e -> updateTask = isAsync ? runnable.runTaskTimerAsynchronously(plugin, delay, period)
                : runnable.runTaskTimer(plugin, delay, period));

        this.addCloseHandler(e -> {
            if (!updateTask.isCancelled()) {
                updateTask.cancel();
            }
        });
    }

}