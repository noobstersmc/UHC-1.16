package me.infinityz.minigame.condor;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import me.infinityz.minigame.UHC;
import redis.clients.jedis.Jedis;

public class CondorManager {
    private @Getter Jedis jedis;
    private UHC instance;

    public CondorManager(UHC instance) {
        this.instance = instance;
        this.jedis = new Jedis("redis-11764.c73.us-east-1-2.ec2.cloud.redislabs.com", 11764);
        this.jedis.auth("Gxb1D0sbt3VoyvICOQKC8IwakpVdWegW");
    }

    public CompletableFuture<Long> writeToDatabase(String json, double score) {
        return CompletableFuture.supplyAsync(() -> {
            return jedis.zadd("selector", score, json);
        });
    }

    public CompletableFuture<Set<String>> getInfo(String set, long from, long to) {
        return CompletableFuture.supplyAsync(() -> {
            return jedis.zrange(set, from, to);
        });
    }

}
