package com.pengkong.boatrace.exp10.factory;

import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.remote.server.classifier.AbstractBoatClassifier;

/**
 * BoatClassifierFactory interface
 * @author ttolt
 *
 */
public abstract class AbstractBoatClassifierFactory {
	public abstract AbstractBoatClassifier create(ModelInfo mi);
}
