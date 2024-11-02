package me.daedalus.droplogic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.WorldSavePath;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DropMotionStorage {
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private static final String FILENAME = "drop_motions.json";

  public static void saveDropMotions(MinecraftServer server, Map<BlockPos, Map<Item, DropMotion>> dropChangedMotions) {
    Path worldFolder = server.getSavePath(WorldSavePath.ROOT);
    File file = worldFolder.resolve(FILENAME).toFile();

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
        return; // Exit if file creation fails
      }
    }

    // Convert BlockPos and Item keys to String for serialization
    Map<String, Map<String, DropMotion>> stringKeyedMap = new HashMap<>();
    for (Map.Entry<BlockPos, Map<Item, DropMotion>> entry : dropChangedMotions.entrySet()) {
        String blockPosKey = blockPosToString(entry.getKey());
        Map<String, DropMotion> itemMap = new HashMap<>();
        for (Map.Entry<Item, DropMotion> itemEntry : entry.getValue().entrySet()) {
            String itemKey = itemEntry.getKey().getTranslationKey(); // Convert Item to String
            itemMap.put(itemKey, itemEntry.getValue());
        }
        stringKeyedMap.put(blockPosKey, itemMap);
    }

    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(stringKeyedMap, writer);
    } catch (IOException e) {
      e.printStackTrace(); // Handle exceptions
    }
  }
  public static Map<BlockPos, Map<Item, DropMotion>> loadDropMotions(MinecraftServer server) {
    Path worldFolder = server.getSavePath(WorldSavePath.ROOT);
    File file = worldFolder.resolve(FILENAME).toFile();

    if (!file.exists()) 
      return new HashMap<>();

    try (Reader reader = new FileReader(file)) {
      Type type = new TypeToken<Map<String, Map<String, DropMotion>>>() {}.getType();
      Map<String, Map<String, DropMotion>> stringKeyedMap = gson.fromJson(reader, type);

      // Convert String keys back to BlockPos and Item
      Map<BlockPos, Map<Item, DropMotion>> dropChangedMotions = new HashMap<>();
      for (Map.Entry<String, Map<String, DropMotion>> entry : stringKeyedMap.entrySet()) {
        BlockPos blockPosKey = stringToBlockPos(entry.getKey());
        Map<Item, DropMotion> itemMap = new HashMap<>();
        for (Map.Entry<String, DropMotion> itemEntry : entry.getValue().entrySet()) {
          Item itemKey = Registries.ITEM.get(Identifier.of(itemEntry.getKey()));
          itemMap.put(itemKey, itemEntry.getValue());
        }
        dropChangedMotions.put(blockPosKey, itemMap);
      }

      return dropChangedMotions;
    } catch (IOException e) {
      e.printStackTrace();
      return new HashMap<>(); // Return an empty map on error
    }
  }

  // Helper to convert BlockPos to string format "(x,y,z)"
  public static String blockPosToString(BlockPos pos) {
    return String.format("(%d,%d,%d)", pos.getX(), pos.getY(), pos.getZ());
  }

  // Helper to convert from string format "(x,y,z)" back to BlockPos
  public static BlockPos stringToBlockPos(String s) {
    String[] parts = s.replace("(", "").replace(")", "").split(",");
    int x = Integer.parseInt(parts[0]);
    int y = Integer.parseInt(parts[1]);
    int z = Integer.parseInt(parts[2]);
    return new BlockPos(x, y, z);
  }
}

