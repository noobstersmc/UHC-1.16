package me.noobsters.minigame.Twitter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import net.md_5.bungee.api.ChatColor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@CommandAlias("twitter")
public @RequiredArgsConstructor class TweetCMD extends BaseCommand {
    private @NonNull UHC instance;
    private static Gson gson = new Gson();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build();
    public static String CONDOR_URL = "https://hynix-condor.herokuapp.com/";

    @Default
    @CommandPermission("tweet.cmd")
    @Subcommand("tweet")
    @CommandAlias("tweet")
    public void tweet(CommandSender sender, String tweet) throws IOException {
        var response = tweet(tweet, "6QR3W05K3F");

        var url = gson.fromJson(response, JsonObject.class).get("url");
        if (url != null) {
            var _url = url.getAsString();
            sender.sendMessage(ChatColor.GREEN + "Tweet send! \n" + _url);
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't send tweet!");
        }
    }

    public static String tweet(String tweet, String auth) throws IOException {
        // Create the tweet as json
        var tweet_json = LairTweet.of(tweet).toJson(gson);
        // Create the request and call for response

        var body = RequestBody.create(tweet_json, JSON);
        Request request = new Request.Builder().url(CONDOR_URL + "utils/tweet").addHeader("Authorization", auth)
                .addHeader("Content-Type", "application/json").post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @CommandPermission("post.cmd")
    @Subcommand("post")
    @CommandAlias("post")
    public void post(CommandSender sender, Integer time) {
        try {

            var size = instance.getTeamManger().getTeamSize() > 1 ? "Teams of " + instance.getTeamManger().getTeamSize() : "FFA";
            var scenarios = instance.getGamemodeManager().getEnabledGamemodesToString();
            var game = size + " " + scenarios;
            var minutes = instance.getGame().getBorderTime();

            sender.sendMessage(game);

            var future = getTimeInFuture(time);
            var formatted = DateTimeFormatter.ofPattern("hh:mm").format(future);
            var timeLeft = getTimeLeft(future);
            if (time < 10 || time > 30) {
                sender.sendMessage(ChatColor.RED
                        + "Couldn't post must be more than 10 minutes in advance and less than 30 minutes in advance.");
            } else {

                var tweet = new StringBuilder();

                tweet.append("UHC 1.16 " + size + "\n");
                tweet.append("IP noobsters.net\n");
                tweet.append("\n");
                tweet.append(minutes/60 + "m + Meetup in " + timeLeft + "\n");
                tweet.append(formatted + " (https://time.is/ET)\n");
                tweet.append("\n");

                var iter = instance.getGamemodeManager().getEnabledGamemodes().iterator();

                if(iter.hasNext())
                    while (iter.hasNext())
                        tweet.append("- " + iter.next().getName() + "\n");
                else
                    tweet.append("- Vanilla+");

                tweet(sender, tweet.toString());
            }

        } catch (Exception e) {

        }
    }

    static LocalDateTime getTimeInFuture(long m) {
        var time = LocalDateTime.now(ZoneId.of("America/New_York")).plusMinutes(m).withSecond(0);

        var min = time.getMinute();
        var module = (int) min % 5;
        if (module != 0) {
            min += (5 - module);
        }
        time = time.plusMinutes(Math.abs(min - time.getMinute()));

        return time;

    }

    static String getTimeLeft(LocalDateTime time) throws ParseException {

        final var apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        apiFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        final Date dateOfGame = apiFormat.parse(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(time));
        final long millis = dateOfGame.getTime() - System.currentTimeMillis() - 1000;
        var hours = TimeUnit.MILLISECONDS.toHours(millis);
        var mins = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));

        return "In " + (hours != 0 ? hours + "h " : "") + (mins != 0 ? mins + "m" : "");
    }

}