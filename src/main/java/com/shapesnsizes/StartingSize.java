package com.shapesnsizes;

import java.util.Random;

public enum StartingSize {

	RANDOM(-1.0f),
	X0_4(0.4f),
	X0_5(0.5f),
	X0_66(0.66f),
	X0_75(0.75f),

	NORMAL(1.0f),
	X1_25(1.25f),
	X1_5(1.5f),
	X2(2.0f),
	X2_5(2.5f);

	private final float scale;

	StartingSize(float scale) {
		this.scale = scale;
	}

	public static final float RANDOM_MIN = X0_4.scale;
	public static final float RANDOM_MAX = X2_5.scale;

	public float pick(Random random) {
		if (this.scale > 0.0f) return this.scale;
		double lo = Math.log(RANDOM_MIN);
		double hi = Math.log(RANDOM_MAX);
		float drawn = (float) Math.exp(lo + random.nextDouble() * (hi - lo));
		return Math.round(drawn * 100.0f) / 100.0f;
	}
}
