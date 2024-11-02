package me.daedalus.droplogic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
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

    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> DropMotionCommandHandler.register(dispatcher, registryAccess));

    // Register events for server start and end
    ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
    ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
  }

  private void onServerStarted(MinecraftServer server) {
    // Load drop motions when the server starts
    DropVelocityMap.dropChangedMotions = DropMotionStorage.loadDropMotions(server);
  }

  private void onServerStopping(MinecraftServer server) {
    // Save drop motions when the server stops
    DropMotionStorage.saveDropMotions(server, DropVelocityMap.dropChangedMotions);
  }
}


