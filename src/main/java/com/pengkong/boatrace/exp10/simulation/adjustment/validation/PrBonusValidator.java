package com.pengkong.boatrace.exp10.simulation.adjustment.validation;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * 予想確率でvalidationする。
 * !!!注意　probability_typeはdigit1で固定が前提 !!!
 * 
 * @author ttolt
 *
 */
public class PrBonusValidator extends AbstractBonusValidator {
	@Override
	Double getFactorValue(DBRecord rec) {
		return rec.getDouble("probability1");
	}
}
