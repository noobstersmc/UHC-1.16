package me.infinityz.minigame.teams.objects;

import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * Quick data object to be used as a issuerContextAware object to reduce compute
 * in variables. Let lombok handle everything.
 */
public @Data @AllArgsConstructor class TeamMember {
    private @Setter(value = AccessLevel.PACKAGE) Player player;
    private @Setter(value = AccessLevel.PACKAGE) Team team;
}