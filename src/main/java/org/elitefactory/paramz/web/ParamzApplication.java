package org.elitefactory.paramz.web;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.elitefactory.paramz.model.Paramz;

public class ParamzApplication extends WebApplication {

	@SpringBean
	private Paramz paramz;

	@Override
	public Class<? extends Page> getHomePage() {
		return ParamzListPage.class;
	}

	@Override
	protected void init() {
		super.init();
		getComponentInstantiationListeners().add(
				new SpringComponentInjector(this));
	}

	public static ParamzApplication get() {
		return (ParamzApplication) Application.get();
	}

	public Paramz getParamz() {
		return paramz;
	}
}
