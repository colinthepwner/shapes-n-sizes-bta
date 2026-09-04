package com.shapesnsizes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeFloat;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.shapesnsizes.PlayerScale;
import com.shapesnsizes.ScalingRules;
import com.shapesnsizes.ShapesConfig;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.packet.PacketGameRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScalingCommand implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		ArgumentBuilderLiteral<CommandSource> root = ArgumentBuilderLiteral.literal("scaling");
		root.requires(CommandSource::hasAdmin);
		root.executes(this::usage);

		root.then(ArgumentBuilderRequired.<CommandSource, Float>argument("scale", ArgumentTypeFloat.floatArg(PlayerScale.MIN, PlayerScale.MAX))
			.executes(this::setSelf));
		root.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("players", ArgumentTypeEntity.usernames())
			.then(ArgumentBuilderRequired.<CommandSource, Float>argument("scale", ArgumentTypeFloat.floatArg(PlayerScale.MIN, PlayerScale.MAX))
				.executes(this::set)));

		ArgumentBuilderLiteral<CommandSource> set = ArgumentBuilderLiteral.literal("set");
		set.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("players", ArgumentTypeEntity.usernames())
			.then(ArgumentBuilderRequired.<CommandSource, Float>argument("scale", ArgumentTypeFloat.floatArg(PlayerScale.MIN, PlayerScale.MAX))
				.executes(this::set)));
		root.then(set);

		ArgumentBuilderLiteral<CommandSource> get = ArgumentBuilderLiteral.literal("get");
		get.executes(this::getSelf);
		get.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("players", ArgumentTypeEntity.usernames())
			.executes(this::get));
		root.then(get);

		ArgumentBuilderLiteral<CommandSource> reset = ArgumentBuilderLiteral.literal("reset");
		reset.executes(this::resetSelf);
		reset.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("players", ArgumentTypeEntity.usernames())
			.executes(this::reset));
		root.then(reset);

		ArgumentBuilderLiteral<CommandSource> brownies = ArgumentBuilderLiteral.literal("browniereset");
		brownies.executes(this::clearBonusSelf);
		brownies.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("players", ArgumentTypeEntity.usernames())
			.executes(this::clearBonus));
		root.then(brownies);

		ArgumentBuilderLiteral<CommandSource> setAbility = ArgumentBuilderLiteral.literal("setabilityscaling");
		setAbility.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("percent", ArgumentTypeInteger.integer(0, 1000))
			.executes(this::setAbility));
		root.then(setAbility);

		ArgumentBuilderLiteral<CommandSource> reload = ArgumentBuilderLiteral.literal("reload");
		reload.executes(this::reload);
		root.then(reload);

		ArgumentBuilderLiteral<CommandSource> getAbility = ArgumentBuilderLiteral.literal("getabilityscaling");
		getAbility.executes(this::getAbility);
		root.then(getAbility);

		LiteralCommandNode<CommandSource> node = dispatcher.register(root);

		ArgumentBuilderLiteral<CommandSource> alias = ArgumentBuilderLiteral.literal("scale");
		alias.requires(CommandSource::hasAdmin);
		alias.redirect(node);
		alias.executes(this::usage);
		dispatcher.register(alias);

		ArgumentBuilderLiteral<CommandSource> brownieAlias = ArgumentBuilderLiteral.literal("browniereset");
		brownieAlias.requires(CommandSource::hasAdmin);
		brownieAlias.executes(this::clearBonusSelf);
		brownieAlias.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("players", ArgumentTypeEntity.usernames())
			.executes(this::clearBonus));
		dispatcher.register(brownieAlias);
	}

	private int clearBonusSelf(CommandContext<CommandSource> c) {
		Player self = c.getSource().getSender();
		if (self == null) {
			c.getSource().sendMessage("§cThe console has no brownies to wear off. Name a player.");
			return 0;
		}
		return clearBonusFor(c, java.util.Collections.singletonList(self));
	}

	private int clearBonus(CommandContext<CommandSource> c) throws CommandSyntaxException {
		List<Player> players = players(c);
		if (players.isEmpty()) {
			c.getSource().sendMessage("§cNo players matched.");
			return 0;
		}
		return clearBonusFor(c, players);
	}

	private int clearBonusFor(CommandContext<CommandSource> c, List<Player> players) {
		for (Player p : players) {
			float eaten = PlayerScale.getBonus(p);
			PlayerScale.clearBonus(p);
			if (p != c.getSource().getSender()) {
				c.getSource().sendMessage(p, "§eWhatever you had eaten has worn off; you are §f"
					+ PlayerScale.format(PlayerScale.getBase(p)) + "x §eagain.");
			}
			if (players.size() == 1) {
				c.getSource().sendMessage("§aCleared §f" + PlayerScale.format(eaten)
					+ "x §aof eaten size from §f" + p.getDisplayName() + "§a, leaving §f"
					+ PlayerScale.format(PlayerScale.getBase(p)) + "x§a.");
			}
		}
		if (players.size() > 1) {
			c.getSource().sendMessage("§aCleared eaten size from §f" + players.size() + " players§a.");
		}
		return 1;
	}

	private int reload(CommandContext<CommandSource> c) {
		ShapesConfig.load();
		c.getSource().sendMessage("§aReloaded starting sizes: default §f"
			+ PlayerScale.format(ShapesConfig.defaultScale()) + "x§a, §f"
			+ ShapesConfig.namedCount() + "§a player(s) named. §7New sizes apply on a player's first join.");
		return 1;
	}

	private int usage(CommandContext<CommandSource> c) {
		CommandSource s = c.getSource();
		s.sendMessage("§eShapes n Sizes §7— §f/scale §7is short for §f/scaling");
		s.sendMessage("§f/scale <scale> §7- yourself. 1 is normal, " + PlayerScale.format(PlayerScale.MIN) + " to " + PlayerScale.format(PlayerScale.MAX));
		s.sendMessage("§f/scale <players> <scale> §7- someone else");
		s.sendMessage("§f/scale reset §7- yourself back to normal");
		s.sendMessage("§f/scaling set <players> <scale>");
		s.sendMessage("§f/scaling get [players]");
		s.sendMessage("§f/scaling reset <players>");
		s.sendMessage("§f/scaling setabilityscaling <percent> §7- how much speed, reach and jump follow size");
		s.sendMessage("§f/scaling getabilityscaling");
		s.sendMessage("§f/scaling reload §7- re-read config/shapesnsizes.properties");
		return 1;
	}

	private List<Player> players(CommandContext<CommandSource> c) throws CommandSyntaxException {
		EntitySelector selector = c.getArgument("players", EntitySelector.class);
		List<? extends Entity> entities = selector.get(c.getSource());
		List<Player> players = new ArrayList<>();
		for (Entity e : entities) {
			if (e instanceof Player) players.add((Player) e);
		}
		return players;
	}

	private int set(CommandContext<CommandSource> c) throws CommandSyntaxException {
		float scale = PlayerScale.clamp(c.getArgument("scale", Float.class));
		return apply(c, scale);
	}

	private int reset(CommandContext<CommandSource> c) throws CommandSyntaxException {
		return apply(c, PlayerScale.DEFAULT);
	}

	private int setSelf(CommandContext<CommandSource> c) {
		return applyToSelf(c, PlayerScale.clamp(c.getArgument("scale", Float.class)));
	}

	private int resetSelf(CommandContext<CommandSource> c) {
		return applyToSelf(c, PlayerScale.DEFAULT);
	}

	private int applyToSelf(CommandContext<CommandSource> c, float scale) {
		Player self = c.getSource().getSender();
		if (self == null) {
			c.getSource().sendMessage("§cName a player: §f/scaling set <players> <scale>");
			return 0;
		}
		return applyTo(c, Collections.singletonList(self), scale);
	}

	private int apply(CommandContext<CommandSource> c, float scale) throws CommandSyntaxException {
		List<Player> players = players(c);
		if (players.isEmpty()) {
			c.getSource().sendMessage("§cNo matching players.");
			return 0;
		}
		return applyTo(c, players, scale);
	}

	private int applyTo(CommandContext<CommandSource> c, List<Player> players, float scale) {
		String size = PlayerScale.format(scale);
		float ability = abilityFor(scale, abilityPercent(c));

		String detail = "§7reach §f" + PlayerScale.format(ability)
			+ "x§7, jump §f" + PlayerScale.format(PlayerScale.jumpFrom(ability))
			+ "x§7, speed §f" + PlayerScale.format(PlayerScale.speedFrom(ability)) + "x";
		for (Player p : players) {
			PlayerScale.set(p, scale);
			if (p != c.getSource().getSender()) {
				c.getSource().sendMessage(p, "§eYou are now §f" + size + "x §esize — " + detail);
			}
		}
		if (players.size() == 1) {
			c.getSource().sendMessage("§aSet §f" + players.get(0).getDisplayName() + " §ato §f" + size + "x §asize — " + detail);
		} else {
			c.getSource().sendMessage("§aSet §f" + players.size() + " players §ato §f" + size + "x §asize — " + detail);
		}
		return players.size();
	}

	private int getSelf(CommandContext<CommandSource> c) {
		Player self = c.getSource().getSender();
		if (self == null) {
			c.getSource().sendMessage("§cName a player: /scaling get <players>");
			return 0;
		}
		report(c, self);
		return 1;
	}

	private int get(CommandContext<CommandSource> c) throws CommandSyntaxException {
		List<Player> players = players(c);
		if (players.isEmpty()) {
			c.getSource().sendMessage("§cNo matching players.");
			return 0;
		}
		for (Player p : players) report(c, p);
		return players.size();
	}

	private void report(CommandContext<CommandSource> c, Player p) {
		c.getSource().sendMessage("§f" + p.getDisplayName() + " §7is §f" + PlayerScale.format(PlayerScale.get(p))
			+ "x §7size — reach and jump §f" + PlayerScale.format(PlayerScale.abilityFactor(p))
			+ "x§7, speed §f" + PlayerScale.format(PlayerScale.speedFactor(p)) + "x");

		float bonus = PlayerScale.getBonus(p);
		if (bonus != 0.0f) {
			c.getSource().sendMessage("§7  §f" + PlayerScale.format(PlayerScale.getBase(p))
				+ "x §7set, §f" + (bonus > 0.0f ? "+" : "") + PlayerScale.format(bonus)
				+ "x §7eaten (lost on death)");
		}
	}

	private float abilityFor(float scale, int percent) {
		return PlayerScale.abilityFor(scale, percent);
	}

	private int abilityPercent(CommandContext<CommandSource> c) {
		Integer pct = c.getSource().getWorld().getGameRuleValue(ScalingRules.ABILITY_SCALING);
		return pct == null ? 100 : pct;
	}

	private int setAbility(CommandContext<CommandSource> c) {
		int pct = c.getArgument("percent", Integer.class);
		CommandSource s = c.getSource();
		s.getWorld().getLevelData().getGameRules().setValue(ScalingRules.ABILITY_SCALING, pct);
		s.sendPacketToAllPlayers(() -> new PacketGameRule(s.getWorld().getLevelData().getGameRules()));
		s.sendMessage("§aAbility scaling set to §f" + pct + "%§a. " + example(pct));
		return 1;
	}

	private int getAbility(CommandContext<CommandSource> c) {
		int pct = abilityPercent(c);
		c.getSource().sendMessage("§7Ability scaling is §f" + pct + "%§7. " + example(pct));
		return 1;
	}

	private String example(int percent) {
		float ability = abilityFor(2.0f, percent);
		return "§7A 2x player reaches §f" + PlayerScale.format(ability)
			+ "x§7, jumps §f" + PlayerScale.format(PlayerScale.jumpFrom(ability))
			+ "x§7 and moves §f" + PlayerScale.format(PlayerScale.speedFrom(ability)) + "x§7.";
	}
}
