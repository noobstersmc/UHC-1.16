package me.infinityz.minigame.condor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import me.infinityz.minigame.UHC;
import redis.clients.jedis.Jedis;

public class CondorManager {
    private @Getter Jedis jedis;
    private UHC instance;
    private boolean registered;

    public CondorManager(UHC instance) {
        this.instance = instance;
        this.jedis = new Jedis("redis-11764.c73.us-east-1-2.ec2.cloud.redislabs.com", 11764);
        this.jedis.auth("Gxb1D0sbt3VoyvICOQKC8IwakpVdWegW");
    }

    public CondorManager() {
        this.jedis = new Jedis("redis-11764.c73.us-east-1-2.ec2.cloud.redislabs.com", 11764);
        this.jedis.auth("Gxb1D0sbt3VoyvICOQKC8IwakpVdWegW");
    }

    public static void main(String[] args) {
        var condor = new CondorManager();

        try {
            var promise = condor.writeExpirableData(UUID.randomUUID(), "{\"ip\": \"192.168.1.1\"}", 10).get();
            System.out.println("Result promise 1 = " + promise);
            var new_promise = condor.getKeys("servers:uhc:*").get();
            System.out.println("Result promise 2 = " + new_promise);
            var data = condor.jedis.mget(new_promise.toArray(new String[] {}));
            System.out.println("All data: " + data);
        } catch (InterruptedException | ExecutionException e) {

            e.printStackTrace();
        }

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
