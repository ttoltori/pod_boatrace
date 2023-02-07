package com.pengkong.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;

import lombok.Data;

public class StringUtil {
	public static boolean isEmpty(String str) {
		return ((str == null) || (str.trim().length() <= 0));
	}

	public static String unpadZero(String str) {
		return String.valueOf(Integer.valueOf(str));
	}

	public static String leftPad(String src, int count, String padding) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count - src.length(); i++) {
			sb.append(padding);
		}
		sb.append(src);

		return sb.toString();
	}

	public static String randomDigits(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(ThreadLocalRandom.current().nextInt(10));
		}
		return sb.toString();
	}

	public static String getStringBetween(String szStr, String szStart, String szEnd) {
		String sRet = null;
		int iStartIndex = szStr.indexOf(szStart) + szStart.length();
		int iEndIndex = szStr.indexOf(szEnd, iStartIndex);

		if (iStartIndex < 0 || iEndIndex < 0 || iStartIndex > iEndIndex || (iStartIndex == szStart.length() - 1)) {
			return new String();
		}
		sRet = szStr.substring(iStartIndex, iEndIndex + szEnd.length()).trim();
		if (sRet.indexOf(szStart) > -1)
			return getStringBetween(sRet, szStart, szEnd);
		else
			return szStr.substring(iStartIndex, iEndIndex).trim();
	}

	public static String toString(HashMap<String, Object> procParam) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Entry<String, Object> entry : procParam.entrySet()) {
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(entry.getValue());
			sb.append(", ");
		}
		sb.append("}");

		return sb.toString();
	}

	/**
	 * option文字列等を分割してlistで取得する。 ex) "1,2-5,7" -> [1,2,3,4,5,6,7]
	 */
	public static List<String> parseNumericOptionsString(String strNumbers) {
		List<String> listNumber = new ArrayList<>();
		String[] numbers = strNumbers.split(Delimeter.COMMA.getValue());
		
		for (String number : numbers) {
			if (StringUtil.isEmpty(number)) {
				continue;
			}

			if (number.contains(Delimeter.DASH.getValue())) {
				String[] range = number.split(Delimeter.DASH.getValue());
				int min = Integer.valueOf(range[0]);
				int max = Integer.valueOf(range[1]);

				for (int i = min; i <= max; i++) {
					listNumber.add(String.valueOf(i));
				}
			} else {
				listNumber.add(number);
			}
		}

		return listNumber;
	}

	/**
	 * 文字列のarrayをcopy and sortして取得する。
	 * 
	 * @param items 文字列のarray
	 * @return
	 */
	public static String[] copyAndSort(String... items) {
		String[] sorted = Arrays.copyOf(items, items.length);
		Arrays.sort(sorted);

		return sorted;
	}

	public static String quote(String src) {
		return "'" + src + "'";
	}
	
	public static String quote(String...strs) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < strs.length; i++) {
			sb.append("'" + strs[i] + "'");
			sb.append(",");
		}
		
		return sb.substring(0, sb.length()-1);
	}
	
	public static String addDelimeter(String str, String delimeter) {
		return String.join(delimeter, str.split("")); 
	}
	
	
	public static String sortString(String str) {
		char tempString[] = str.toCharArray();
		// perform sort using Arrays.sort() method
		Arrays.sort(tempString);
		// storing sorted character array back to string
		return new String(tempString);
	}

	public static boolean contains(String findStr, String[] src) {
		return Arrays.stream(src).anyMatch(findStr::contains);
	}

	public static Double[] getDoubleArray(String[] arr) {
        Double[] ret = new Double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = Double.valueOf(arr[i].trim());
        }

        return ret;
    }
    public static List<Double> getDoubleList(String[] arr) {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(Double.valueOf(arr[i].trim()));
        }

        return list;
    }
	
	static public void print(double b) {
		System.out.println( b);
	}
	public static void main(String[] args) {
		
		MlEvaluation dto = new MlEvaluation();
		System.out.println(StringUtil.quote("1") );
		System.out.println(StringUtil.quote("1", "2", "3") );
//		String[] arr = { "a", "b" };
//		List<String> list = Arrays.asList("a", "b");
//		System.out.println(list.subList(0, 0));
//		System.out.println(list.subList(0, 1));

//		System.out.println(arr);
//		System.out.println(list);
//		System.out.println(StringUtil.getStringBetween("asdf{{dsf}}asdfad", "{{", "}}"));
//		System.out.println(String.valueOf(Integer.valueOf("00103")));
	}

	@Data
	public class Tmp {
		private Double a;
	}
}
