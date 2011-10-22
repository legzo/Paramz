package org.elitefactory.paramz.web;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;
import org.elitefactory.paramz.model.Parameter;
import org.elitefactory.paramz.model.Paramz;

public class ParamzModel extends LoadableDetachableModel<List<Parameter>> {

	private static final long serialVersionUID = 1L;

	private Paramz paramz;

	public ParamzModel(Paramz paramz) {
		this.paramz = paramz;
	}

	@Override
	public void detach() {
		super.detach();
		this.paramz = null;
	}

	@Override
	protected List<Parameter> load() {
		return paramz.getAll();
	}

}
