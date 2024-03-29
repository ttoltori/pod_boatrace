package com.pengkong.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class CommandExecutor {

    public static int execute(String cmd, List<String> params, long timeoutSec) {
        List<String> cmdAndParams = new LinkedList<>();
        cmdAndParams.add(cmd);
        cmdAndParams.addAll(params);

        return execute(cmdAndParams, timeoutSec, null, null);
    }
    
    /**
     * 
     * @param cmdAndParams コマンドライン文字列のリスト ex) java -cp C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/weka.jar;C:/Dev/workspace/Oxygen/pod_boatrace/target/classes; weka.classifiers.bayes.BayesNet -D -t "D:/Dev/experiment/expr10/arff/0001_rank3.arff" -d "D:/Dev/experiment/expr10/model_release/0001/nopattern/0001_nopattern_20210730_rank3.model" -no-cv -split-percentage 80
     * @param timeoutSec 実行後待機タイムアウト 
     * @param filepathStdout 標準出力のリダイレクト先ファイル, nullの場合はコンソルに出力
     * @param envMap 環境変数のmap
     * @return
     */
    public static int execute(List<String> cmdAndParams, long timeoutSec, String filepathStdout, Map<String, String> envMap) {
        ProcessBuilder builder = new ProcessBuilder(cmdAndParams);
        builder.redirectErrorStream(true);  // 標準エラー出力の内容を標準出力にマージする
        
        // 標準出力のリダイレクト
        if (filepathStdout != null) {
        	builder.redirectOutput(Redirect.appendTo(new File(filepathStdout)));
        }

        // 環境変数設定
        if (envMap != null) {
        	Map<String, String> env = builder.environment();
        	for (String key : envMap.keySet()) {
        		if (env.containsKey(key)) {
        			// すでに存在する変数の場合、既存値にアペンドする
        			env.put(key, env.get(key) + ";" + envMap.get(key)); 
        		} else { 
        			env.put(key, envMap.get(key));
        		}
        	}
        }
        
        Process process;
        try {
            process = builder.start();
        } catch (Exception e) {
        	e.printStackTrace();
            throw new CommandExecuteFailedException("Command launch failed. [cmd: " + cmdAndParams + "]", e);
        }

        int exitCode = -1;
        try {
        	if (filepathStdout == null) {
            	print(process.getInputStream());         	
        	}

            boolean end = process.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (end) {
                exitCode = process.exitValue();
            } else {
                throw new CommandExecuteFailedException("Command timeout. [CommandPath: " + cmdAndParams + "]");
            }

        } catch (InterruptedException e) {
            throw new CommandExecuteFailedException("Command InterruptedException. [CommandPath: " + cmdAndParams + "]", e);
        } catch (IOException e) {
        	throw new CommandExecuteFailedException("Command IOException. [CommandPath: " + cmdAndParams + "]", e);
		} finally {
            if (process.isAlive()) {
                process.destroy(); // プロセスを強制終了
            }
        }

        return exitCode;
    }

	private static void print(InputStream is) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}
    private CommandExecutor() {
    }
    
    
    @SuppressWarnings("serial")
	public static class CommandExecuteFailedException extends RuntimeException {

        public CommandExecuteFailedException(String message) {
            super(message);
        }

        public CommandExecuteFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

