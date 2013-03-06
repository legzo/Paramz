package com.orange.ccmd.paramz.web;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.orange.ccmd.paramz.Parameter;

public class ParamzModel extends LoadableDetachableModel<List<Parameter>> {

	private final String configProviderId;

	public ParamzModel(final String configProviderId) {
		this.configProviderId = configProviderId;
	}

	@Override
	protected List<Parameter> load() {
		return ParamzApplication.getConfigProvider(configProviderId).getAll();
	}

}
