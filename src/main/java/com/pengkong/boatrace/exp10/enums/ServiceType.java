package com.pengkong.boatrace.exp10.enums;

/**
 * WEKAの場合全てのClassifierが同一インターフェースであるためgenericにする
 * pythonの場合,classifier毎にベットサービスが必要なためclassifier毎にサービスを作成する
 * ex) cf_bayesnet-filtered-1_wk, cf_bayesnet-1_wkなどのalgorithmの場合、
 *     cf_generic_wkの一つのサービスタイプで対応できる
 * @author ttolt
 *
 */
public enum ServiceType {
	CF_GENERIC_WEKA("cf_generic_wk"); // -> ClassificationService
//	CF_BAYESNET_WEKA("cf_bayesnet_wk"), // -> ClassificationService
//	CF_NAIVEBAYES_WEKA("cf_naivebayes_wk"), // -> ClassificationService
//	RG_BAYESNET_WEKA("rg_bayesnet_wk"); // -> ClassificationService

	private final String serviceType;
	
	private ServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	public String getValue() {
		return this.serviceType;
	}
}
