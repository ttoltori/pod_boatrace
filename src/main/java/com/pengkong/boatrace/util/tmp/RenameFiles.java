package com.pengkong.boatrace.util.tmp;

import java.io.File;
import java.util.List;

import com.pengkong.common.FileUtil;

public class RenameFiles {

    public void execute(String dir) throws Exception {
        List<File> files = FileUtil.listFilesByExtensionRecursively(dir, "png");
        for (File file : files) {
            String srcPath = file.getAbsolutePath();
            String targetPath = null;
            if (srcPath.contains("FPI-3_1232_0")) {
                targetPath = srcPath.replace("FPI-3_1232_0_", "FPI-3_");
            } else if (srcPath.contains("FPI-3_1232_1")) {
                targetPath = srcPath.replace("FPI-3_1232_1_", "FPI-3_");
            } else {
                continue;
            }
            file.renameTo(new File(targetPath));
            System.out.println(srcPath + " -> " + targetPath);
        }
    }
    
    public static void main(String[] args) {
        String dir = "D:/Dev/experiment/expr10/simulation_step1/simul4_1_FPI-2";
        try {
            new RenameFiles().execute(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
