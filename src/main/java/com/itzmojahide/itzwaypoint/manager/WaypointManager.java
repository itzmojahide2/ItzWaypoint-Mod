package com.itzmojahide.itzwaypoint.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itzmojahide.itzwaypoint.waypoint.Waypoint;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WaypointManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
    private static final Path WAYPOINTS_DIR = GAME_DIR.resolve("waypoints");
    private static final Path DEATHPOINTS_DIR = GAME_DIR.resolve("deathpoints");

    public static Map<String, Waypoint> waypoints = new ConcurrentHashMap<>();
    public static Map<String, Waypoint> deathpoints = new ConcurrentHashMap<>();

    public static void init() {
        WAYPOINTS_DIR.toFile().mkdirs();
        DEATHPOINTS_DIR.toFile().mkdirs();
        loadWaypoints();
        loadDeathpoints();
    }

    private static String getSafeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public static void saveOrUpdateWaypoint(Waypoint waypoint) {
        waypoints.put(waypoint.name, waypoint);
        File file = WAYPOINTS_DIR.resolve(getSafeFilename(waypoint.name) + ".json").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(waypoint, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadWaypoints() {
        waypoints.clear();
        File[] files = WAYPOINTS_DIR.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;
        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Waypoint waypoint = GSON.fromJson(reader, Waypoint.class);
                if (waypoint != null && waypoint.name != null) {
                    waypoints.put(waypoint.name, waypoint);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteWaypoint(String name) {
        waypoints.remove(name);
        File file = WAYPOINTS_DIR.resolve(getSafeFilename(name) + ".json").toFile();
        if (file.exists()) {
            file.delete();
        }
    }

    public static void createDeathPoint(double x, double y, double z) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String name = "DeathPoint_" + timestamp;
        Waypoint deathPoint = new Waypoint(name, x, y, z, 0xFFFF5555); // Red color
        deathpoints.put(name, deathPoint);

        File file = DEATHPOINTS_DIR.resolve(getSafeFilename(name) + ".json").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(deathPoint, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadDeathpoints() {
        deathpoints.clear();
        File[] files = DEATHPOINTS_DIR.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;
        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Waypoint deathpoint = GSON.fromJson(reader, Waypoint.class);
                if (deathpoint != null && deathpoint.name != null) {
                    deathpoints.put(deathpoint.name, deathpoint);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteDeathPoint(String name) {
        deathpoints.remove(name);
        File file = DEATHPOINTS_DIR.resolve(getSafeFilename(name) + ".json").toFile();
        if (file.exists()) {
            file.delete();
        }
    }
            }
