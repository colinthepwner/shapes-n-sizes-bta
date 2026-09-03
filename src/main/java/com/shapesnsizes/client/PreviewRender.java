package com.shapesnsizes.client;

import com.shapesnsizes.PlayerScale;

public final class PreviewRender {

	public static boolean active = false;

	private static final float CEILING = 1.3f;

	private static final float DAMPING = 0.1f;

	private PreviewRender() {}

	public static float portraitScale(float scale) {
		if (scale <= PlayerScale.DEFAULT) return scale;
		return Math.min(CEILING, 1.0f + (scale - 1.0f) * DAMPING);
	}
}
