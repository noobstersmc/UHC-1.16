package me.infinityz.minigame.condor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CondorAPI {
    private static Gson gson = new Gson();
    private static String CONDOR_URL = "http://condor.jcedeno.us:420";
    private static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS).build();
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
        return makeJsonRequest(builder().url(CONDOR_URL + "/game").addHeader("auth", secret)
                .addHeader("condor_id", condorID).get().build());
    }

    /* Helper to reduce boiler plate */
    static okhttp3.Request.Builder builder() {
        return new Request.Builder();
    }

}
