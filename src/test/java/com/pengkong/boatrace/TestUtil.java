package com.pengkong.boatrace;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class TestUtil {

	public static RemoteRequestParam createRemoteRequestParam(String exNo, String modelNo, String rankNo, String pattern, String ymd,
			String[] values) {
		RemoteRequestParam param = new RemoteRequestParam();
		param.values = values;
		param.exNo = exNo;
		param.modelNo = modelNo;
		param.rankNo = rankNo;
		param.pattern = pattern;
		param.ymd = ymd;
		param.modelFileName = "00001_nopattern_20151231_rank1.model";
		
		return param;
	}
	
	public static RemoteRequest createRemoteRequest(String id, String algorithmId, String exNo, String modelNo, String rankNo, String pattern, String ymd,
			String[] values) {
		
		return new RemoteRequest(id, algorithmId, createRemoteRequestParam(exNo, modelNo, rankNo, pattern, ymd, values));
	}

	// RemoteRequest string  = {"id":"id1","algorithmId":"cf_bayesnet-1_wk","param":{"exNo":"3","modelNo":"3","rankNo":"3","pattern":"nopattern","ymd":"20162015","modelFileName":"00001_nopattern_20151231_rank1.model","values":["4546","3707","4738","4882","4746","4845"]}}
	// RemoteResponse string = {"id":"ad1","algorithmId":"cf_bayesnet-1_wk","values":[1.0],"status":"OK"}

	public static DBRecord createDBRecord(String[] predictions) {
		DBRecord rec = new DBRecord();
		rec.put("result_no", "1");
		rec.put("modelno", "1");
		rec.put("ymd", "20211220");
		rec.put("jyocd", "1");
		rec.put("raceno", Integer.valueOf(1));
		rec.put("sime", "11:11");
		rec.put("pattern", "nopattern");
		rec.put("sanrentanno", "123");
		rec.put("tansyono", "1");
		rec.put("nirentanno", "12");
		rec.put("nirenhukuno", "12");
		rec.put("sanrentanno", "123");
		rec.put("sanrenhukuno", "123");
		rec.put("tansyoprize", Integer.valueOf(110));
		rec.put("nirentanprize", Integer.valueOf(210));
		rec.put("nirenhukuprize", Integer.valueOf(210));
		rec.put("sanrentanprize", Integer.valueOf(310));
		rec.put("sanrenhukuprize", Integer.valueOf(310));
		rec.put("tansyopopular", Integer.valueOf(1));
		rec.put("nirentanpopular", Integer.valueOf(1));
		rec.put("nirenhukupopular", Integer.valueOf(1));
		rec.put("sanrentanpopular", Integer.valueOf(1));
		rec.put("sanrenhukupopular", Integer.valueOf(1));
		rec.put("prediction1", predictions[0]);
		rec.put("prediction2", predictions[1]);
		rec.put("prediction3", predictions[2]);
		
		return rec;
	}
	
	public static List<MlResult> createResultList(int count) {
		List<MlResult> results = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			MlResult result = new MlResult();
			results.add(result);
		}
		
		return results;
	}
}
