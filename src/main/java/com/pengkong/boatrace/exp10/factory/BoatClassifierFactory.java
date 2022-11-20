package com.pengkong.boatrace.exp10.factory;

import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.remote.server.classifier.AbstractBoatClassifier;
import com.pengkong.boatrace.exp10.remote.server.classifier.BoatClassifier;

public class BoatClassifierFactory extends AbstractBoatClassifierFactory {
	public AbstractBoatClassifier create(ModelInfo mi) {
		return new BoatClassifier(mi);
	}
}
