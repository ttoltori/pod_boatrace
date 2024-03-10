package com.pengkong.boatrace.exp02;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.PropertyUtil;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class BoatClassifier {
	PropertyUtil prop = PropertyUtil.getInstance();
	DefineFileParser defParser;
	private Instances dataRow;
	
	protected Classifier classifier;
	
	protected List<String> classVal;
	
	private HashMap<String, Attribute> mapAttribute;
	
	public BoatClassifier() {
		this.defParser = new DefineFileParser();
		this.mapAttribute = new HashMap<>();
		this.classVal = new ArrayList<>();
	}
	
	public void initialize(String defFilepath, String modelFilepath) throws Exception  {
		ArrayList<Attribute> attributes = new ArrayList<>();
		
		// parse def file
		defParser.parseFile(defFilepath);
		
		// set attributes
		String[] tokenAttrs = defParser.getValue("attributes").split(",");
		Attribute attr;
		
		NominalManager nominalMgr = new NominalManager();
		
		for (String strAttr : tokenAttrs) {
			String[] tokenAttr = strAttr.split(":");
			String attrName = tokenAttr[0].trim();
			String attrType = tokenAttr[1].trim();
			
			if (attrType.equals("nominal")) {
				attr = new Attribute(attrName, nominalMgr.getNominalAttrList(attrName));
			} else { //if (attrType.equals("numeric")) {
				attr = new Attribute(attrName);
			}
			mapAttribute.put(attrName, attr);
			attributes.add(attr);
		}
		
		// set class values
		String strClassValues = defParser.getValue("class_values");
		this.classVal = Arrays.asList(strClassValues.split(","));
		attr = new Attribute("class", this.classVal);
    	attributes.add(attr);
    	
    	// create Instance wrapper with attributes
		dataRow = new Instances("TestInstances", attributes, 0);
		dataRow.setClassIndex(dataRow.numAttributes() - 1);
		
		// load model file
		this.classifier = (Classifier) SerializationHelper.read(modelFilepath);
	}

	public String predictFromHashmap(HashMap<String, ?> hashmap) throws Exception {
    	Instances insts = createInstanceFromHashMap(hashmap);
    	return classVal.get((int) classifier.classifyInstance(insts.firstInstance()));
    	
	}
	
    protected Instances createInstanceFromHashMap(HashMap<String, ?> hashmap) {
    	dataRow.clear();
    	 
    	
		String[] tokenAttrs = defParser.getValue("attributes").split(",");
    	Instance instance = new DenseInstance(tokenAttrs.length+1);
    	// set attribute values
		for (String strAttr : tokenAttrs) {
			String[] tokenAttr = strAttr.split(":");
			String attrName = tokenAttr[0].trim();
			String attrType = tokenAttr[1].trim();
			
			if (attrType.equals("nominal")) {
				try {
					instance.setValue(mapAttribute.get(attrName), String.valueOf(hashmap.get(attrName)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // if (attrType.equals("numeric")) {
				try {
					Object val = hashmap.get(attrName);
					if (val instanceof BigDecimal) {
						instance.setValue(mapAttribute.get(attrName), ((BigDecimal)val).doubleValue());	
					} else if (val instanceof Integer) {
						instance.setValue(mapAttribute.get(attrName), ((Integer)val).doubleValue());	
					} else {
						instance.setValue(mapAttribute.get(attrName), (double)val);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// set class value
    	instance.setValue(tokenAttrs.length, 0);
    	dataRow.add(instance);
    	
    	return dataRow;
    }
    
    public static BoatClassifier create(BoatClassifierInfo cInfo) throws Exception {
    	BoatClassifier cls = new BoatClassifier();
    	cls.initialize(cInfo.defineFilepath, cInfo.modelFilepath);
    	
    	return cls;
    }

	public void destroy() {
		defParser = null;
		mapAttribute = null;
		classVal = null;
		classifier = null;
	}
}
