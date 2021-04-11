package me.noobsters.minigame.condor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CondorAPI {
    private static Gson gson = new Gson();
    public static String CONDOR_URL = "https://hynix-condor.herokuapp.com/";
    private @Getter static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS)
            .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Creates a request that non-null JsonObjects.
     * 
     * @param rq Okhttp3 request, use {@link #builder()} or new Request.Builder() to
     *           create one.
     * @return Non-null JsonObject which may or may not be empty.
     */
    public static JsonObject makeJsonRequest(Request rq) {
        var jsonObject = new JsonObject();

        /* Attempt to perform call to condor */
        try (var rs = client.newCall(rq).execute()) {
            try {
                return gson.fromJson(rs.body().string(), JsonObject.class);
            } catch (JsonSyntaxException e) {
                jsonObject.addProperty("error", "unable to parse json data");
                jsonObject.addProperty("unparsed", rs.body().string());
                e.printStackTrace();
            }
        } catch (IOException e) {
            /* If response does exist but it wasn't able to parse it */
            jsonObject.addProperty("error", "not available");
            e.printStackTrace();
        }
        /* return the object with its contents */
        return jsonObject;
    }

    /**
     * Obtain a json object containing a game config
     * 
     * @param condorID Condor UUIDv4 ID
     * @param secret   Secret to authenticate with Condor
     * @return JsonObject containing gameConfig. Might contain error as an element.
     *         Non Null
     */
    public static JsonObject getGameJsonConfig(String condorID, String secret) {
        /* Helper fucntion to create the request and return it */
        return makeJsonRequest(builder().url(CONDOR_URL + "utils/game?condor_id=" + condorID)
                .addHeader("Authorization", secret).get().build());
    }

    public static String getCondorRandomSeed() throws IOException {
        var rq = builder().url(CONDOR_URL + "utils/seeds").get().build();
        var response = client.newCall(rq).execute().body().string();

        return response;
    }

    
    /**
     * Create a delete request in condor
     * 
     * @param auth      Authorization token, also used as billing id.
     * @param condor_id Condor_id of instance to be deleted
     * @return Json Response in string form.
     * @throws IOException
     */
    public static String delete(String auth, String condor_id) throws IOException {
        Request request = new Request.Builder().url(CONDOR_URL + "instances/" + condor_id + "?fromBukkit=true")
                .addHeader("Authorization", auth).addHeader("Content-Type", "application/json").delete().build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /* Helper to reduce boiler plate */
    public static okhttp3.Request.Builder builder() {
        return new Request.Builder();
    }

}
