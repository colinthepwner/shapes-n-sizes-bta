package com.shapesnsizes;

public interface SizeTicker {

	void shapesnsizes$sizeTick();

	double shapesnsizes$stepX();

	double shapesnsizes$stepZ();

	default double shapesnsizes$stepLength() {
		double dx = shapesnsizes$stepX();
		double dz = shapesnsizes$stepZ();
		return Math.sqrt(dx * dx + dz * dz);
	}
}
