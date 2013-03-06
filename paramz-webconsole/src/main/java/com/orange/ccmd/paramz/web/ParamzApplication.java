package com.orange.ccmd.paramz.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.ccmd.paramz.Paramz;

public class ParamzApplication extends WebApplication {

	private final Map<String, Class<? extends ParamzListPage>> mappings = new HashMap<String, Class<? extends ParamzListPage>>();
	private final static Map<String, Paramz> configProviders = new HashMap<String, Paramz>();

	private static final Logger logger = LoggerFactory.getLogger(ParamzApplication.class);

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	public void setPages(final Map<Class<ParamzListPage>, Paramz> pages) throws InstantiationException,
			IllegalAccessException {
		for (final Entry<Class<ParamzListPage>, Paramz> entry : pages.entrySet()) {
			final Class<ParamzListPage> page = entry.getKey();
			final Paramz configProvider = entry.getValue();

			final String configProviderId = page.getSimpleName().toLowerCase();
			logger.info("mounting {} to /{}", entry.getValue(), configProviderId);
			configProviders.put(configProviderId, configProvider);
			mappings.put(configProviderId, page);
		}
	}

	@Override
	protected void init() {
		super.init();
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));

		getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);

		for (final Entry<String, Class<? extends ParamzListPage>> entry : mappings.entrySet()) {
			mountPage(entry.getKey(), entry.getValue());
		}
	}

	public static ParamzApplication get() {
		return (ParamzApplication) Application.get();
	}

	public static Paramz getConfigProvider(final String configProviderId) {
		return configProviders.get(configProviderId);
	}

}
