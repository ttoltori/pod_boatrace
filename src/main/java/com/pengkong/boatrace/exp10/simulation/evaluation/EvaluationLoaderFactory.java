package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.lang.reflect.Constructor;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class EvaluationLoaderFactory {
	public static AbstractEvaluationLoader create() throws Exception{
		String className = MLPropertyUtil.getInstance().getString("evaluation_loader");
		String clazz = "com.pengkong.boatrace.exp10.simulation.evaluation." + className;
		Constructor<?> c = Class.forName(clazz).getConstructor();
		
		return (AbstractEvaluationLoader) c.newInstance();
	}
}
