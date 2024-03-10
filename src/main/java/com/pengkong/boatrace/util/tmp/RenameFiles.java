package com.pengkong.boatrace.util.tmp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.FileUtil;

public class RenameFiles {

    public void execute(String dir) throws Exception {
        List<File> files = FileUtil.listFilesByExtensionRecursively(dir, "png");
        for (File file : files) {
            String targetPath = null;
            // ex) 272847_ip_2T_12_wk123+race_1442_1_x_x_x_97103_1.01~99_3.8.png
            String namePart = file.getName().replace(".png", "");
            if (namePart.startsWith("ip")) {
            	String[] token = namePart.split(Delimeter.UNDERBAR.getValue());
            	String newNamepart = String.join(Delimeter.UNDERBAR.getValue() ,
            			token[0], token[1], token[2], token[3], token[4], token[5], token[6], 
            			token[7], token[8], token[9], token[10], token[12], token[11]
            			);
            	targetPath = file.getParent() + "/" + newNamepart + ".png";
            }
            
            file.renameTo(new File(targetPath));
            System.out.println(targetPath);
        }
    }

    public void executeJsj(String dir) throws Exception {
        List<File> files = FileUtil.listFilesByExtensionRecursively(dir, "png");
        for (File file : files) {
            String targetPath = null;
            // ex)  SG_1T_1_prob1+wk123_i09-100_-1_79100_JSJ-S3_555_123_10146_398_0.3.png
            //   -> SG_1T_1_prob1+wk123_i09-100_~1_79100_JSJ-S3_555_123_10146_398_0.3.png
            String namePart = file.getName().replace(".png", "");
        	String[] token = namePart.split(Delimeter.UNDERBAR.getValue());
        	String renStr = token[5].replace("-", "~"); 
        	String newNamepart = String.join(Delimeter.UNDERBAR.getValue() ,
        			token[0], token[1], token[2], token[3], token[4], renStr, token[6], 
        			token[7], token[8], token[9], token[10], token[11], token[12]
        			);
        	targetPath = file.getParent() + "/" + newNamepart + ".png";

        	if (renStr.startsWith("~")) {
                file.renameTo(new File(targetPath));
                System.out.println(targetPath);
        	}
        }
    }

    public void executeStep2(String dir) throws Exception {
        List<File> files = Arrays.asList(FileUtil.listFilesByExtension(dir, "tsv"));
        for (File file : files) {
            String targetPath = null;
            // ex)  30002_ip_3R_123_i02_hitrate_1.1_30_1_BIG5-777_2020_0.05~0.06=1_x_x
            // 30002 ip 3R 123 i02 hitrate 1.1 30 1 BIG5-777 2020 0.05~0.06=1 x x
            String namePart = file.getName().replace(".tsv", "");
        	String[] token = namePart.split(Delimeter.UNDERBAR.getValue());
        	
        	String newNamepart = String.join(Delimeter.UNDERBAR.getValue() ,
        			token[1], token[2], token[3], token[8], token[0], token[4], 
        			token[5], token[6], token[7], token[9], token[10], token[11], token[12], token[13]
        			);
        	targetPath = file.getParent() + "/" + newNamepart + ".tsv";

            file.renameTo(new File(targetPath));
            System.out.println(targetPath);
        }
    }
    
    public static void main(String[] args) {
        String dir = "c:/boatrace/online/groups";
        //String dir = "D:/Dev/experiment/expr10/tmp";
        try {
            new RenameFiles().executeStep2(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
