package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import java.util.List;

import com.pengkong.boatrace.exp10.simulation.data.AbstractMultiClassificationDataLoader;
import com.pengkong.boatrace.exp10.simulation.data.rmi.server.RmiDataServerInterface;
import com.pengkong.boatrace.mybatis.entity.MlClassification;

/**
 * db dataをrmi data serverから取得する.
 * @author ttolt
 *
 */
public class RmiMultiClassificationDataProvider extends AbstractMultiClassificationDataLoader {

	RmiDataServerInterface rmiClient = DataServerClinet.getInstance();
	
	@Override
	public List<MlClassification> load(List<String> modelNoList, String fromYmd, String toYmd) throws Exception {
		// サーバー側の保持データを一括取得するため、fromYmd,toYmdは使用しない
		return rmiClient.getClassificationData(modelNoList);
	}
}
