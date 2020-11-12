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


    public CompletableFuture<String> writeExpirableData(String ID, String data, int expiration) {
        return CompletableFuture.supplyAsync(() -> {
            var serverID = "servers:uhc:" + ID;
            return jedis.setex(serverID, 60, data);
        });
    }

    public CompletableFuture<Set<String>> getKeys(String pattern) {
        return CompletableFuture.supplyAsync(() -> {
            return jedis.keys(pattern);
        });
    }

    public CompletableFuture<String> writeExpirableData(Object ID, String data, int expiration) {
        return writeExpirableData(ID.toString(), data, expiration);
    }

}
