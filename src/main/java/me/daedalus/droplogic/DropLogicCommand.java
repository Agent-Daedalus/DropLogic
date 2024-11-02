package me.daedalus.droplogic;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.Random;


public class DropLogicCommand {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
      CommandManager.literal("setDropVelocity")
      .then(CommandManager.argument("x", IntegerArgumentType.integer())
        .then(CommandManager.argument("y", IntegerArgumentType.integer())
          .then(CommandManager.argument("z", IntegerArgumentType.integer())
            .then(CommandManager.argument("blockType", StringArgumentType.word())
              .then(CommandManager.argument("dropData", StringArgumentType.greedyString())
                .executes(context -> executeSetDropVelocity(
                  context.getSource(),
                  new BlockPos(
                    IntegerArgumentType.getInteger(context, "x"),
                    IntegerArgumentType.getInteger(context, "y"),
                    IntegerArgumentType.getInteger(context, "z")),
                  StringArgumentType.getString(context, "blockType"),
                  StringArgumentType.getString(context, "dropData")
                ))))))));
  }

  private static int executeSetDropVelocity(ServerCommandSource source, BlockPos pos, String blockType, String dropData) {
    ServerWorld world = source.getWorld();

    // Access or create the DropLogicMapPersistentState

    // Check if dropData is a seed (numeric) or velocity values
    double[] velocities;
    velocities = parseVelocities(dropData);

    // Add entry to DropLogicMap
    DropLogicMapPersistentState.addDropVelocityChange(world, pos, new DropVelocityChange(Optional.empty(), velocities));

    // Send feedback to the player
    source.sendFeedback(() -> Text.literal("Set drop velocity at " + pos + " for block " + blockType), true);

    return 1;
  }

  private static double[] generateVelocitiesFromSeed(long seed) {
    Random random = new Random(seed);
    double[] velocities = new double[3];
    for (int i = 0; i < velocities.length; i++) {
      velocities[i] = random.nextDouble() * 2 - 1; // Random values between -1 and 1
    }
    return velocities;
  }

  private static double[] parseVelocities(String dropData) {
    String[] parts = dropData.split(",");
    double[] velocities = new double[parts.length];
    for (int i = 0; i < parts.length; i++) {
      velocities[i] = Double.parseDouble(parts[i]);
    }
    return velocities;
  }
}
