package com.pengkong.boatrace.online.tohyo.pc;

import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.tohyo.AbstractTohyoRunner;
import com.pengkong.boatrace.online.tohyo.TohyoExecutor;
import com.pengkong.boatrace.online.tohyo.TohyoRunnerListener;

/** PC投票スレッド */
public class PcTohyoRunner extends AbstractTohyoRunner {
	
	public PcTohyoRunner(AbstractApiProvider apiProvider, TohyoRunnerListener listener) {
		super(apiProvider, listener);
	}

	@Override
	protected void setup() {
		// 投票実施クラス
		tohyoExecutor = new TohyoExecutor(apiProvider);
	}
}
