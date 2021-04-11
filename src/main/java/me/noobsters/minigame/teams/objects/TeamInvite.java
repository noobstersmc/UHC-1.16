package me.noobsters.minigame.teams.objects;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

/*
 * Data object with team invite info
 */
@Data
public class TeamInvite {
    private @NonNull @Setter(value = AccessLevel.PRIVATE) UUID teamToJoin;
    private @NonNull @Setter(value = AccessLevel.PRIVATE) UUID sender;
    private @NonNull @Setter(value = AccessLevel.PRIVATE) UUID target;

}