package com.pengkong.boatrace.exp10.simulation.pattern;

/**
 * 임의의 데이터로부터 패턴을 추출하기 위한 추상클래스. pattern.tsv가 추가되었다면 여기에 대응 처리를 기술하여야한다.
 * 
 * @author ttolt
 *
 */
public abstract class AbstractPatternWrapper {
	/**
	 * 패턴을 취득한다.
	 * 
	 * @param id 패턴 id ex) prob1+waku12
	 * @return 데이터가 보유하고 있는 패턴값
	 * @throws IllegalStateException
	 */
	public String getPattern(String id) throws IllegalStateException {

		switch (id) {
		// # 레이스 정보 중심
		case "jyo":
			return jyo();
		case "turn":
			return turn();
		case "race":
			return race();
		case "grade":
			return grade();
		case "rtype":
			return rtype();
		case "alcnt":
			return alcnt();
		case "jyo+turn":
			return String.join("-", jyo(), turn());
		case "jyo+grade":
			return String.join("-", jyo(), grade());
		case "jyo+rtype":
			return String.join("-", jyo(), rtype());
		case "jyo+alcnt":
			return String.join("-", jyo(), alcnt());
		case "turn+race":
			return String.join("-", turn(), race());
		case "turn+grade":
			return String.join("-", turn(), grade());
		case "turn+rtype":
			return String.join("-", turn(), rtype());
		case "turn+alcnt":
			return String.join("-", turn(), alcnt());
		case "race+grade":
			return String.join("-", race(), grade());
		case "race+rtype":
			return String.join("-", race(), rtype());
		case "race+alcnt":
			return String.join("-", race(), alcnt());
		case "grade+rtype":
			return String.join("-", grade(), rtype());
		case "grade+alcnt":
			return String.join("-", grade(), alcnt());
		case "rtype+alcnt":
			return String.join("-", rtype(), alcnt());
		// # 확률
		case "prob1":
			return prob1();
		case "prob1-2dig":
			return prob12dig();
		case "prob12":
			return prob12();
		case "prob123":
			return prob123();
		// # 레벨
		case "wk1":
			return wk1();
		case "wk12":
			return wk12();
		case "wk123":
			return wk123();
		case "wk1234":
			return wk1234();
		// #전국승률
		case "nw1":
			return nw1();
		case "nw1-2dig":
			return nw12dig();
		case "nw12":
			return nw12();
		case "nw123":
			return nw123();
		// # ML예측
		case "pd12":
			return pd12();
		case "pd123":
			return pd123();
		
		// # 키 항목간 조합 1단위+1단위
		case "prob1+wk1":
			return String.join("-", prob1(), wk1());
		case "prob1+nw1":
			return String.join("-", prob1(), nw1());
		case "wk1+nw1":
			return String.join("-", wk1(), nw1());
		
		// # 키 항목간 조합 1단위+2단위
		case "prob1+wk12":
			return String.join("-", prob1(), wk12());
		case "prob1+nw12":
			return String.join("-", prob1(), nw12());
		case "prob1+pd12":
			return String.join("-", prob1(), pd12());
		case "wk1+prob12":
			return String.join("-", wk1(), prob12());
		case "wk1+nw12":
			return String.join("-", wk1(), nw12());
		case "wk1+pd12":
			return String.join("-", wk1(), pd12());
		case "nw1+prob12":
			return String.join("-", nw1(), prob12());
		case "nw1+wk12":
			return String.join("-", nw1(), wk12());
		case "nw1+pd12":
			return String.join("-", nw1(), pd12());
		
		// # 키 항목간 조합 2단위+2단위
		case "wk12+prob12":
			return String.join("-", wk12(), prob12());
		case "prob12+nw12":
			return String.join("-", prob12(), nw12());
		case "prob12+pd12":
			return String.join("-", prob12(), pd12());
		case "wk12+nw12":
			return String.join("-", wk12(), nw12());
		case "wk12+pd12":
			return String.join("-", wk12(), pd12());
		case "nw12+pd12":
			return String.join("-", nw12(), pd12());
		
		// # 키 항목간 조합 1단위+3단위
		case "prob1+wk123":
			return String.join("-", prob1(), wk123());
		case "prob1+nw123":
			return String.join("-", prob1(), nw123());
		case "prob1+pd123":
			return String.join("-", prob1(), pd123());
		case "wk1+prob123":
			return String.join("-", wk1(), prob123());
		case "wk1+nw123":
			return String.join("-", wk1(), nw123());
		case "wk1+pd123":
			return String.join("-", wk1(), pd123());
		case "nw1+prob123":
			return String.join("-", nw1(), prob123());
		case "nw1+wk123":
			return String.join("-", nw1(), wk123());
		case "nw1+pd123":
			return String.join("-", nw1(), pd123());
		
		// # 확률 1단위 + 레이스정보
		case "prob1+jyo":
			return String.join("-", prob1(), jyo());
		case "prob1+turn":
			return String.join("-", prob1(), turn());
		case "prob1+race":
			return String.join("-", prob1(), race());
		case "prob1+grade":
			return String.join("-", prob1(), grade());
		case "prob1+rtype":
			return String.join("-", prob1(), rtype());
		case "prob1+alcnt":
			return String.join("-", prob1(), alcnt());
		
		// # 레벨 1단위 + 레이스정보
		case "wk1+jyo":
			return String.join("-", wk1(), jyo());
		case "wk1+turn":
			return String.join("-", wk1(), turn());
		case "wk1+race":
			return String.join("-", wk1(), race());
		case "wk1+grade":
			return String.join("-", wk1(), grade());
		case "wk1+rtype":
			return String.join("-", wk1(), rtype());
		case "wk1+alcnt":
			return String.join("-", wk1(), alcnt());

		// # 전국승률 1단위 + 레이스정보
		case "nw1+jyo":
			return String.join("-", nw1(), jyo());
		case "nw1+turn":
			return String.join("-", nw1(), turn());
		case "nw1+race":
			return String.join("-", nw1(), race());
		case "nw1+grade":
			return String.join("-", nw1(), grade());
		case "nw1+rtype":
			return String.join("-", nw1(), rtype());
		case "nw1+alcnt":
			return String.join("-", nw1(), alcnt());
			
		// # ML예측 2단위 + 레이스정보
		case "pd12+jyo":
			return String.join("-", pd12(), jyo());
		case "pd12+turn":
			return String.join("-", pd12(), turn());
		case "pd12+race":
			return String.join("-", pd12(), race());
		case "pd12+grade":
			return String.join("-", pd12(), grade());
		case "pd12+rtype":
			return String.join("-", pd12(), rtype());
		case "pd12+alcnt":
			return String.join("-", pd12(), alcnt());
	
		// # 확률 2단위 + 레이스정보
		case "prob12+jyo":
			return String.join("-", prob12(), jyo());
		case "prob12+turn":
			return String.join("-", prob12(), turn());
		case "prob12+race":
			return String.join("-", prob12(), race());
		case "prob12+grade":
			return String.join("-", prob12(), grade());
		case "prob12+rtype":
			return String.join("-", prob12(), rtype());
		case "prob12+alcnt":
			return String.join("-", prob12(), alcnt());
			
		// # 레벨 2단위 + 레이스정보
		case "wk12+jyo":
			return String.join("-", wk12(), jyo());
		case "wk12+turn":
			return String.join("-", wk12(), turn());
		case "wk12+race":
			return String.join("-", wk12(), race());
		case "wk12+grade":
			return String.join("-", wk12(), grade());
		case "wk12+rtype":
			return String.join("-", wk12(), rtype());
		case "wk12+alcnt":
			return String.join("-", wk12(), alcnt());
			
		// # 전국승률 2단위 + 레이스정보
		case "nw12+jyo":
			return String.join("-", nw12(), jyo());
		case "nw12+turn":
			return String.join("-", nw12(), turn());
		case "nw12+race":
			return String.join("-", nw12(), race());
		case "nw12+grade":
			return String.join("-", nw12(), grade());
		case "nw12+rtype":
			return String.join("-", nw12(), rtype());
		case "nw12+alcnt":
			return String.join("-", nw12(), alcnt());
			
		// # 레벨 3단위 + 레이스정보
		case "wk123+jyo":
			return String.join("-", wk123(), jyo());
		case "wk123+turn":
			return String.join("-", wk123(), turn());
		case "wk123+race":
			return String.join("-", wk123(), race());
		case "wk123+grade":
			return String.join("-", wk123(), grade());
		case "wk123+rtype":
			return String.join("-", wk123(), rtype());
		case "wk123+alcnt":
			return String.join("-", wk123(), alcnt());

		// # 키항목 1단위 레이스정보 2단위
		case "prob1+turn+grade":
			return String.join("-", prob1(), turn(), grade());
		case "wk1+turn+grade":
			return String.join("-", wk1(), turn(), grade());
		case "nw1+turn+grade":
			return String.join("-", nw1(), turn(), grade());
		case "prob1+turn+rtype":
			return String.join("-", prob1(), turn(), rtype());
		case "wk1+turn+rtype":
			return String.join("-", wk1(), turn(), rtype());
		case "nw1+turn+rtype":
			return String.join("-", nw1(), turn(), rtype());
		case "prob1+turn+alcnt":
			return String.join("-", prob1(), turn(), alcnt());
		case "wk1+turn+alcnt":
			return String.join("-", wk1(), turn(), alcnt());
		case "nw1+turn+alcnt":
			return String.join("-", nw1(), turn(), alcnt());
		case "prob1+grade+rtype":
			return String.join("-", prob1(), grade(), rtype());
		case "wk1+grade+rtype":
			return String.join("-", wk1(), grade(), rtype());
		case "nw1+grade+rtype":
			return String.join("-", nw1(), grade(), rtype());
		case "prob1+grade+alcnt":
			return String.join("-", prob1(), grade(), alcnt());
		case "wk1+grade+alcnt":
			return String.join("-", wk1(), grade(), alcnt());
		case "nw1+grade+alcnt":
			return String.join("-", nw1(), grade(), alcnt());
		case "prob1+rtype+alcnt":
			return String.join("-", prob1(), rtype(), alcnt());
		case "wk1+rtype+alcnt":
			return String.join("-", wk1(), rtype(), alcnt());
		case "nw1+rtype+alcnt":
			return String.join("-", nw1(), rtype(), alcnt());
		// 20221030 追加
		case "pd12+wk1":
			return String.join("-", pd12(), wk1());
		case "pd12+wk12":
			return String.join("-", pd12(), wk12());
		case "pd12+wk123":
			return String.join("-", pd12(), wk123());
		case "pd123+wk1":
			return String.join("-", pd123(), wk1());
		case "pd123+wk12":
			return String.join("-", pd123(), wk12());
		case "pd123+wk123":
			return String.join("-", pd123(), wk123());
		// 20221113 追加
		case "wk13":
			return String.join("-", wk1(), wk3());
		case "wk14":
			return String.join("-", wk1(), wk4());
		case "wk15":
			return String.join("-", wk1(), wk5());
		case "wk125":
			return String.join("-", wk1(), wk2(), wk5());
		// 20230212 追加
		case "wk12345":
			return String.join("-", wk1(), wk2(), wk3(), wk4(), wk5());
		case "wk123456":
			return String.join("-", wk1(), wk2(), wk3(), wk4(), wk5(), wk6());
		case "wk1+jyo+race":
			return String.join("-", wk1(), jyo(), race());
		case "wk1+jyo+turn":
			return String.join("-", wk1(), jyo(), turn());
		case "wk12+turn+race":
			return String.join("-", wk12(), turn(), race());
		case "wk12+jyo+race":
			return String.join("-", wk12(), jyo(), race());
			
		// 20230411 追加	
		case "wk123+fx+tm":
			return String.join("-", wk123(), getFixedEntrance(), getTimezone());
		case "comp":
			return getComPredict();

		case "cond1":
			return getCond1();
		case "n1pt1":
			return getN1point1();
		case "n1ptwaku1":
			return getN1pointWaku1();
			
		// 패턴 없음
		case "nopattern":
			return "nopattern";
		default:
			throw new IllegalStateException("unidentified pattern id. " + id);
		}
	}

	/**
	 * 部分文字列取得。長さが範囲してより短い場合は原本をそのまま返還する。
	 * 
	 * @param src   原本
	 * @param start 開始インデックス
	 * @param end   終了インデックス
	 * @return 部分文字列 String#substring(start, end)
	 */
	String subStringDouble(double value, int start, int end) {
		String src = String.valueOf(value);
		if (src.length() < (end - start)) {
			return src;
		}

		return src.substring(start, end);
	}
	
	String jyo() { return getJyo(); }
	String turn() { return getTurn(); }
	String race() { return getRaceNo(); }
	String grade() { return getGrade(); }
	String rtype() { return getRtype(); }
	String alcnt() { return getAlcnt(); }

	
	String prob1() {
		return subStringDouble(getProbability1(), 0, 3);
	}
	String prob12dig() {
		return subStringDouble(getProbability1(), 0, 4);
	}
	
	String prob12() {
		return String.join("-", subStringDouble(getProbability1(), 0, 3), subStringDouble(getProbability2(), 0, 3));
	}

	String prob123() {
		return String.join("-", subStringDouble(getProbability1(), 0, 3), subStringDouble(getProbability2(), 0, 3), subStringDouble(getProbability3(), 0, 3));
	}
	
	String wk1() {
		return getWakulevellist().substring(0, 2); 
	}

	String wk2() {
		return getWakulevellist().substring(3, 5); 
	}

	String wk3() {
		return getWakulevellist().substring(6, 8); 
	}

	String wk4() {
		return getWakulevellist().substring(9, 11); 
	}

	String wk5() {
		return getWakulevellist().substring(12, 14); 
	}
	
	// B2-A1-B1-B1-A2-A2
	String wk6() {
		return getWakulevellist().substring(15, 17); 
	}

	String wk12() {
		return getWakulevellist().substring(0, 5); 
	}
	
	String wk123() {
		return getWakulevellist().substring(0, 8); 
	}

	String wk1234() {
		return getWakulevellist().substring(0, 11); 
	}

	String nw1() {
		return subStringDouble(getNationwiningrate()[0], 0, 1);
	}

	String nw12dig() {
		return subStringDouble(getNationwiningrate()[0], 0, 3);
	}
	
	String nw12() {
		return String.join("-", subStringDouble(getNationwiningrate()[0], 0, 1), subStringDouble(getNationwiningrate()[1], 0, 1));
	}

	String nw123() {
		return String.join("-", subStringDouble(getNationwiningrate()[0], 0, 1), subStringDouble(getNationwiningrate()[1], 0, 1), subStringDouble(getNationwiningrate()[2], 0, 1));
	}

	String pd12() {
		return String.join("", getPrediction1(), getPrediction2());
	}

	String pd123() {
		return String.join("", getPrediction1(), getPrediction2(), getPrediction2());
	}

	abstract String getJyo();

	abstract String getRaceNo();

	abstract String getTurn();

	abstract String getAlcnt();

	abstract String getGrade();

	abstract String getRtype();

	abstract String getWakulevellist();

	abstract String getComConfidence();

	abstract String getComPredict();

	abstract String getPrediction1();

	abstract String getPrediction2();

	abstract String getPrediction3();

	abstract double[] getNationwiningrate();

	abstract double[] getProbabilities1();

	abstract double[] getProbabilities2();

	abstract double getProbability1();

	abstract double getProbability2();

	abstract double getProbability3();

	abstract double getOdds();
	
	abstract String getFixedEntrance();

	abstract String getTimezone();
	
	abstract String getCond1();
	
	abstract String getN1point1();
	
	abstract String getN1pointWaku1();

	abstract String getCond2();
	
	abstract String getN1point2();
	
	abstract String getN1pointWaku2();
}
