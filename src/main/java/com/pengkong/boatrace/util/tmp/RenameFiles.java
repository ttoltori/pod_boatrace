package com.pengkong.boatrace.util.tmp;

import java.io.File;
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
    
    public static void main(String[] args) {
        String dir = "D:/Dev/experiment/expr10/simulation_step1/simul4_4_FPI-5";
        try {
            new RenameFiles().execute(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
