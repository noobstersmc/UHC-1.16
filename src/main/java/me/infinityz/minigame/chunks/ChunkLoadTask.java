package me.infinityz.minigame.chunks;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public @RequiredArgsConstructor class ChunkLoadTask implements Runnable {
    private @Getter UUID locationID = UUID.randomUUID();
    private @NonNull @Getter World world;
    private @NonNull ChunksManager chunksManager;
    private @Getter boolean isDone = false, isRunning = false;
    private @Getter int chunksLeft = -1;

    @Override
    public void run() {
        isRunning = true;
        var coordinatePair = getRandomCoordinatePair(-2000, 2000);
        System.out.println("Finding location...");

        if (chunksManager.getLocations().isEmpty()) {
            // Load the chunk async and hold the async thread to prevent errors
            try {
                world.getChunkAtAsync(coordinatePair.getX(), coordinatePair.getZ()).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            var loc = coordinatePair.toLocation(world, true);
            // Test whether the loc is safe or not
            while (!ChunksManager.isSafe(loc)) {
                // Obtain a new coordiante
                coordinatePair = getRandomCoordinatePair(-2000, 2000);
                // Load the chunk async and hold the async thread to prevent errors
                try {
                    world.getChunkAtAsync(coordinatePair.getX(), coordinatePair.getZ()).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                loc = getRandomCoordinatePair(-2000, 2000).toLocation(world, true);
            }
            // Add the coordinates to the Collection.
            chunksManager.getLocations().add(ChunksManager.centerLocation(loc));
        } else {
            // Get all the currently known locations as a Collection of Coordinates
            var coordinates = chunksManager.getLocations().stream().map(it -> CoordinatePair.fromLocation(it))
                    .collect(Collectors.toList());
            // Validate the pair of coordiantes against set
            var loc = isSafeValidate(coordinatePair, 100, coordinates);
            // Add the coordinates to the Collection.
            chunksManager.getLocations().add(ChunksManager.centerLocation(loc));
            // Set variable to the possibly changed coordinate pair
            coordinatePair = getCoordinatePair(loc);
        }
        System.out.println("Done finding the location at " + coordinatePair.toString() + ", moving to chunk load.");
        // Once this point is reach, lots of chunks must be loaded.
        var neighbours = ChunksManager.getNeighbouringChunks(coordinatePair.toChunkObject(), 8);
        chunksLeft = neighbours.size();
        var iterator = neighbours.iterator();
        var semaphore = new Semaphore(50);
        // Start iterating.
        while (iterator.hasNext()) {
            try {
                semaphore.acquire();
                var chunkObj = iterator.next();
                world.getChunkAtAsync(chunkObj.getX(), chunkObj.getZ()).thenAccept(chunk -> {
                    semaphore.release();
                    chunk.setForceLoaded(true);
                    chunksLeft--;
                    //Changed 1 to 5 to allow for error margin and avoid getting stuck
                    if (chunksLeft <= 5) {
                        System.out.println("(Virrtually) Done with chunkload task " + getLocationID().toString() + ".");
                        isDone = true;
                    }
                    //Notify every 25 chunks to reduce spam.
                    if (chunksLeft % 25 == 0) {
                        System.out.println("Another chunk loaded for task " + getLocationID().toString() + ". "
                                + chunksLeft + " left.");
                    }

                });
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

    }

    private Location isSafeValidate(CoordinatePair toTest, final int distance,
            final Collection<CoordinatePair> coordinatePairs) {
        // Check the numbers mucho rapido.
        while (!validate(toTest, distance, coordinatePairs.iterator())) {
            toTest = getRandomCoordinatePair(-2000, 2000);
        }
        // Change the coordinate set to a location
        try {
            world.getChunkAtAsync(toTest.getX(), toTest.getZ()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        var loc = toTest.toLocation(world, true);
        // If the location is not safe, go through the whole process again.
        if (ChunksManager.isSafe(loc)) {
            return loc;
        } else {
            return isSafeValidate(getRandomCoordinatePair(-2000, 2000), distance, coordinatePairs);
        }
    }

    private boolean validate(final CoordinatePair toTest, final int distance, final Iterator<CoordinatePair> iterator) {
        // Simply calculate the distance to verify validity.
        while (iterator.hasNext()) {
            var coord = iterator.next();
            if (Math.abs(toTest.distanceTo(coord)) < distance)
                return false;
        }
        return true;
    }

    CoordinatePair getRandomCoordinatePair(int min, int max) {
        return CoordinatePair.of((int) ((Math.random() * (max - min)) + min),
                (int) ((Math.random() * (max - min)) + min));
    }

    CoordinatePair getCoordinatePair(Location loc) {
        return CoordinatePair.of(loc.getBlockX(), loc.getBlockZ());
    }

}