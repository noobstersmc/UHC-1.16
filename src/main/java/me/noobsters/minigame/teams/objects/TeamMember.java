package me.noobsters.minigame.teams.objects;

import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

/*
 * Quick data object to be used as a issuerContextAware object to reduce compute
 * in  variables. Let lombok handle everything.
 */
@Data
public class TeamMember {
    private @NonNull @Setter(value = AccessLevel.PACKAGE) Player player;
    private @NonNull @Setter(value = AccessLevel.PACKAGE) Team team;
}