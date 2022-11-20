package com.pengkong.boatrace.exp10.remote.server.classifier;

import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;

/**
 * classification, regression 共有のclassificationメソッドを定義する
 * 
 * @author ttolt
 *
 */
public abstract class AbstractBoatClassifier {
	/** 
	 * classification実行結果を各class value別の確率値で返却する。
	 * @param req classification要求
	 * @return classification確率値。ex) class values=1,2,3,4,5,6の場合 [0.6, 0.1, 0.2, 0.05, 0.05, 0.001]
	 * @throws Exception
	 */
	public abstract double[] predictProba(RemoteRequestParam req) throws Exception;
}
