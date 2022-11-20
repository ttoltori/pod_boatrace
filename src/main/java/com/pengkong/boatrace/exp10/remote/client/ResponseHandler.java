package com.pengkong.boatrace.exp10.remote.client;

/**
 * websocketの応答文字列ハンドラ.
 * @author ttolt
 *
 */
public interface ResponseHandler {
	public void handle(String jsonStr);
}
