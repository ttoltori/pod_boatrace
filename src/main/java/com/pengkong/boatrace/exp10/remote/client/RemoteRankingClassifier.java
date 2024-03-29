package com.pengkong.boatrace.exp10.remote.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
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
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlClassificationExample;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.service.stat.WakuValue;
import com.pengkong.common.MathUtil;

/**
 * リモートMLClassifireクラス。ML要求をwebsocket経由で行う.
 * Ranker모델을 사용하므로 request의 데이터가 복수행이어야 한다.
 * @author ttolt
 *
 */
public class RemoteRankingClassifier {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	Logger logger = LoggerFactory.getLogger(RemoteRankingClassifier.class);

	/** req,resのid用カウンタ */
	private long counter = 0;

	/** リモート通信クラス(websocket) */
	RemoteCommunication rc;
	
	/** 記述統計値計算 */
	MLDescriptiveStatistics descStat = MLDescriptiveStatistics.getInstance();
	
	public RemoteRankingClassifier() throws Exception {
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
	public List<Classification> classify(SqlSession session, List<DBRecord> hashmapList) throws Exception {

		RemoteRequestSet rrs = new RemoteRequestSet();
		for (int rankNo = 1; rankNo <= 1; rankNo++) {
			String classId = prop.getString("class_rank" + rankNo);
			// 固定値classificationの場合、通信せずに固定値の結果を返却する
			if (Clazz.isFixedClassification(classId)) {
				return createFixedClassification(classId);
			}
			
			// classIdが未定義(="x")ならループを抜ける
			if (Clazz.isUndefined(classId))
				break;

			RemoteRequest req = createRequest(rankNo, hashmapList);
			rrs.addRequest(req);
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
			SqlSession session) {
		MlClassificationMapper mapper = session.getMapper(MlClassificationMapper.class);
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
		cls.probability = 1;
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
	RemoteRequest createRequest(int rankNo, List<DBRecord> hashMapList) throws Exception {
		/** モデル情報manager */
		ModelInfoManager mim = ModelInfoManager.getInstance();

		// 要求パラメータ生成
		RemoteRequestParam param = new RemoteRequestParam();
		param.exNo = prop.getString("model_no");
		param.pattern = (String) hashMapList.get(0).get("pattern");
		param.rankNo = String.valueOf(rankNo);
		param.ymd = (String) hashMapList.get(0).get("ymd"); // 要求対象年月日

		ModelInfo mi = mim.get(param.exNo, param.rankNo, param.pattern, param.ymd);
		param.modelNo = mi.modelNo;

		if  (hashMapList.size() != 6) {
			throw new  IllegalStateException("db data for ranker mode needs to be 6 record.  actual size="  +  hashMapList.size());
		}
		param.values = new String[hashMapList.size()];
		
		for (int i = 0;  i < hashMapList.size();  i++) {
			HashMap<String, ?> rec  = hashMapList.get(i);
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < mi.arffNames.length; j++) {
				// DB取得値をデータに設定する
				sb.append( String.valueOf(rec.get(mi.arffNames[j])));
				sb.append(",");
			}
			param.values[i] = sb.substring(0, sb.length()-1);
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
			
			result.addAll(createRankingClassifications(req, res));
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
	List<Classification> createRankingClassifications(RemoteRequest req, RemoteResponse res) throws Exception {
		List<Classification> result = new ArrayList<>();
		List<WakuScore> wakus = new ArrayList<>();
		for (int i = 0; i < res.values.length; i++) {
			wakus.add(new WakuScore(String.valueOf(i+1), res.values[i]));
		}
		
		// score順にソート
		Collections.sort(wakus, new WakuScoreReverseComparator());
		
		for(WakuScore ws : wakus) {
			Classification clsn = new Classification();
			clsn.prediction = ws.waku;
			clsn.probability = ws.score;
			result.add(clsn);
		}
		
		return result;
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

	private class WakuScore {
		public String waku;
		public Double score;
		public WakuScore(String waku, double score) {
			super();
			this.waku = waku;
			this.score = score;
		}
		public Double getScore() {
			return score;
		}
	}

	private class WakuScoreReverseComparator implements Comparator<WakuScore> {

		public WakuScoreReverseComparator() {
		}

		@Override
		public int compare(WakuScore o1, WakuScore o2) {
			return o2.getScore().compareTo(o1.getScore());
		}
	}
}
