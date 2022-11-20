package com.pengkong.boatrace.exp10.remote.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.RemoteServiceStatus;
import com.pengkong.boatrace.exp10.factory.AbstractBoatClassifierFactory;
import com.pengkong.boatrace.exp10.factory.BoatClassifierFactory;
import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;
import com.pengkong.boatrace.exp10.remote.server.classifier.AbstractBoatClassifier;
import com.pengkong.boatrace.exp10.remote.server.classifier.ClassifierQueue;

public class ClassificationService extends AbstractService {
	Logger logger = LoggerFactory.getLogger(ClassificationService.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** classifier queue */
	ClassifierQueue queue;

	/** モデルファイル情報マネージャ */
	ModelInfoManager mim = ModelInfoManager.getInstance();

	/** factory of classifier */
	AbstractBoatClassifierFactory classifierFactory = new BoatClassifierFactory();
	
	private static class InstanceHolder {
		private static final ClassificationService INSTANCE = new ClassificationService();
	}

	public static ClassificationService getInstance() {
		return InstanceHolder.INSTANCE;
	}

	boolean isInitialized = false;
	
	private void initialize() {
		// queue初期化
		queue = new ClassifierQueue(prop.getInteger("classifier_queue_max"));
	}

	private void ensureInitialized() {
		if (!isInitialized) {
			initialize();
			isInitialized = true;
		}
	}

	@Override
	public RemoteResponse execute(RemoteRequest req) throws Exception {
		ensureInitialized();
		
		// param(JsonNode)をクラス変換してサービスを実行する
		RemoteRequestParam reqParam = (RemoteRequestParam)req.param;
		
		// ML実行
		double[] values = classify(reqParam);
		
		return new RemoteResponse(req.id, req.algorithmId, values, RemoteServiceStatus.OK.getValue());
	}

	public void setClassifierFactory(AbstractBoatClassifierFactory factory) {
		this.classifierFactory = factory;
	}

	double[] classify(RemoteRequestParam param) throws Exception {
		// モデルファイル情報を取得
		ModelInfo mi = mim.get(param.exNo, param.rankNo, param.pattern, param.ymd);

		// classifier取得 key = filename
		// ex) 1_nopattern_20151231_rank1.model
		AbstractBoatClassifier clf = queue.get(param.modelFileName);

		// classifierがキューに存在しない場合
		if (clf == null) {
			// モデルファイルからClassifierをロードする
			clf = classifierFactory.create(mi);

			// queueに登録する
			queue.add(param.modelFileName, clf);
		}

		return clf.predictProba(param);
	}
}
