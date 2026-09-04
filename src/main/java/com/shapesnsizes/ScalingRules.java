package com.shapesnsizes;

import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.gamerule.GameRuleEnum;
import net.minecraft.core.data.gamerule.GameRuleInteger;
import net.minecraft.core.data.gamerule.GameRules;

public final class ScalingRules {
	public static final GameRuleInteger ABILITY_SCALING =
		GameRules.register(new GameRuleInteger("abilityScaling", "gamerule.ability_scaling", 100, "percent"));

	public static final GameRuleBoolean SIZE_GRIEFING =
		GameRules.register(new GameRuleBoolean("doSizeGriefing", "gamerule.do_size_griefing", true));

	public static final GameRuleBoolean TRAMPLE_PLAYERS =
		GameRules.register(new GameRuleBoolean("sizeTramplePlayers", "gamerule.size_trample_players", true));

	public static final GameRuleBoolean STOMP_SOUNDS =
		GameRules.register(new GameRuleBoolean("doSizeStompSounds", "gamerule.do_size_stomp_sounds", false));

	public static final GameRuleBoolean WATER_DISPLACEMENT =
		GameRules.register(new GameRuleBoolean("doSizeWaterDisplacement", "gamerule.do_size_water_displacement", true));

	public static final GameRuleEnum<StartingSize> STARTING_SIZE =
		GameRules.register(new GameRuleEnum<>("startingSize", "gamerule.starting_size", StartingSize.NORMAL));

	private ScalingRules() {}

	public static void init() {}
}
