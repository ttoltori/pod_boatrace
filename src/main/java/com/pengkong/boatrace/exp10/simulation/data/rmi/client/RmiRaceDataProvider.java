package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import java.util.List;

import com.pengkong.boatrace.exp10.simulation.data.AbstractRaceDataLoader;
import com.pengkong.boatrace.exp10.simulation.data.rmi.server.RmiDataServerInterface;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * db dataをrmi data serverから取得する.
 * @author ttolt
 *
 */
public class RmiRaceDataProvider extends AbstractRaceDataLoader {

	RmiDataServerInterface rmiClient = DataServerClinet.getInstance();
	
	@Override
	protected List<DBRecord> excute(String fromYmd, String toYmd) throws Exception {
		return rmiClient.getSimulationData(fromYmd, toYmd);
	}
}
