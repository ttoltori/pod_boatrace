package com.pengkong.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.pengkong.boatrace.exp10.util.MLDescriptiveStatistics;

public class MathUtil {

	static final SimpleRegression regr = new SimpleRegression();
	
	public MathUtil() {
	}

	/**
	 * double arrayをソートして返却する。
	 * @param arr ソート対象
	 * @param isAscending true=ascending false=descending
	 * @return sorted array
	 */
	public static double[] getSortedArray(double[] arr, boolean isAscending) {
		double[] copied = arr.clone();
		Arrays.sort(copied);
		if (isAscending) {
			return copied;
		}

		// descending
		double[] descended = new double[copied.length];
		int j = 0;
		for (int i = copied.length-1; i >= 0; i--) {
			descended[j++] = copied[i];
		}
		
		return descended;
	}
	
	
	public static List<Double> getSortedList(List<Double> list) {
		return getSortedList(list, true);
	}

	/**
	 * Double Listをソートして返却する。
	 * @param list ソート対象
	 * @param isAscending true=ascending false=descending
	 * @return sorted list
	 */
	public static List<Double> getSortedList(List<Double> list, boolean isAscending) {
		List<Double> copied = new ArrayList<>(list);
		Collections.sort(copied);
		if (isAscending) {
			return copied;
		}

		// descending
		Collections.reverse(copied);
		return copied;
	}

	/**
	 * regressionを返却する
	 * @param listValue
	 * @return [0]=slope, [1]=intercept
	 */
	public static double[] getRegression(List<Double> listValue) {
		regr.clear();
		// [0]=slope, [1]=intercept
		double[] result = new double[2];
		for (int i=0; i < listValue.size(); i++) {
			regr.addData(i, listValue.get(i));	
		}
		regr.regress();
		result[0] = regr.getSlope();
		result[1] = regr.getIntercept();
		
		return result;
	}
	
	/**
	 * regressionからslopeだけ取得する。 
	 * @param values  
	 * @return slope (regression失敗した場合は-0.999)
	 */
	public static double getRegressionSlope(List<Double> values) {
		try {
			return MathUtil.getRegression(values)[0];
		} catch (NoDataException e) {
			return -0.999; // エラー設定値
		}
	}
	
	public static Double scale0(Double value) {
		return MathUtil.scale(value, 0);
	}

	public static Double scale1(Double value) {
		return MathUtil.scale(value, 1);
	}

	public static Double scale2(Double value) {
		return MathUtil.scale(value, 2);
	}

	public static Double scale3(Double value) {
		return MathUtil.scale(value, 3);
	}
	
	public static Double scale(Double value, int scale) {
		if (value == null || value.isNaN())
			return value;
		return new BigDecimal(value).setScale(scale, RoundingMode.FLOOR).doubleValue();
	}
	
	public static float[] convertFloatList(List<Float> input) {
		float[] result = new float[input.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = input.get(i);
		}
		
		return result;
	}
	
	public static float sumOfFloatArray(float[] input) {
		float sum = 0;
	    for (int i = 0; i < input.length; i++) {
	        sum += input[i];
	    }
	    
	    return sum;
	}
	
	/**
	 * 指定したunit単位に金額を四捨五入する ex) 430 -> 500
	 * @param amount 金額
	 * @param unit 単位 ex) 100
	 * @return
	 */
	public static int ceilAmount(int amount, int unit) {
		return (int) ( Math.ceil((double) amount / unit) * unit );
	}

	/** listを分割してarray of listで返却する. 
	 * @param <T>*/
	@SuppressWarnings("unchecked")
	public static <T> List<T>[] splitList(List<T> src, int splitCount)  {
		ArrayList<T>[] arrList = (ArrayList<T>[])new ArrayList[splitCount];
		int newSize = src.size() / splitCount;
		// 分割数が原本のサイズより大きい場合、原本サイズ分のarrayを返却する
		// array内のリストのアイテムは各１個だけ
		if (newSize <= 0) {
			arrList = (ArrayList<T>[])new ArrayList[src.size()];
			for (int i = 0; i < arrList.length; i++) {
				arrList[i] = new ArrayList<>();
				arrList[i].add(src.get(i));
			}
			return arrList;
		}
		
		int newTotal = newSize * splitCount;
		int cnt = 0;
		int idx = 0;
		
		arrList[idx] = new ArrayList<>();
		for (int i = 0; i < newTotal; i++) {
			if (cnt++ >= newSize) {
				cnt = 1;
				idx++;
				arrList[idx] = new ArrayList<>();
			}
			arrList[idx].add(src.get(i));
		}
		
		return arrList;
	}
	
	/** 調和平均を求める */
	public static Double getHarmonyMean(Double...values) {
		Double sum = 0.0;
		for (Double value : values) {
			sum += (1.0 / value);
		}
		
		return (1.0 / sum);
	}
	
	public static Double min(Double...values) {
		Double min = values[0];
		for (Double value : values) {
			if (value < min) {
				min = value;
			}
		}
		return min;
	}
	
	public static Double max(Double...values) {
		Double max = values[0];
		for (Double value : values) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}
	
	/**
	 * 記述統計データを配列で取得する
	 * @param listValues 記述統計対象データ
	 * @return [min, max, mean, standard deviation, skewness, kurtosis, median] 
	 */
	public static double[] getDescriptiveStatistics(List<Double> listValues) {
		double[] result = new double[7];
		MLDescriptiveStatistics mld = MLDescriptiveStatistics.getInstance();
		mld.clear();
		mld.addValues(listValues);
		
		result[0] = MathUtil.scale2(mld.getMin()); // min
		result[1] = MathUtil.scale2(mld.getMax()); // max
		result[2] = MathUtil.scale2(mld.getMean()); // 平均
		result[3] = MathUtil.scale2(mld.getStandardDeviation()); // 標準偏差
		result[4] = MathUtil.scale2(mld.getPercentile(50)); // 中央値
		//result[5] = MathUtil.scale2(mld.getSkewness()); // skewness
		//result[6] = MathUtil.scale2(mld.getKurtosis()); // kurtosis
		
		return result;
	}
	public static double[] getDescriptiveStatistics(double[] values) {
		return getDescriptiveStatistics(convertList(values));
	}
	
	public static List<Double> convertList(double[] values) {
		return Arrays.stream(values).boxed().collect(Collectors.toList());
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
	public static double[] getOutlierRangeByPercentile(List<Double> values, double minPercent, double maxPercent) {
		double[] result = new double[2];
		
		if (minPercent == 0) {
			result[0] = -99999;
		} else {
			result[0] = values.get( (int)(values.size() * minPercent));
		}
		result[1] = values.get( (int)(values.size() * maxPercent));
		
		return result;
	}

	public static Double getHistogramValue(Double value, List<Double> splitValues) {
        if (value < splitValues.get(0)) {
            return 0.0; // 最小値未満の場合0
        } else if (value >= splitValues.get(splitValues.size()-1)) {
            return splitValues.get(splitValues.size()-1); // 最大値以上の場合最大値 
        }

        for (int i = 0; i < splitValues.size() - 1; i++) {
            if (value >= splitValues.get(i) && value < splitValues.get(i+1)) {
                return splitValues.get(i);
            }
        }
        
        throw new IllegalStateException();
	}
	
    public static double convertToPercentile(double value, double min, double max, double percentile) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value is not within the specified range");
        }
        double range = max - min;
        return     MathUtil.scale( ((value - min) / range) * percentile , 0)  ;
    }
	
    public static double calculatePercentile(List<Double> numbers, double N) {
        // Sort the list in ascending order
        Collections.sort(numbers);

        // Calculate the index of the Nth percentile
        int indexOfPercentile = (int) Math.ceil(N * numbers.size() / 100.0) - 1;

        // If the index is out of bounds, return the maximum or minimum value
        if (indexOfPercentile < 0) {
            return numbers.get(0);
        } else if (indexOfPercentile >= numbers.size()) {
            return numbers.get(numbers.size() - 1);
        }

        // Return the Nth percentile value
        return numbers.get(indexOfPercentile);
    }
    
	public static void main(String[] args) {
		try {
			List<Double> list = Arrays.asList(1.0, 25.0, 50.0, 75.0, 1000.0);
			System.out.println( MathUtil.calculatePercentile(list, 5));
			
			
//			System.out.println( MathUtil.min(0.0, 25.0, 50.0, 75.0, 100.0 ));
//			System.out.println( MathUtil.max(0.0, 25.0, 50.0, 75.0, 100.0 ));
//			System.out.println( MathUtil.convertToPercentile(25, 1, 100, 95 ));
//			System.out.println((double)21 / 100.0);
//			System.out.println((double)33 / 100.0);
			
//			List<Double> yList1 = new ArrayList<>();
//			double[] yData1 = {0};
//			double[] desc1;
//			desc1 = MathUtil.getDescriptiveStatistics(yData1);
//			
//			System.out.println(MathUtil.ceilAmount(46, 100));
//			System.out.println(MathUtil.ceilAmount(2236, 100));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
