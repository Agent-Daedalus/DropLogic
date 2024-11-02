package me.daedalus.droplogic;

import net.minecraft.util.math.Vec3d;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;

public class DropMotion {
  private final Vec3d offset;
  private final Vec3d velocity;

  public DropMotion(double offsetX, double offsetY, double offsetZ, double velocityX, double velocityY, double velocityZ) {
    this.offset = new Vec3d(offsetX, offsetY, offsetZ);
    this.velocity = new Vec3d(velocityX, velocityY, velocityZ);
  }

  public DropMotion(double offsetX, double offsetY, double offsetZ, double velocityX, double velocityZ) {
    this.offset = new Vec3d(offsetX, offsetY, offsetZ);
    this.velocity = new Vec3d(velocityX, 0.2, velocityZ);
  }

  public DropMotion(long seed) {
    Random random = new Random(seed);
    this.offset = new Vec3d((random.nextDouble() / 2 - .25), (random.nextDouble() / 2 - .25), (random.nextDouble() / 2 - .25));
    this.velocity = new Vec3d(random.nextDouble() * 0.2 - 0.1, 0.2, random.nextDouble() * 0.2 - 0.1);
  }

  public Supplier<ItemEntity> asSupplier(World world, BlockPos pos, ItemStack stack) {
    double d = (double) 0.25 / 2.0;
    double e = (double) pos.getX() + 0.5 + offset.x;
    double f = (double) pos.getY() + 0.5 + offset.y - d;
    double g = (double) pos.getZ() + 0.5 + offset.z;

    return (() -> new ItemEntity(world, e, f, g, stack, velocity.x, velocity.y, velocity.z));
  }

  @Override
  public String toString() {
    return String.format("BlockMotion [Offset: %s, Velocity: %s]", 
      offset.toString(), velocity.toString());
  }
}
