package me.daedalus.droplogic.mixin;

import java.util.function.Supplier; // Importing the Supplier interface

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin; // Importing the Mixin annotation
import org.spongepowered.asm.mixin.Overwrite; // Importing the Overwrite annotation

import me.daedalus.droplogic.DropLogicMod;
import net.minecraft.block.Block; // Importing the Block class
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack; // Importing the ItemStack class
import net.minecraft.server.world.ServerWorld; // Importing the ServerWorld class
import net.minecraft.util.math.BlockPos; // Importing the BlockPos class
import net.minecraft.util.math.Direction; // Importing the Direction class
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World; // Importing the World class

@Mixin(Block.class)
public class DropLogicMixin {
  private static final Logger LOGGER = LoggerFactory.getLogger("droplogic");

  private static void dropStack(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
    if (!(world instanceof ServerWorld)) return;  // Check if the world is a server world
    ServerWorld serverWorld = (ServerWorld) world;

    if (stack.isEmpty() || !serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) return;  // Game rules check

    ItemEntity itemEntity = itemEntitySupplier.get();
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);  // Spawn the item entity in the world
  }

  @Overwrite
  public static void dropStack(World world, BlockPos pos, ItemStack stack) {
    LOGGER.info("dropStack called with pos: " + pos + " and stack: " + stack);

    double d = (double) 0.25 / 2.0;
    if (world.getGameRules().getBoolean(DropLogicMod.CUSTOM_DROP_RULE)) {
      double[] randomValues = DropLogicMod.getRandomValues(world, pos, stack);
      double e = (double) pos.getX() + 0.5 + (randomValues[0] / 2 - .25);
      double f = (double) pos.getY() + 0.5 + (randomValues[1] / 2 - .25) - d;
      double g = (double) pos.getZ() + 0.5 + (randomValues[2] / 2 - .25);

      double l = randomValues[3] * 0.2 - 0.1;
      double m = 0.2;
      double n = randomValues[4] * 0.2 - 0.1; 
      dropStack(world, () -> new ItemEntity(world, e, f, g, stack, l, m, n), stack);
    }
    else {
      double e = (double)pos.getX() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
      double f = (double)pos.getY() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25) - d;
      double g = (double)pos.getZ() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
      dropStack(world, () -> new ItemEntity(world, e, f, g, stack), stack);
    }
  }

  @Overwrite
  public static void dropStack(World world, BlockPos pos, Direction direction, ItemStack stack) {
    LOGGER.info("dropStack2 called with pos: " + pos + " and stack: " + stack);
    double e = (double) pos.getX() + 0.5;
    double f = (double) pos.getY() + 0.5 - (0.25 / 2.0); // Drop straight up
    double g = (double) pos.getZ() + 0.5;
    double l = 0.0; // Zero horizontal velocity
    double m = 0.1; // Vertical velocity; adjust as needed
    double n = 0.0; // Zero horizontal velocity
    dropStack(world, () -> new ItemEntity(world, e, f, g, stack, l, m, n), stack);
  }
}
