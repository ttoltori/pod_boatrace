package com.pengkong.boatrace.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.server.manager.PatternStatRunner;
import com.pengkong.boatrace.server.stat.StStatMlPtnFinal;

public class PatternStatRunnerPool {

	public List<StStatMlPtnFinal> liStStatMlPtnFinals = new ArrayList<StStatMlPtnFinal>();
	private List<PatternStatRunner> listRunner = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(PatternStatRunnerPool.class);

	int poolSize;
	public PatternStatRunnerPool(int poolSize) {
		this.poolSize = poolSize;
	}

	public void addRunner(PatternStatRunner runner) {
		listRunner.add(runner);
	}
	
	/**
	 * DB레코드 리스트를 추가한다.
	 * @param listDbRec
	 * @throws Exception
	 */
	public List<StStatMlPtnFinal> execute() throws Exception {
		List<StStatMlPtnFinal> results = new ArrayList<>();
		ExecutorService exec = Executors.newFixedThreadPool(poolSize);
		
		List<Future<List<StStatMlPtnFinal>>> listFuture = new ArrayList<>();
		for (PatternStatRunner runner : listRunner) {
			Future<List<StStatMlPtnFinal>> future = exec.submit(runner);
			listFuture.add(future);
		}
		int cnt = 0;
		for (Future<List<StStatMlPtnFinal>> future : listFuture) {
			cnt++;
			List<StStatMlPtnFinal> result = future.get();
			results.addAll(result);
			logger.info(cnt + "/" + listRunner.size() + " total result count=" + results.size());
		}
		
		exec.shutdown();
		return results;
	}
}
