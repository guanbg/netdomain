package com.platform.cubism.service.core;

import com.platform.cubism.base.Json;
import com.platform.cubism.service.remote.RemoteExecutor;
import com.platform.cubism.service.remote.RemoteFactory;
import com.platform.cubism.struc.RetHead;

public class JmsRemoteService extends AbstractService {

	public JmsRemoteService(String serviceId) {
		super(serviceId);
	}

	@Override
	protected Json perform(Json in) {
		RemoteExecutor remoteExecutor = RemoteFactory.getJmsExecutor();
		in.getObject().addField(RetHead.SERVICE.value(), getServiceId());
		Json result = remoteExecutor.execute(in);
		return result;
	}

	@Override
	protected Json validate(Json in) {
		// TODO Auto-generated method stub
		return null;
	}

}