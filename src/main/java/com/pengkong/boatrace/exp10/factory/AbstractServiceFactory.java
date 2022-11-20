package com.pengkong.boatrace.exp10.factory;

import com.pengkong.boatrace.exp10.enums.ServiceType;
import com.pengkong.boatrace.exp10.remote.server.service.AbstractService;

/**
 * ServiceFactory interface
 * @author ttolt
 *
 */
public abstract class AbstractServiceFactory {
	public abstract AbstractService create(ServiceType serviceType);
}
