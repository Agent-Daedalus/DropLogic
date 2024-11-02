package me.daedalus.droplogic;

import java.util.Random;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class DropLogicMod implements ModInitializer {
  public static final String MOD_ID = "droplogic";

  public static final GameRules.Key<GameRules.BooleanRule> CUSTOM_DROP_RULE = GameRuleRegistry.register(
    "customDropLogic",
    GameRules.Category.DROPS, 
    GameRuleFactory.createBooleanRule(false)
  );

  @Override
  public void onInitialize() {
    System.out.println("Initializing DropLogic mod...");

    CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, environment) -> {
      if (environment == CommandManager.RegistrationEnvironment.DEDICATED) {
        DropLogicCommand.register(commandDispatcher);
      }
});
  }

  public static double[] getRandomValues(World world, BlockPos pos, ItemStack stack) {
    double[] fromMap = DropLogicMapPersistentState.getRandomValuesFromState(world, pos, stack);
    if (fromMap != null) return fromMap;

    long seed = pos.getX() + pos.getY() * 12309832 - (pos.getZ() ^ 858585843) + (stack.getItem().getName().hashCode()); 
    Random myRandom = new Random(seed);
    double[] values = new double[5];
    for (int i = 0; i < 5; i++) {
      values[i] = myRandom.nextDouble();
    }
    return values;
  }
}

