package com.pengkong.boatrace.exp10.remote.server.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.exp10.enums.FeatureType;
import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.StringUtil;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class BoatClassifier extends AbstractBoatClassifier {
	Logger logger = LoggerFactory.getLogger(BoatClassifier.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	// weka用dataset */
	private Instances dataRow;
	
	/** モデル情報 */
	protected ModelInfo mi;
	
	/** weka classifier binary instance */
	protected Classifier classifier;
	
	/** ex) ["1","2","3","4","5","6"] */
	protected List<String> listClassValue;
	
	/** arffに宣言されていた@Attributeを保持する */
	private HashMap<String, Attribute> mapAttribute;
	
	boolean isInitialized = false;
	
	public BoatClassifier(ModelInfo mi) {
		this.mi = mi;
	}
	
	void ensureInitialized() throws Exception {
		if (!isInitialized) {
			initialize();
			isInitialized = true;
		}
	}
	
	/**
	 * モデル情報を基にweka classifierに必要な事前設定を行う。 mapAttribute, listClassValueを設定しておく
	 * @param mi モデル情報
	 * @throws Exception
	 */
	void initialize() throws Exception  {
		this.mapAttribute = new HashMap<>();
		this.listClassValue = new ArrayList<>();
		
		// load model file
		String modelFilepath = createModelFilepath(mi);
		this.classifier = (Classifier) SerializationHelper.read(modelFilepath);
		
		ArrayList<Attribute> attributes = new ArrayList<>();
		NominalManager nominalMgr = NominalManager.getInstance();
		
		// set attributes
		Attribute attr;
		for (int i = 0; i < mi.arffNames.length; i++) {
			String attrName = mi.arffNames[i];
			String attrType = mi.arffTypes[i];
			
			if (attrType.equals(FeatureType.NOMINAL.getValue())) {
				attr = new Attribute(attrName, nominalMgr.getNominalAttrList(attrName));
			} else { //if (attrType.equals("numeric")) {
				attr = new Attribute(attrName);
			}
			mapAttribute.put(attrName, attr);
			attributes.add(attr);
		}
		
		// set class values
		this.listClassValue = Arrays.asList(mi.classValues);

		attr = new Attribute("class", this.listClassValue);
    	attributes.add(attr);
    	
    	// create Instance wrapper with attributes
		dataRow = new Instances("TestInstances", attributes, 0);
		dataRow.setClassIndex(dataRow.numAttributes() - 1);
		
	}
	
	@Override
	public double[] predictProba(RemoteRequestParam req) throws Exception {
		ensureInitialized();
		
		return classifier.distributionForInstance(createInstance(req));
	}
	
	/**
	 * 予測要求データからweka用のInstanceを生成して取得する.
	 * @param req 予測要求
	 * @return weka classifier用のInstance
	 */
    private Instance createInstance(RemoteRequestParam req) {
    	dataRow.clear();
    	
		//String[] tokenAttrs = defParser.getValue("attributes").split(",");
		
    	Instance instance = new DenseInstance(mi.arffNames.length + 1);
    	// set attribute values
		for (int i = 0; i < mi.arffNames.length; i++) {
			String attrName = mi.arffNames[i];
			String attrType = mi.arffTypes[i];
			String attrValue = req.values[i];
			
			if (attrType.equals(FeatureType.NOMINAL.getValue())) {
				instance.setValue(mapAttribute.get(attrName), attrValue);
			} else { // if (attrType.equals("numeric")) {
				instance.setValue(mapAttribute.get(attrName), Double.valueOf(attrValue));
			}
		}
		
		// set class value
    	instance.setValue(mi.arffNames.length, 0);
    	dataRow.add(instance);
    	
    	return dataRow.firstInstance();
    }
    
	/**
	 * モデルファイル情報からモデルファイルのfullpathを取得する。
	 * @param req モデルファイル情報
	 * @return file fullpath
	 */
	private String createModelFilepath(ModelInfo req) {
		// 注意 exNoでなくmodelNoである
		return prop.getString("dir_model_release") + StringUtil.leftPad(req.modelNo, BoatConst.LEFT_PAD, "0") +
		"/" + req.rankNo + "/" + req.pattern + "/" + req.fileName;
	}

	public void destroy() {
		dataRow = null;
		mapAttribute = null;
		listClassValue = null;
		classifier = null;
	}
}
