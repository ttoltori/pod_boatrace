package com.pengkong.boatrace.weka.automation;

import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.common.FileEx;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * 커맨드라인 arff파일 생성처리
 *  def파일내 필수 요소
 *    attributes, class_values, sql_model
 * 
 * @author qwerty
 *
 */
public class ArffCreatorCli {

	Logger logger = LoggerFactory.getLogger(ArffCreatorCli.class);
	
	NominalManager nominalManager;
	
	DefineFileParser defParser;
	
	PropertyUtil prop = PropertyUtil.getInstance();
	
	public ArffCreatorCli() {
		 nominalManager = new NominalManager();
		 defParser = new DefineFileParser();
	}

	public void create(String arffFilepath, String defFilepath, String fromYmd, String toYmd) throws Exception {
		FileEx arFile = new FileEx(arffFilepath);
		String dir = arFile.getDir();
		// parse def file
		defParser.parseFile(defFilepath);
		String arffName = arFile.getNameOnly();
		// write file (ex: {dir}/2F_01-01.arff.header)
		String headerFilepath = dir + "/" + arffName + ".arff.header";
		
		logger.info("creating temporary arff header file...");
		createArffHeader(headerFilepath);

		String bodyFilepath = dir + "/" + arffName + ".arff.body";
		String sql = defParser.getValue("sql_model");
		sql = sql.replace("{fromYmd}", fromYmd);
		sql = sql.replace("{toYmd}", toYmd);
		sql = sql.replace("{outfile}", bodyFilepath);
		
		logger.info("creating temporary arff body file...");
		createArffBody(sql);
		
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
		String resource = prop.getString("target_db");
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
//			"C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr10\\tmp\\practice_rank2.arff",
//			"C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr10\\tmp\\weka_practice_rank2.def",
//			"20190101",
//			"20191231"
//		};
		   		
    	if (args.length != 4) {
    		System.out.println("Usage: ArffCreatorCli {arff full path} {definition file path} {from ymd} {to ymd} ");
    		System.exit(1);
    	}
    	
		ArffCreatorCli ac = new ArffCreatorCli();
		try {
			PropertyUtil.getInstance().putProperty("target_db", "mybatis-config.1.xml");
			String arffFullpath = args[0];
			String defFullpath = args[1];
			String fromYmd = args[2];
			String toYmd = args[3];
			
			ac.create(arffFullpath, defFullpath, fromYmd, toYmd);
			
		} catch (Exception e) {
			ac.logger.error("failed.", e);
		}
	}
}
