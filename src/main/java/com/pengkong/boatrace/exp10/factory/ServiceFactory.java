package com.pengkong.boatrace.exp10.factory;

import com.pengkong.boatrace.exp10.enums.ServiceType;
import com.pengkong.boatrace.exp10.remote.server.service.AbstractService;
import com.pengkong.boatrace.exp10.remote.server.service.ClassificationService;

public class ServiceFactory extends AbstractServiceFactory {
	@Override
	public AbstractService create(ServiceType serviceType) {
		if (serviceType.equals(ServiceType.CF_GENERIC_WEKA)) {
			return ClassificationService.getInstance();
		}
		
		return null;
	}
}
