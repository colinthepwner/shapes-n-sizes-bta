package com.shapesnsizes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import net.minecraft.core.net.PropertyManager;

public final class ShapesConfig {
	private static final String FILE_NAME = "shapesnsizes.properties";
	private static final String PLAYER_PREFIX = "player.";
	private static final String DEFAULT_KEY = "default-scale";

	private static final Map<String, Float> PLAYER_SCALES = new HashMap<>();
	private static float defaultScale = PlayerScale.DEFAULT;

	public static final String SERVER_PROPERTY = "starting-size";
	private static final String SERVER_PROPERTY_DEFAULT = "1";
	private static final String SERVER_PROPERTY_RANDOM = "random";

	private static float serverFixed = PlayerScale.DEFAULT;
	private static boolean serverRandom = false;

	private ShapesConfig() {}

	public static float startingScale(String username) {
		if (username == null) return defaultScale;
		Float named = PLAYER_SCALES.get(username.toLowerCase(Locale.ROOT));
		return named == null ? defaultScale : named;
	}

	public static Float namedScale(String username) {
		return username == null ? null : PLAYER_SCALES.get(username.toLowerCase(Locale.ROOT));
	}

	public static void loadServerProperty(PropertyManager properties) {
		serverFixed = PlayerScale.DEFAULT;
		serverRandom = false;
		String raw = properties.getStringProperty(SERVER_PROPERTY, SERVER_PROPERTY_DEFAULT).trim();
		if (raw.equalsIgnoreCase(SERVER_PROPERTY_RANDOM)) {
			serverRandom = true;
			ShapesNSizes.LOGGER.info("server.properties: new players start at a random size, {}x to {}x.",
				PlayerScale.format(StartingSize.RANDOM_MIN), PlayerScale.format(StartingSize.RANDOM_MAX));
			return;
		}
		try {
			serverFixed = PlayerScale.clamp(Float.parseFloat(raw));
		} catch (NumberFormatException e) {
			ShapesNSizes.LOGGER.warn("server.properties: {}='{}' is not a number or 'random'; new players start at normal size.",
				SERVER_PROPERTY, raw);
			return;
		}
		if (serverFixed != PlayerScale.DEFAULT) {
			ShapesNSizes.LOGGER.info("server.properties: new players start at {}x.", PlayerScale.format(serverFixed));
		}
	}

	public static boolean serverIsRandom() {
		return serverRandom;
	}

	public static float serverStartingScale(Random random) {
		return serverRandom ? StartingSize.RANDOM.pick(random) : serverFixed;
	}

	public static boolean hasAnything() {
		return defaultScale != PlayerScale.DEFAULT || !PLAYER_SCALES.isEmpty();
	}

	public static int namedCount() {
		return PLAYER_SCALES.size();
	}

	public static float defaultScale() {
		return defaultScale;
	}

	public static void load() {
		PLAYER_SCALES.clear();
		defaultScale = PlayerScale.DEFAULT;

		File file = new File(new File("config"), FILE_NAME);
		try {
			if (!file.exists()) {
				writeTemplate(file);
				ShapesNSizes.LOGGER.info("Wrote a starting {} -- edit it to give players default sizes.", file.getPath());
				return;
			}
			Properties props = new Properties();
			try (InputStream in = Files.newInputStream(file.toPath())) {
				props.load(in);
			}
			for (String key : props.stringPropertyNames()) {
				String value = props.getProperty(key).trim();
				if (value.isEmpty()) continue;
				float parsed;
				try {
					parsed = PlayerScale.clamp(Float.parseFloat(value));
				} catch (NumberFormatException e) {
					ShapesNSizes.LOGGER.warn("{}: '{}' is not a number, ignoring the line for '{}'.", FILE_NAME, value, key);
					continue;
				}
				if (key.equalsIgnoreCase(DEFAULT_KEY)) {
					defaultScale = parsed;
				} else if (key.toLowerCase(Locale.ROOT).startsWith(PLAYER_PREFIX)) {
					PLAYER_SCALES.put(key.substring(PLAYER_PREFIX.length()).toLowerCase(Locale.ROOT), parsed);
				} else {
					ShapesNSizes.LOGGER.warn("{}: ignoring unknown setting '{}'.", FILE_NAME, key);
				}
			}
			if (hasAnything()) {
				ShapesNSizes.LOGGER.info("Starting sizes: default {}x, {} player(s) named.",
					PlayerScale.format(defaultScale), PLAYER_SCALES.size());
			}
		} catch (IOException e) {

			ShapesNSizes.LOGGER.warn("Could not read {}; every player will start at normal size.", file.getPath(), e);
		}
	}

	private static void writeTemplate(File file) throws IOException {
		File dir = file.getParentFile();
		if (dir != null && !dir.exists() && !dir.mkdirs()) {
			throw new IOException("could not create " + dir.getPath());
		}
		try (Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			out.write("# Shapes n Sizes -- the sizes players start at.\n");
			out.write("#\n");
			out.write("# These apply on login, and only to a player this server has no size on record\n");
			out.write("# for -- their first join. After that a player keeps whatever size they have,\n");
			out.write("# so /scale sticks and nobody springs back here when they reconnect.\n");
			out.write("#\n");
			out.write("# Sizes run from " + PlayerScale.format(PlayerScale.MIN)
				+ " to " + PlayerScale.format(PlayerScale.MAX) + ". 1 is normal.\n");
			out.write("# Run /scaling reload after editing to pick changes up without a restart.\n");
			out.write("\n");
			out.write("# Everyone not named below.\n");
			out.write(DEFAULT_KEY + " = 1.0\n");
			out.write("\n");
			out.write("# One line per player, by username, case does not matter.\n");
			out.write("#" + PLAYER_PREFIX + "SomePlayer = 0.5\n");
			out.write("#" + PLAYER_PREFIX + "AnotherPlayer = 3\n");
		}
	}
}
