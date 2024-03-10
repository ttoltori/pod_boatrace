package com.pengkong.boatrace.weka.automation;

import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class ArffCreator {

	Logger logger = LoggerFactory.getLogger(ArffCreator.class);
	
	NominalManager nominalManager;
	
	DefineFileParser defParser;

	PropertyUtil prop = PropertyUtil.getInstance();
	
	public ArffCreator() {
		 nominalManager = new NominalManager();
		 defParser = new DefineFileParser();
	}

	public void create(String dir, String defFileName, String fromYmd, String toYmd) throws Exception {
		
		// parse def file
		defParser.parseFile(dir + "/" + defFileName);
		String arffName = defFileName.split("\\.")[0];
		// write file (ex: {dir}/2F_01-01.arff.header)
		String headerFilepath = dir + "/model_arff/" + arffName + ".arff.header";
		
		logger.info("creating temporary arff header file...");
		createArffHeader(headerFilepath);

		String bodyFilepath = dir + "/model_arff/" + arffName + ".arff.body";
		String sql = defParser.getValue("sql");
		sql = sql.replace("{fromYmd}", fromYmd);
		sql = sql.replace("{toYmd}", toYmd);
		sql = sql.replace("{outfile}", bodyFilepath);
		
		logger.info("creating temporary arff body file...");
		createArffBody(sql);
		
		String arffFilepath = dir + "/model_arff/" + arffName + ".arff";
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "copy", 
				headerFilepath.replaceAll("/", "\\\\") + "+" + bodyFilepath.replaceAll("/", "\\\\"), 
				arffFilepath.replaceAll("/", "\\\\"));
		Process process = pb.start();
		int ret = process.waitFor();
		if (ret != 0) {
			throw new Exception("Process execution failed.");
		}
		logger.info("arff file saved. " + arffFilepath);
		
		FileUtil.deleteFileIfExist(headerFilepath);
		FileUtil.deleteFileIfExist(bodyFilepath);
		logger.info("temporary files are deleted.");
	}
	
	private void createArffHeader(String headerFilepath) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("% " + defParser.getValue("name")); sb.append(System.lineSeparator());
		sb.append("@RELATION boatrace"); sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		String[] tokenAttrs = defParser.getValue("attributes").split(",");
		for (String attr : tokenAttrs) {
			sb.append(convertAttribute(attr));
			sb.append(System.lineSeparator());
		}
		
		String strClassValues = defParser.getValue("class_values");
		String classValues = null;
		if (strClassValues.startsWith("range")) {
			// ex) range:100:500:10  10単位増分の100~500(prize)
			String[] token = strClassValues.split(":");
			classValues = nominalManager.getRange(
					Integer.parseInt(token[1]), Integer.parseInt(token[2]), Integer.parseInt(token[3]));
		} else {
			classValues = strClassValues;
		}
		 
		sb.append("@ATTRIBUTE class {" + classValues + "}");sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		sb.append("@DATA");
		
		FileUtil.writeFile(headerFilepath, sb.toString()); // 最後にnewline追加される
	}
	
	private void createArffBody(String sql) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);
			procedureExecutorMapper.executeSql(sql);
		}
	}
	
	private String convertAttribute(String attribute) {
		String[] tokenAttr = attribute.split(":");
		String attrName = tokenAttr[0].trim();
		String attrType = tokenAttr[1].trim();
		
		// nominal attribute
		if (attrType.equals("nominal")) {
			return "@ATTRIBUTE " + attrName + " " + nominalManager.getNominalAttr(attrName);
		} 
		
		// numeric attribute
		return "@ATTRIBUTE " + attrName + " NUMERIC"; 
	}
	
	public static void main(String[] args) {
//		args = new String[] {
//			"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels",
//			"1_1T_oddspredict_2019.def",
//			"20190125",
//			"20190220"
//		};

    	if (args.length != 4) {
    		System.out.println("Usage: ArffCreator {work directory} {definition file name} {from ymd} {to ymd} ");
    		System.exit(1);
    	}
    	
		ArffCreator ac = new ArffCreator();
		try {
			String dir = args[0];
			String defFileName = args[1];
			String fromYmd = args[2];
			String toYmd = args[3];
			
			ac.create(dir, defFileName, fromYmd, toYmd);
			
		} catch (Exception e) {
			ac.logger.error("failed.", e);
		}
	}
}
