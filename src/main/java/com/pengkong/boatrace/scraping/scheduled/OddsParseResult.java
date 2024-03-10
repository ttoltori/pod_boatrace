package com.pengkong.boatrace.scraping.scheduled;

import java.util.Map;

import com.pengkong.boatrace.common.enums.BetType;

import lombok.Getter;
import lombok.Setter;

/**
 * Oddsページのパーシング結果クラス.
 * 
 * @author ttolt
 *
 */
@SuppressWarnings("unchecked")
public class OddsParseResult {
	
	@Getter
	@Setter
	/**
	 * ex) {"1T", {"1", 1.4}, {"2", 1.3} },
	 *     {"1F", {"1", [1.4, 1.6]}, {"2", [1.3, 1.7]} },
	 */
	private Map<BetType, Object> data;
	
	public OddsParseResult() {
	}

	public Map<String, Float> getOddsMap(BetType betType) {
		return (Map<String, Float>) data.get(betType);
	}
	
	public Map<String, Float[]> getOddsArrayMap(BetType betType) {
		return (Map<String, Float[]>) data.get(betType);
	}
}
