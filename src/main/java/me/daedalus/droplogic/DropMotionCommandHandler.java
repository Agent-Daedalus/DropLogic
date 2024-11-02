package me.daedalus.droplogic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.CommandRegistryAccess;

import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Item;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.command.CommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class DropMotionCommandHandler {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    dispatcher.register(
      literal("setDropMotion").then(
        argument("location", BlockPosArgumentType.blockPos()).then(
          argument("item", ItemStackArgumentType.itemStack(registryAccess)).then(
            argument("offset", Vec3ArgumentType.vec3(false)).then(
              argument("velocity", Vec3ArgumentType.vec3(false)).executes(ctx -> setDropMotion(ctx.getSource(), 
                BlockPosArgumentType.getBlockPos(ctx, "location"), 
                ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem(), 
                Vec3ArgumentType.getVec3(ctx, "offset"),
                Vec3ArgumentType.getVec3(ctx, "velocity")
              ))
            )
          )
        ).then(
          literal("all").then(
            argument("offset", Vec3ArgumentType.vec3(false)).then(
              argument("velocity", Vec3ArgumentType.vec3(false)).executes(ctx -> setDropMotion(
                ctx.getSource(), 
                BlockPosArgumentType.getBlockPos(ctx, "location"), 
                Items.AIR, 
                Vec3ArgumentType.getVec3(ctx, "offset"),
                Vec3ArgumentType.getVec3(ctx, "velocity")
              ))
            )
          )
        )
      )
    );

    dispatcher.register(
      literal("rerollDropMotion").then(
        argument("location", BlockPosArgumentType.blockPos()).then(
          argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(ctx -> rerollDropMotion(
            ctx.getSource(),
            BlockPosArgumentType.getBlockPos(ctx, "location"), 
            ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()
          ))
        ).then(
          literal("all").executes(ctx -> rerollDropMotion(
            ctx.getSource(),
            BlockPosArgumentType.getBlockPos(ctx, "location"), 
            Items.AIR
          ))
        )
      )
    );
  }

  public static int setDropMotion(ServerCommandSource source, BlockPos blockPos, Item item, Vec3d offset, Vec3d velocity) {
    DropVelocityMap.addDropMotion(blockPos, item, new DropMotion(offset.x, offset.y, offset.z, velocity.x, velocity.y, velocity.z));
    source.sendFeedback(() -> Text.literal("Set " + blockPos.toShortString() + " for " + (item == Items.AIR ? "all" : item.getName()) 
                + " to (offset=" + offset.toString() + ", velocity=" + velocity.toString() + ")"), true);
    return Command.SINGLE_SUCCESS; // Return success
  }

  public static int rerollDropMotion(ServerCommandSource source, BlockPos blockPos, Item item) {
    DropVelocityMap.addDropMotion(blockPos, item, new DropMotion(source.getWorld().random.nextLong()));
    source.sendFeedback(() -> Text.literal("Rerolled at BlockPos: " + blockPos.toShortString() + " for " + (item == Items.AIR ? "all" : item.getName())), true);
    return Command.SINGLE_SUCCESS; // Return success
  }
}
