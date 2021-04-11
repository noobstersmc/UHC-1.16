package me.noobsters.minigame.game.border;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitScheduler;

import lombok.Getter;
import me.noobsters.minigame.UHC;

/**
 * https://bitbucket.org/abandoncaptian/battleroyalborder/src/master/
 */
public class FortniteBorder {
    private UHC instance;

    private @Getter World world;
    private @Getter WorldBorder worldBorder;
    private @Getter double radius;
    private @Getter double centerX;
    private @Getter double centerZ;
    private @Getter double borderDamage;

    public FortniteBorder(World world, UHC instance) {
        this.instance = instance;
        this.world = world;
        this.worldBorder = world.getWorldBorder();
        this.centerX = worldBorder.getCenter().getX();
        this.centerZ = worldBorder.getCenter().getZ();
        this.radius = worldBorder.getSize();
        this.borderDamage = 0;
        this.worldBorder.setDamageAmount(0);
        this.worldBorder.setDamageBuffer(0);
        this.worldBorder.setWarningDistance(0);
        this.worldBorder.setWarningTime(0);
    }

    public void moveWorldBorder(double newX, double newZ, double newRadius, int timeInSeconds) {
        moveWorldBorder(newX, newZ, newRadius, timeInSeconds, this.borderDamage);
    }

    public void moveWorldBorder(double newX, double newZ, double newRadius, int timeInSeconds, double damageAmount) {
        BukkitScheduler bs = Bukkit.getScheduler();
        int stepsTime = timeInSeconds * 20;
        if (damageAmount != this.borderDamage)
            bs.runTaskLater(instance, () -> {
                setBorderDamage(damageAmount);
            }, stepsTime);
        int st = stepsTime / 5;
        double gapX = 0;
        double gapZ = 0;
        if (newX < this.centerX)
            gapX = this.centerX - newX;
        else
            gapX = newX - this.centerX;
        if (newZ < this.centerZ)
            gapZ = this.centerZ - newZ;
        else
            gapZ = newZ - this.centerZ;
        double stepsX = (gapX / st);
        double stepsZ = (gapZ / st);
        this.worldBorder.setSize(newRadius, timeInSeconds);
        for (int i = 1; i <= st; i++) {
            if (newX < this.centerX) {
                final int vI = i;
                bs.runTaskLaterAsynchronously(instance, () -> {
                    double n = this.centerX - (vI * stepsX);
                    this.worldBorder.setCenter(n, this.worldBorder.getCenter().getZ());
                }, i * 5);
            } else {
                //double n = this.centerX + (i * stepsX);
                final int vi = i;
                bs.runTaskLaterAsynchronously(instance, () -> {
                    double n = this.centerX + (vi * stepsX);
                    this.worldBorder.setCenter(n, this.worldBorder.getCenter().getZ());
                }, i * 5);
            }
        }
        for (int i = 1; i <= st; i++) {
            if (newZ < this.centerZ) {
                final int vI = i;
                bs.runTaskLaterAsynchronously(instance, () -> {
                    double n = this.centerZ - (vI * stepsZ);
                    this.worldBorder.setCenter(this.worldBorder.getCenter().getX(), n);
                }, i * 5);
            } else {

                final int vI = i;
                bs.runTaskLaterAsynchronously(instance, () -> {
                    double n = this.centerZ + (vI * stepsZ);
                    this.worldBorder.setCenter(this.worldBorder.getCenter().getX(), n);
                }, i * 5);
            }
        }
    }

    public void setBorderDamage(double damageAmount) {
        this.borderDamage = damageAmount;
        this.worldBorder.setDamageAmount(damageAmount);
    }

    public void setBorderDamageBuffer(double damageBuffer) {
        this.worldBorder.setDamageBuffer(damageBuffer);
    }

}
