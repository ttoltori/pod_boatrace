package com.pengkong.boatrace.exp10.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.enums.OutlierMethod;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.ex.MlResultEx;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * Required properties: outlier_method, outlier_field
 * @author ttolt
 *
 */
public class MLUtil {
	/**
	 * 外れ値を除外処理を行う https://qiita.com/papi_tokei/items/6f77a2a250752cfa850b  １次元データの外れ値の検出
	 * @param list
	 * @return
	 */
	public static List<DBRecord> processOutliers(List<DBRecord> list) {
		MLPropertyUtil prop = MLPropertyUtil.getInstance();
		String outlierMethod = prop.getString("outlier_method");
		// 外れ値除去なしの場合
		if (outlierMethod.equals(OutlierMethod.NO.getValue())) {
			return list;
		}
		
		String outlierFieldName = prop.getString("outlier_field");
		
		// 外れ値測定対象の値をソートする
		List<Double> input = new ArrayList<>();
		for( DBRecord rec : list) {
			input.add(rec.getDouble(outlierFieldName));
		}
		Collections.sort(input);
		
		// 外れ値の境界を求める min, max
		double[] minMax = null;
		if (outlierMethod.equals(OutlierMethod.QUARTILE)) {
			minMax = getOutlierRangeByQuartile(input);	
		} else if (outlierMethod.equals(OutlierMethod.OCTILE)) {
			minMax = getOutlierRangeByOctile(input);
		} else if (outlierMethod.equals(OutlierMethod.PERCENTILE)) {
			minMax = getOurlierRangeByPercentile(input, 0, 0.95);
		}

		// 外れ値の境界外のレコードを削除する
		for (DBRecord rec : list) {
			double outlierValue = rec.getDouble(outlierFieldName);
			if (outlierValue < minMax[0] || outlierValue > minMax[1]) {
				list.remove(rec);
			}
		}
		
		return list;
	}
	
	/**
	 * 四分位数を利用して外れ値の境界(min, max)を求める
	 * @param values 対象
	 * @return [0]=min, [1]=max
	 */
	public static double[] getOutlierRangeByQuartile(List<Double> values) {
		double[] result = new double[2];
		
		double q1 = values.get(values.size() / 4);
		double q3 = values.get(values.size() * 3 / 4);
		double iqr = q3 - q1;
		
		result[0] = q1 - iqr * 1.5;
		result[1] = q3 + iqr * 1.5;
		
		return result;
	}

	/**
	 * 八分位数を利用して外れ値の境界(min, max)を求める
	 * @param values 対象
	 * @return [0]=min, [1]=max
	 */
	public static double[] getOutlierRangeByOctile(List<Double> values) {
		double[] result = new double[2];
		
		double q1 = values.get(values.size() / 8);
		double q7 = values.get(values.size() * 7 / 8);
		double iqr = q7 - q1;
		
		result[0] = q1 - iqr * 1.5;
		result[1] = q7 + iqr * 1.5;
		
		return result;
	}
	
	/**
	 * 指定の四分位数を利用して外れ値の境界(min, max)を求める
	 * @param values 対象
	 * @return [0]=min, [1]=max
	 */
	public static double[] getOurlierRangeByPercentile(List<Double> values, double minPercent, double maxPercent) {
		double[] result = new double[2];
		
		if (minPercent == 0) {
			result[0] = -99999;
		} else {
			result[0] = values.get( (int)(values.size() * minPercent));
		}
		result[1] = values.get( (int)(values.size() * maxPercent));
		
		return result;
	}
	
	/**
	 * classIdが他モデルを参照しているか。
	 * @param classId
	 * @return true=対モデル参照 ex) 1=(r1-123456)
	 */
	public static boolean isReferentialClassId(String classId) {
		return classId.contains(Delimeter.EQUAL.getValue());
	}
	
	/**
	 * classIdが参照している参照先のモデル番号を取得する
	 * @param classId ex) 1=(r1-123456)
	 * @return モデルNO e) 1=(r1-123456) -> 1を抽出
	 */
	public static String getReferedModelNo(String classId) {
		return classId.split("=")[0];
	}
	
	public static void main(String[] args) {
		MLDescriptiveStatistics stat = new MLDescriptiveStatistics();
		double[] input = {28,30,40,50,60,70,80,900,900,2,2,2,2,2,2,2,2,2,2,2,2,5,5,5,5,6,6,6,7,7,7,8,9.10,11,12,13,14,15,16,17,18,1000,3,3,3,3,3,4,4,4,4,4,1,1,1,1,1,1,1,1,1,1,1,1,19,20,21,22,24,26};
		
		stat.addValues(input);
		System.out.println("before," + stat.getTitles(Delimeter.COMMA));
		System.out.println("before," + stat.getValues(Delimeter.COMMA));

		double[] outlierMinMax = MLUtil.getOutlierRangeByQuartile(Arrays.asList(ArrayUtils.toObject(input)));
		System.out.println("byQuartile min=" + outlierMinMax[0]);
		System.out.println("byQuartile max=" + outlierMinMax[1]);
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < input.length; i++) {
			if (input[i] < outlierMinMax[0] || input[i] > outlierMinMax[1]) {
				continue;
			}
			list.add(input[i]);
		}
		stat.clear();
		stat.addValues(list);
		System.out.println("quartile," + stat.getTitles(Delimeter.COMMA));
		System.out.println("quartile," + stat.getValues(Delimeter.COMMA));
		

		outlierMinMax = MLUtil.getOutlierRangeByOctile(Arrays.asList(ArrayUtils.toObject(input)));
		System.out.println("byOctile min=" + outlierMinMax[0]);
		System.out.println("byOctile max=" + outlierMinMax[1]);
		list = new ArrayList<>();
		for (int i = 0; i < input.length; i++) {
			if (input[i] < outlierMinMax[0] || input[i] > outlierMinMax[1]) {
				continue;
			}
			list.add(input[i]);
		}
		stat.clear();
		stat.addValues(list);
		System.out.println("octile," + stat.getTitles(Delimeter.COMMA));
		System.out.println("octile," + stat.getValues(Delimeter.COMMA));
		
		outlierMinMax = MLUtil.getOurlierRangeByPercentile(Arrays.asList(ArrayUtils.toObject(input)),0,0.95);
		System.out.println("byPercentile min=" + outlierMinMax[0]);
		System.out.println("byPercentile max=" + outlierMinMax[1]);
		List<Double> list2 = new ArrayList<>();
		for (int i = 0; i < input.length; i++) {
			if (input[i] < outlierMinMax[0] || input[i] > outlierMinMax[1]) {
				continue;
			}
			list2.add(input[i]);
		}
		
		stat.clear();
		stat.addValues(list2);
		System.out.println("percentile," + stat.getTitles(Delimeter.COMMA));
		System.out.println("percentile," + stat.getValues(Delimeter.COMMA));
	}
	
}
