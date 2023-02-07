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
    
    void initialize() throws Exception {
        mapHistogramValues = new HashMapList<>();
        List<String> lines = FileUtil.readFileByLineArr(prop.getString("file_histogram_values"));
        for (String line : lines) {
            String[] keyValue = line.split(Delimeter.EQUAL.getValue());
            Double[] values = StringUtil.getDoubleArray(keyValue[1].split(Delimeter.COMMA.getValue()));
            mapHistogramValues.addItemAll(keyValue[0], Arrays.asList(values));
        }
    }
    
    void ensureInitialize() throws Exception {
        if (mapHistogramValues == null) {
            initialize();
        }
    }
    
    public Double convert(String key, Double value) throws Exception {
        ensureInitialize();
        if (value == null) {
            return value;
        }

        return MathUtil.getHistogramValue(value, mapHistogramValues.get(key));
    }
}
