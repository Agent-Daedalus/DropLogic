package me.daedalus.droplogic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import net.minecraft.world.PersistentState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtInt;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtDouble;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class DropLogicMapPersistentState extends PersistentState {

  public HashMap<Integer, DropVelocityChange> overridenDropVelocities = new HashMap<>();

  public DropLogicMapPersistentState() {
    
  }

  public static DropLogicMapPersistentState createNewState() {
    return new DropLogicMapPersistentState();
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    NbtCompound dropVelocsNbts = new NbtCompound();
    overridenDropVelocities.forEach((pos, dropVeloc) -> {
      NbtCompound dropVelocNbt = new NbtCompound();

      NbtList randomValuesList = new NbtList();

      for (double value : dropVeloc.randomValues) {
        randomValuesList.add(NbtDouble.of(value));
      }
      dropVelocNbt.put("randomValues", randomValuesList);

      if (dropVeloc.overridenBlocks.isPresent()) {
        NbtList blockList = new NbtList();
        dropVeloc.overridenBlocks.get().forEach(blockType -> {
          blockList.add(NbtInt.of(blockType));
        });
        dropVelocNbt.put("overridenBlocks", blockList);
      }
      dropVelocsNbts.put(pos.toString(), dropVelocNbt);
    });
    nbt.put("dropVelocNbts", dropVelocsNbts);

    return nbt;
  }
 
  public static DropLogicMapPersistentState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
    DropLogicMapPersistentState state = new DropLogicMapPersistentState();

    NbtCompound dropVelocNbts = tag.getCompound("dropVelocNbts");

    for (String posKey : dropVelocNbts.getKeys()) {
      NbtCompound dropVelocNbt = dropVelocNbts.getCompound(posKey);

      DropVelocityChange dropVelocityChange = new DropVelocityChange();

      NbtList randomValuesList = dropVelocNbt.getList("randomValues", NbtType.DOUBLE);
      dropVelocityChange.randomValues = new double[randomValuesList.size()];

      // Populate the array with values from the NbtList
      for (int i = 0; i < randomValuesList.size(); i++) {
        dropVelocityChange.randomValues[i] = randomValuesList.getDouble(i);
      }

      if (dropVelocNbt.contains("overridenBlocks")) {
        NbtList blockList = dropVelocNbt.getList("overridenBlocks", NbtType.INT);
        Set<Integer> blockTypes = new HashSet<>();

        for (int i = 0; i < blockList.size(); i++) {
          Integer blockType = blockList.getInt(i);
          blockTypes.add(blockType);
        }

        dropVelocityChange.overridenBlocks = Optional.of(blockTypes);
      } else {
        dropVelocityChange.overridenBlocks = Optional.empty();
      }

      state.overridenDropVelocities.put(Integer.parseInt(posKey), dropVelocityChange);
    }

    return state;
  }
  private static Type<DropLogicMapPersistentState> type = new Type<>(
    DropLogicMapPersistentState::new, // If there's no 'StateSaverAndLoader' yet create one
    DropLogicMapPersistentState::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
    null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
  );

  public static DropLogicMapPersistentState getServerState(MinecraftServer server) {
    // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
    PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

    // The first time the following 'getOrCreate' function is called, it creates a brand new 'DropLogicMapPersistentState' and
    // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
    // 'DropLogicMapPersistentState' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
    DropLogicMapPersistentState state = persistentStateManager.getOrCreate(type, DropLogicMod.MOD_ID);

    // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
    // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
    // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
    // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
    // there were no actual change to any of the mods state (INCREDIBLY RARE).
    state.markDirty();

    return state;
  }

  public static double[] getRandomValuesFromState(World world, BlockPos pos, ItemStack stack) {
    // Ensure we are dealing with a ServerWorld
    if (!(world instanceof ServerWorld serverWorld)) {
      // Handle the case where the world is not a ServerWorld
      return null;
    }

    PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
    DropLogicMapPersistentState state = persistentStateManager.getOrCreate(type, DropLogicMod.MOD_ID);

    // Check if the state exists and if it contains values for the given position
    if (state != null && state.overridenDropVelocities.containsKey(Objects.hash(pos.getX(), pos.getY(), pos.getZ()))) {
      // Retrieve the DropVelocityChange for the position
      DropVelocityChange dropVelocityChange = state.overridenDropVelocities.get(Objects.hash(pos.getX(), pos.getY(), pos.getZ()));
      return dropVelocityChange.randomValues; // Return the random values
    }

    return null;
  }

  public static void addDropVelocityChange(ServerWorld serverWorld, BlockPos pos, DropVelocityChange dropVelocityChange) {
    PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
    DropLogicMapPersistentState state = persistentStateManager.getOrCreate(type, DropLogicMod.MOD_ID);

    if (state != null) {
        state.overridenDropVelocities.put(Objects.hash(pos.getX(), pos.getY(), pos.getZ()), dropVelocityChange);
        state.markDirty();
    }
  }
}

