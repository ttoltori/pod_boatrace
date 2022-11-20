package com.pengkong.boatrace.exp10.remote.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.common.enums.RemoteServiceStatus;
import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.Clazz;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;
import com.pengkong.boatrace.exp10.util.MLDescriptiveStatistics;
import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlClassificationExample;
import com.pengkong.common.MathUtil;

/**
 * リモートMLClassifireクラス。ML要求をwebsocket経由で行う.
 * @author ttolt
 *
 */
public class RemoteClassifier {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	Logger logger = LoggerFactory.getLogger(RemoteClassifier.class);

	/** req,resのid用カウンタ */
	private long counter = 0;

	/** リモート通信クラス(websocket) */
	RemoteCommunication rc;
	
	/** 記述統計値計算 */
	MLDescriptiveStatistics descStat = MLDescriptiveStatistics.getInstance();
	
	public RemoteClassifier() throws Exception {
		 rc = new RemoteCommunication();
	}

	public void connect() {
		rc.connect();
	}
	
	public void disconnect() {
		rc.disconnect();
	}
	
	/**
	 * DBからの取得値を基にリモートMLを実行して結果を取得する。
	 * @param mapper  ml_classificationへの参照。(classificationをDBから取得時）
	 * @param hashmap  DBからの取得値
	 * @return ML結果リスト
	 * @throws Exception
	 */
	public List<Classification> classify(MlClassificationMapper mapper, HashMap<String, ?> hashmap) throws Exception {

		RemoteRequestSet rrs = new RemoteRequestSet();
		for (int rankNo = 1; rankNo <= 3; rankNo++) {
			String classId = prop.getString("class_rank" + rankNo);
			// 固定値classificationの場合、通信せずに固定値の結果を返却する
			if (Clazz.isFixedClassification(classId)) {
				return createFixedClassification(classId);
			}
			
			// classIdが未定義(="x")ならループを抜ける
			if (Clazz.isUndefined(classId))
				break;

			RemoteRequest req = createRequest(rankNo, hashmap);
			rrs.addRequest(req);
			
			// 他モデル参照への要求ならDBから応答を生成する
			if (req.isReferentialRequest()) {
				RemoteResponse res = createResponseFromDB(rankNo, req, hashmap, mapper);
				rrs.receivedResponse(res);
			}
		}
		
		// 通信実行
		if (!rrs.isCompleted()) {
			rrs = rc.send(rrs);
		}
		
		// 通信結果返却
		return createResult(rrs);
	}

	/**
	 * ml_classificationから当該ranknoに対するclassification結果を取得して、RemoteResponseを生成する
	 * @param rankNo rankno
	 * @param req 要求
	 * @param hashmap classification対象のDB取得地
	 * @param mapper ml_classificationへの参照 
	 * @return
	 */
	RemoteResponse createResponseFromDB(int rankNo, RemoteRequest req, HashMap<String, ?> hashmap,
			MlClassificationMapper mapper) {
		RemoteRequestParam param = (RemoteRequestParam)req.param;
		MlClassificationExample example = new MlClassificationExample();
		example.createCriteria().andModelnoEqualTo(param.modelNo)
			.andPatternEqualTo(param.pattern)
			.andYmdEqualTo(param.ymd)
			.andJyocdEqualTo((String) hashmap.get("jyocd"))
			.andRacenoEqualTo(Short.valueOf(String.valueOf(hashmap.get("raceno"))));
		List<MlClassification> listRec = mapper.selectByExample(example);
		
		MlClassification clf = listRec.get(0);
		RemoteResponse res = new RemoteResponse();
		res.id = req.id;
		res.algorithmId = req.algorithmId;
		if (rankNo == 1) {
			res.values = (double[])clf.getProbabilities1();	
		} else if (rankNo == 2) {
			res.values = (double[])clf.getProbabilities2();
		} else {
			res.values = (double[])clf.getProbabilities3();
		}
		res.status = RemoteServiceStatus.OK.getValue();
		
		return res;
	}
	
	/**
	 * 固定値のclassificationを取得する.
	 * @param classId ex) fixed=123 
	 * @return ex) 123を予測値とするclassification
	 */
	List<Classification> createFixedClassification(String classId) {
		List<Classification> result = new ArrayList<>();
		Classification cls = new Classification();
		// ex) fixed=123  -> 123
		cls.prediction = classId.split(Delimeter.EQUAL.getValue())[1];
		cls.probabilities = new double[0];
		cls.probability = 1;
		cls.skewness = 0;
		cls.kurtosis = 0;
		result.add(cls);
		
		return result;
	}
	
	/**
	 * 現在の実験(model_config.id)のrankNoに対する予測要求パラメータを生成する。
	 * 
	 * @param rankNo
	 * @param hashMap DB取得データ
	 * @return {@link RemoteRequest}
	 * @throws Exception
	 */
	RemoteRequest createRequest(int rankNo, HashMap<String, ?> hashMap) throws Exception {
		/** モデル情報manager */
		ModelInfoManager mim = ModelInfoManager.getInstance();

		// 要求パラメータ生成
		RemoteRequestParam param = new RemoteRequestParam();
		param.exNo = prop.getString("model_no");
		param.pattern = (String) hashMap.get("pattern");
		param.rankNo = String.valueOf(rankNo);
		param.ymd = (String) hashMap.get("ymd"); // 要求対象年月日

		ModelInfo mi = mim.get(param.exNo, param.rankNo, param.pattern, param.ymd);
		param.modelNo = mi.modelNo;

		param.values = new String[mi.arffNames.length];
		for (int i = 0; i < mi.arffNames.length; i++) {
			// DB取得値をデータに設定する
			param.values[i] = String.valueOf(hashMap.get(mi.arffNames[i]));
		}

		// モデル向けの場合はモデルファイル名も設定する（pythonサーバ用)
		param.modelFileName = mi.fileName;

		String id = String.valueOf(counter++);
		return new RemoteRequest(id, mi.algorithmId, param);
	}

	/**
	 * リモートMLサービスに送受信結果を基に処理用結果リストを返却する
	 * @param rrs ML要求セット(最大３個の個別要求を保持する)
	 * @return ML要求セットに含まれる個別要求毎の結果のリスト
	 * @throws Exception
	 */
	List<Classification> createResult(RemoteRequestSet rrs) throws Exception {
		List<Classification> result = new ArrayList<>();
		
		// responseをidの古い順でソートする
		rrs.listRes.sort((RemoteResponse r1, RemoteResponse r2) -> (int)(Long.valueOf(r1.id) - Long.valueOf(r2.id)));
		for (int i = 0; i < rrs.listRes.size(); i++) {
			RemoteRequest req = rrs.listReq.get(i);
			RemoteResponse res = rrs.listRes.get(i);
			
			// 要求と応答のidが一致しない
			if (!req.id.equals(res.id)) {
				throw new IllegalStateException("ids of req,res are unmatch. reqid=" + req.id + "resid=" + res.id);
			}
			
			// responseがOKまはたREF以外
			if (!res.status.equals(RemoteServiceStatus.OK.getValue()) && !res.status.equals(RemoteServiceStatus.REF.getValue())) {
				throw new IllegalStateException("service result error. refer server log for detail." + res.status);
			}
			
			result.add(createClassification(req, res));
		}
		
		return result;
	}
	
	
	/** 
	 * リモートMLの結果をDB作業用の単位クラスに変換する
	 * @param req 要求データ
	 * @param res 結果データ
	 * @return 
	 * @throws Exception
	 */
	Classification createClassification(RemoteRequest req, RemoteResponse res) throws Exception {
		/** モデル情報manager */
		ModelInfoManager mim = ModelInfoManager.getInstance();

		Classification clsn = new Classification();
    	descStat.clear();
    	if (res.algorithmId.startsWith("cf_")) { // classification
    		descStat.addValues(res.values);
    		int indexOfMax = descStat.getIndexOfMax();
	
    		// モデル情報取得(classValues参照)
    		RemoteRequestParam param = (RemoteRequestParam) req.param;
    		ModelInfo mi = mim.get(param.exNo, param.rankNo, param.pattern, param.ymd);
    		clsn.prediction = mi.classValues[indexOfMax];
    		clsn.probability = MathUtil.scale3(res.values[indexOfMax]);
    		clsn.probabilities = new double[res.values.length];
        	for (int i=0; i < res.values.length; i++) {
        		clsn.probabilities[i] = MathUtil.scale3(res.values[i]);
        	}
        	clsn.skewness = MathUtil.scale3(descStat.getSkewness());
        	clsn.kurtosis = MathUtil.scale3(descStat.getKurtosis());

    		//result.prediction = mi.classValues
    	} else if (res.algorithmId.startsWith("rg_")) { // regression
    		clsn.prediction = "rg";
    		clsn.probability = MathUtil.scale3(res.values[0]);
    	}
		
		return clsn;
	}
	
	/**
	 * @param algorithmId ex) cf_lgbm_1_py
	 * @return true=pythonモデル false=javaモデル
	 */
	boolean isPythonModel(String algorithmId) {
		return (algorithmId.endsWith("_py"));
	}

	/**
	 * machine learning typeを取得する
	 * 
	 * @param algorithmId ex) cf_lgbm_1_py
	 * @return cf=classification rg=regression, その他は未定
	 */
	String getMLType(String algorithmId) {
		return algorithmId.split("_")[0];
	}

}
