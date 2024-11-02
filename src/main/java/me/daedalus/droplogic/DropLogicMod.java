package me.daedalus.droplogic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

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
    DropVelocityMap.addDropMotion(new BlockPos(0,0,0), Items.SAND, new DropMotion(0,0,0,0,2,0));
  }
}

