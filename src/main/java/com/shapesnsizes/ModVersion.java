package com.shapesnsizes;

import java.nio.charset.StandardCharsets;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.net.packet.PacketCustomPayload;

public final class ModVersion {

	public static final String CHANNEL = "SnS:Version";

	public static final String KICK_MISMATCH = "Outdated Shapes N Sizes";
	public static final String KICK_MISSING = "This server requires Shapes N Sizes";

	private static String version = null;

	private ModVersion() {}

	public static String get() {
		if (version == null) {
			version = FabricLoader.getInstance()
				.getModContainer(ShapesNSizes.MOD_ID)
				.map(c -> c.getMetadata().getVersion().getFriendlyString())
				.orElse("unknown");
		}
		return version;
	}

	public static PacketCustomPayload hello() {
		return new PacketCustomPayload(CHANNEL, get().getBytes(StandardCharsets.UTF_8));
	}

	public static String read(PacketCustomPayload packet) {
		if (packet.data == null || packet.data.length == 0) return "";
		return new String(packet.data, StandardCharsets.UTF_8);
	}
}
