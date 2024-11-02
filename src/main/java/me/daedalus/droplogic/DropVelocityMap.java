package me.daedalus.droplogic;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.*;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public class DropVelocityMap {
  private static final Map<BlockPos, Map<Item, DropMotion>> dropChangedMotions = new HashMap<>();

  public static DropMotion getDropMotion(World world, BlockPos pos, ItemStack stack) {
    Map<Item, DropMotion> motionsAtPos = dropChangedMotions.get(pos);

    if (motionsAtPos != null) {
      Item item = stack.getItem();

      if (motionsAtPos.containsKey(item))
        return motionsAtPos.get(item);

      if (motionsAtPos.containsKey(Items.AIR))
        return motionsAtPos.get(Items.AIR);
    }

    long seed = pos.getX() + pos.getY() * 912309832 - (pos.getZ() ^ 858585843) + (Item.getRawId(stack.getItem()) ^ 12039832);
    return new DropMotion(seed);
  }

  public static void addDropMotion(BlockPos pos, Item item, DropMotion motion) {
    dropChangedMotions.computeIfAbsent(pos, k -> new HashMap<>()).put(item, motion);
  }

  public static void addDropMotion(BlockPos pos, DropMotion motion) {
    dropChangedMotions.computeIfAbsent(pos, k -> new HashMap<>()).put(Items.AIR, motion);
  }
}
