package org.elitefactory.paramz.web;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;
import org.elitefactory.paramz.model.Parameter;

public class ParamzModel extends LoadableDetachableModel<List<Parameter>> {

	@Override
	protected List<Parameter> load() {
		return ParamzApplication.getParamzService().getAll();
	}

}
