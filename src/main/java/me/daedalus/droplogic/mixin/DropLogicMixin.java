package me.daedalus.droplogic.mixin;

import java.util.function.Supplier; // Importing the Supplier interface

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin; // Importing the Mixin annotation
import org.spongepowered.asm.mixin.Overwrite; // Importing the Overwrite annotation

import me.daedalus.droplogic.DropLogicMod;
import me.daedalus.droplogic.DropVelocityMap;
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

  /**
   * Overrides the dropStack method from Block to implement custom drop logic.
   *
   * <p>This method is called when an item needs to be dropped in the world. If the custom drop rule
   * is enabled, it uses the custom drop motion defined in the DropVelocityMap. If not, it calculates a
   * default position for dropping the item within the specified block position.</p>
   *
   * @param world the world in which the item is being dropped
   * @param pos the position of the block from which the item is dropped
   * @param stack the item stack to be dropped
   * @reason kinda the whole point of the mod
   * @author me (daedalus)
   */
  @Overwrite
  public static void dropStack(World world, BlockPos pos, ItemStack stack) {
    LOGGER.info("dropStack called with pos: " + pos + " and stack: " + stack);

    if (world.getGameRules().getBoolean(DropLogicMod.CUSTOM_DROP_RULE)) {
      dropStack(world, DropVelocityMap.getDropMotion(world, pos, stack).asSupplier(world, pos, stack), stack);
    }
    else {
      double d = (double) 0.25 / 2.0;
      double e = (double)pos.getX() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
      double f = (double)pos.getY() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25) - d;
      double g = (double)pos.getZ() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
      dropStack(world, () -> new ItemEntity(world, e, f, g, stack), stack);
    }
  }
}
