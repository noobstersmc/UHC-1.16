package me.infinityz.minigame.condor;

import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CondorConfig {
    String host;
    String game_type;
    UUID host_uuid;
    String level_seed;
    String[] scenarios;
    int team_size;

    public static CondorConfig ofJson(JsonObject jsonObject) {
        var host = jsonObject.get("host").getAsString();
        var host_uuid = UUID.fromString(jsonObject.get("host_uuid").getAsString());
        var game_type = jsonObject.get("game_type").getAsString();
        var extra_data = jsonObject.getAsJsonObject("extra_data");
        var level_seed = extra_data.get("level_seed").getAsString();
        if (level_seed.isEmpty())
            level_seed = "random";

        var scenariosList = new ArrayList<String>();
        extra_data.getAsJsonArray("memberName").forEach(all -> scenariosList.add(all.getAsString()));

        var scenarios = scenariosList.toArray(new String[] {});

        var team_size = extra_data.get("team_size").getAsInt();

        return CondorConfig.of(host, game_type, host_uuid, level_seed, scenarios, team_size);
    }

}
