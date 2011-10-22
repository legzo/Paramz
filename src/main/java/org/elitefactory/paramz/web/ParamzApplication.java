package org.elitefactory.paramz.web;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class ParamzApplication extends WebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return ParamzListPage.class;
	}

}
