package com.pengkong.boatrace.exp10.simulation.pattern;

import com.pengkong.common.MathUtil;

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
		double[] arrdouble;

		switch (id) {
		case "jyocd":
			return getJyocd();
		case "turn":
			return getTurn();
		case "raceno":
			return getRaceNo();
		case "grade":
			return getGrade();
		case "racetype":
			return getRacetype();
		case "alevelcount":
			return getAlevelcount();
		case "wakulevel1":
			return getWakulevellist().substring(0, 2);
		case "wakulevel12":
			return getWakulevellist().substring(0, 5);
		case "wakulevel13":
			return getWakulevellist().substring(0, 8);
			
		case "jyocd+grade":
			return getJyocd() + "-" + getGrade();
		case "jyocd+racetype":
			return getJyocd() + "-" + getRacetype();
		case "jyocd+race":
			return getJyocd() + "-" + getRaceNo();
		case "jyocd+alvlcnt":
			return getJyocd() + "-" + getAlevelcount();
			
		case "turn+race":
			return getTurn() + "-" + getRaceNo();
		case "turn+alvlcnt":
			return getTurn() + "-" + getAlevelcount();
		case "turn+racetype":
			return getTurn() + "-" + getRacetype();
		case "turn+grade":
			return getTurn() + "-" + getGrade();
		case "turn+nw1":
			return getTurn() + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);

		case "race+alvlcnt":
			return getRaceNo() + "-" + getAlevelcount();
		case "race+racetype":
			return getRaceNo() + "-" + getRacetype();
		case "race+nw1":
			return getRaceNo() + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "race+grade":
			return getRaceNo() + "-" + getGrade();

		case "alvlcnt+nw1":
			return getAlevelcount() + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "alvlcnt+racetype":
			return getAlevelcount() + "-" + getRacetype();
		case "alvlcnt+grade":
			return getAlevelcount() + "-" + getGrade();
			
		// # 1착예측 확률 중심
		case "probr1-1dig":
			return subStringDouble(getProbability1(), 0, 3);
		case "probr1-2dig":
			return subStringDouble(getProbability1(), 0, 4);
		case "probr12-1dig":
			return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(getProbability2(), 0, 3);
			
		// # 1착예측의 확률집합 중심
		case "probr1-r2-1dig":
			arrdouble = MathUtil.getSortedArray(getProbabilities1(), false);
			return subStringDouble(arrdouble[0] - arrdouble[1], 0, 3);
		case "probr1-r2-2dig":
			arrdouble = MathUtil.getSortedArray(getProbabilities1(), false);
			return subStringDouble(arrdouble[0] - arrdouble[1], 0, 4);

		// # 1착예측확률과 레이스정보 조합
		case "prob1+waku1":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getWakulevellist().substring(0, 2);
		case "prob1+waku12":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getWakulevellist().substring(0, 5);
		case "prob1+turn":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getTurn();
		case "prob1+raceno":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getRaceNo();
		case "prob1+jyocd":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getJyocd();
		case "prob1+alevelcount":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getAlevelcount();
		case "prob1+racetype":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getRacetype();
		case "prob1+nw1":
			return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "prob1+grade":
			return subStringDouble(getProbability1(), 0, 3) + "-" + getGrade();

			// # 와꾸레벨1과 레이스정보 조합
		case "jyocd+waku1":
			return getJyocd() + "-" + getWakulevellist().substring(0, 2);
		case "race+waku1":
			return getRaceNo() + "-" + getWakulevellist().substring(0, 2);
		case "turn+waku1":
			return getTurn() + "-" + getWakulevellist().substring(0, 2);
		case "alevelcnt+waku1":
			return getAlevelcount() + "-" + getWakulevellist().substring(0, 2);
		case "racetype+waku1":
			return getRacetype() + "-" + getWakulevellist().substring(0, 2);
		case "nw1+wk1":
			return subStringDouble(getNationwiningrate()[0], 0, 1) + "-" + getWakulevellist().substring(0, 2);
		case "nw1+wk12":
			return subStringDouble(getNationwiningrate()[0], 0, 1) + "-" + getWakulevellist().substring(0, 5);
		case "grade+wk12":
			return getGrade() + "-" + getWakulevellist().substring(0, 5);
			
			// # 1,2착예측과 레이스정보 조합
		case "pd12":
			return getPrediction1() + getPrediction2();
		case "pd12+wk1":
			return getPrediction1() + getPrediction2() + "-" + getWakulevellist().substring(0, 2);
		case "pd12+grade":
			return getPrediction1() + getPrediction2() + "-" + getGrade();

		// # 1,2,3착예측과 레이스정보조합
		case "pd123":
			return getPrediction1() + getPrediction2() + getPrediction3();
			
		// 予想確率組み合わせ	
		case "pmul-12":
			return subStringDouble(getProbability1() * getProbability2(), 0, 4);
		case "pmul-12+wk1":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getWakulevellist().substring(0, 2);
		case "pmul-12+prob1":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + subStringDouble(getProbability1(), 0, 3);
		case "pmul-12+jyocd":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getJyocd();
		case "pmul-12+turn":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getTurn();
		case "pmul-12+raceno":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getRaceNo();
		case "pmul-12+racetype":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getRacetype();
		case "pmul-12+alvlcnt":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getAlevelcount();
		case "pmul-12+nw1":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "pmul-12+grade":
			return subStringDouble(getProbability1() * getProbability2(), 0, 3) + "-" + getGrade();

		case "pmul-123":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 4);
		case "pmul-123+wk1":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getWakulevellist().substring(0, 2);
		case "pmul-123+prob1":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + subStringDouble(getProbability1(), 0, 3);
		case "pmul-123+jyocd":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getJyocd();
		case "pmul-123+turn":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getTurn();
		case "pmul-123+raceno":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getRaceNo();
		case "pmul-123+racetype":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getRacetype();
		case "pmul-123+alvlcnt":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getAlevelcount();
		case "pmul-123+nw1":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "pmul-123+grade":
			return subStringDouble(getProbability1() * getProbability2() * getProbability3(), 0, 3) + "-" + getGrade();
			
		case "psum-12":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3);
		case "psum-12+wk1":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getWakulevellist().substring(0, 2);
		case "psum-12+prob1":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + subStringDouble(getProbability1(), 0, 3);
		case "psum-12+jyocd":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getJyocd();
		case "psum-12+turn":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getTurn();
		case "psum-12+raceno":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getRaceNo();
		case "psum-12+racetype":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getRacetype();
		case "psum-12+alvlcnt":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getAlevelcount();
		case "psum-12+nw1":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "psum-12+grade":
			return subStringDouble(getProbability1() + getProbability2(), 0, 3) + "-" + getGrade();
			
		case "psum-123":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3);
		case "psum-123+wk1":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getWakulevellist().substring(0, 2);
		case "psum-123+prob1":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + subStringDouble(getProbability1(), 0, 3);
		case "psum-123+jyocd":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getJyocd();
		case "psum-123+turn":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getTurn();
		case "psum-123+raceno":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getRaceNo();
		case "psum-123+racetype":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getRacetype();
		case "psum-123+alvlcnt":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getAlevelcount();
		case "psum-123+nw1":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
		case "psum-123+grade":
			return subStringDouble(getProbability1() + getProbability2() + getProbability3(), 0, 3) + "-" + getGrade();

		case "prob12":
			return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(getProbability2(), 0, 3); 
		case "prob123":
			return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(getProbability2(), 0, 3) + "-" + subStringDouble(getProbability3(), 0, 3); 
		case "probdist12":
			return subStringDouble(getProbability1() - getProbability2(), 0, 3);
		case "probdist123":
			return subStringDouble(getProbability1() - getProbability2(), 0, 3)  + "-" + subStringDouble(getProbability2() - getProbability3(), 0, 3);
		case "probdist1213":
			return subStringDouble(getProbability1() - getProbability2(), 0, 3)  + "-" + subStringDouble(getProbability1() - getProbability3(), 0, 3);
		case "probdist12+wk1":
			return subStringDouble(getProbability1() - getProbability2(), 0, 3) + "-" + getWakulevellist().substring(0, 2);
		case "probdist12+nw1":
			return subStringDouble(getProbability1() - getProbability2(), 0, 3) + "-" + subStringDouble(getNationwiningrate()[0], 0, 1);
			
		// その他
		case "prob1mix":
			arrdouble = MathUtil.getSortedArray(getProbabilities1(), false);
			return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(arrdouble[0] - arrdouble[1], 0, 3);
		case "probr123-1dig":
			return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(getProbability2(), 0, 3) + "-" + subStringDouble(getProbability3(), 0, 3);
		case "nopattern":
			return "nopattern";
		default:
			throw new IllegalStateException("unidentified pattern id. " + id);
		}
	}

//case "turn+race+waku1":
//	return getTurn() + "-" + getRaceNo() + "-" + getWakulevellist().substring(0, 2);
//case "turn+lcnt+waku1":
//	return getTurn() + "-" + getAlevelcount() + "-" + getWakulevellist().substring(0, 2);
	
//case "prob12+waku12":
//	return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(getProbability2(), 0, 3) + "-"
//			+ getWakulevellist().substring(0, 5);

//case "prob1mix":
//	arrdouble = MathUtil.getSortedArray(getProbabilities1(), false);
//	return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(arrdouble[0] - arrdouble[1], 0, 3);
//case "prob1mix_2":
//	arrdouble = MathUtil.getSortedArray(getProbabilities1(), false);
//	return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(arrdouble[0] - arrdouble[1], 0, 4);
//case "prob1mix_3":
//	arrdouble = MathUtil.getSortedArray(getProbabilities1(), false);
//	return subStringDouble(getProbability1(), 0, 4) + "-" + subStringDouble(arrdouble[0] - arrdouble[1], 0, 3);

//	// # 1,2,3착 예측확률의 조합 중심
//	case "psum-r12":
//		return subStringDouble(getProbability1() + getProbability2(), 0, 3);
//	case "pmul-r12":
//		value = getProbability1() * getProbability2();
//		return subStringDouble(value, 0, 3);
//	case "psum-r123":
//		value = getProbability1() + getProbability2() + getProbability3();
//		return subStringDouble(value, 0, 3);
//	case "pmul-r123":
//		value = getProbability1() * getProbability2() * getProbability3();
//		return subStringDouble(value, 0, 3);
//	case "psum-r23":
//		value = getProbability2() + getProbability3();
//		return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(value, 0, 3);
//	case "pmul-r23":
//		value = getProbability2() * getProbability3();
//		return subStringDouble(getProbability1(), 0, 3) + "-" + subStringDouble(value, 0, 4);
//	case "pmulsum":
//		value = getProbability1() + getProbability2() + getProbability3();
//		value2 = getProbability1() * getProbability2() * getProbability3();
//		return subStringDouble(value, 0, 3) + "-" + subStringDouble(value2, 0, 4);

//case "pd12+compred1":
//	return getPrediction1() + getPrediction2() + "-" + getComPredict().substring(0, 1);
//case "pd12+compred12":
//	return getPrediction1() + getPrediction2() + "-" + getComPredict().substring(0, 2);
//case "turn+level1":
//	return getTurn() + "-" + getWakulevellist().substring(0, 2);
//case "swa12":
//	arrdouble = getNationwiningrate();
//	return getWakulevellist().substring(0, 2) + subStringDouble(arrdouble[0], 0, 1) + "-"
//			+ getWakulevellist().substring(3, 5) + subStringDouble(arrdouble[1], 0, 1);
//	// # 옺즈 중심
//	case "resultodds":
//		return String.valueOf(getOdds());
//	case "prob1_oddr3T123":
//		return subStringDouble(getProbability1(), 0, 3) + "-" + getOdds();
//	case "oddr3T123":
//		return String.valueOf(getOdds());
//	case "exptrate1":
//		return subStringDouble(getProbability1() * getOdds(), 0, 3);
//	case "exptrate_mul12":
//		return subStringDouble(getProbability1() * getProbability2() * getOdds(), 0, 3);
//	case "exptrate_mul123":
//		return subStringDouble(getProbability1() * getProbability2() * getProbability3() * getOdds(), 0, 3);
//	case "exptrate_sum12":
//		return subStringDouble((getProbability1() + getProbability2()) * getOdds(), 0, 3);
//	case "exptrate_sum123":
//		return subStringDouble((getProbability1() + getProbability2() + getProbability3()) * getOdds(), 0, 3);
//	// # 컴예측 중심
//	case "compred1":
//		return getComPredict().substring(0, 1);
//	case "compred12":
//		return getComPredict().substring(0, 2);
//	case "compred123":
//		return getComPredict().substring(0, 3);
//	case "compred1234":
//		return getComPredict().substring(0, 4);
//	case "compred1+conf":
//		return getComPredict().substring(0, 1) + "-" + getComConfidence();
//	case "ml+comconf":
//		return subStringDouble(getProbability1(), 0, 3) + "-" + getComPredict().substring(0, 1) + "-"
//				+ getComConfidence();
//	case "ml+comconf_2":
//		return subStringDouble(getProbability1(), 0, 4) + "-" + getComPredict().substring(0, 1) + "-"
//				+ getComConfidence();
//	case "comconf12":
//		return getComPredict().substring(0, 2) + "-" + getComConfidence();
//	// 2022/3/10追加
//	case "compred1+wk1":
//		return getComPredict().substring(0, 1) + "-" + getWakulevellist().substring(0, 2);
//	case "compred1+wk12":
//		return getComPredict().substring(0, 1) + "-" + getWakulevellist().substring(0, 5);
//	case "compred12+wk1":
//		return getComPredict().substring(0, 2) + "-" + getWakulevellist().substring(0, 2);
//	case "comconf1+wk1":
//		return getComPredict().substring(0, 1) + "-" + getComConfidence() + "-"
//				+ getWakulevellist().substring(0, 2);
//	case "compred1+jyocd":
//		return getComPredict().substring(0, 1) + "-" + getJyocd();
//	case "compred1+raceno":
//		return getComPredict().substring(0, 1) + "-" + getRaceNo();
//	case "compred1+turn":
//		return getComPredict().substring(0, 1) + "-" + getTurn();
//	case "compred1+alevelcount":
//		return getComPredict().substring(0, 1) + "-" + getAlevelcount();
//	case "compred1+prob1":
//		return getComPredict().substring(0, 1) + "-" + subStringDouble(getProbability1(), 0, 3);
//	case "comp1+prob1+wk1":
//		return getComPredict().substring(0, 1) + "-" + subStringDouble(getProbability1(), 0, 3) + "-"
//				+ getWakulevellist().substring(0, 2);

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

	abstract String getJyocd();

	abstract String getRaceNo();

	abstract String getTurn();

	abstract String getAlevelcount();

	abstract String getGrade();

	abstract String getRacetype();

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
}
