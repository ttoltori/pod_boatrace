package com.pengkong.boatrace.exp10.result.histogram;

import java.util.Arrays;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.MathUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

public class HistogramConverter {
    MLPropertyUtil prop = MLPropertyUtil.getInstance();

    /** キー(BetType, PRなど)毎のヒストグラム区分値リスト */
    HashMapList<Double>mapHistogramValues; 
    // 0=初期 1=使用 2=使用しない
    int useFlag = 0;
    void initialize() throws Exception {
        mapHistogramValues = new HashMapList<>();
        String filepath = prop.getString("file_histogram_values");
        if (filepath.equals("x")) {
        	useFlag = 2;
        	return;
        }
        
        List<String> lines = FileUtil.readFileByLineArr(prop.getString("file_histogram_values"));
        for (String line : lines) {
            String[] keyValue = line.split(Delimeter.EQUAL.getValue());
            Double[] values = StringUtil.getDoubleArray(keyValue[1].split(Delimeter.COMMA.getValue()));
            mapHistogramValues.addItemAll(keyValue[0], Arrays.asList(values));
        }
        useFlag = 1;
    }
    
    void ensureInitialize() throws Exception {
        if (useFlag == 0) {
            initialize();
        }
    }
    
    public Double convertByKey(String key, Double value) throws Exception {
        ensureInitialize();
        if (value == null || useFlag == 2) {
            return value;
        }
        
        return MathUtil.getHistogramValue(value, mapHistogramValues.get(key));
    }

    public Double convertByBettypeKumiban(String betType, String kumiban, Double value) throws Exception {
        ensureInitialize();
        if (value == null || useFlag == 2) {
            return value;
        }

        // bettype_kumibanで探す
        List<Double> values = mapHistogramValues.get(String.join(Delimeter.UNDERBAR.getValue(), betType, kumiban));
        if (values == null) {
        	// bettypeで探す
        	values = mapHistogramValues.get(betType);
        	if (values == null) {
        		throw new IllegalStateException("histogram values doesnot exist. key->" + String.join(Delimeter.UNDERBAR.getValue(), betType, kumiban));
        	}
        }
        
        return MathUtil.getHistogramValue(value, values);
    }
}
