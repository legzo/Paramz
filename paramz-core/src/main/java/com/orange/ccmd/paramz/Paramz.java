package com.orange.ccmd.paramz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paramz implements ConfigurationListener {

	private static final Logger logger = LoggerFactory.getLogger(Paramz.class);

	private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();

	private int refreshDelay = 5000;

	private final CombinedConfiguration config = new CombinedConfiguration();

	private PrettyXMLConfiguration localConfig;
	private String localConfigurationFile;

	public Paramz() {
		logger.debug("Initializing Paramz");
		config.setNodeCombiner(new OverrideCombiner());
		config.setForceReloadCheck(true);
		config.addConfigurationListener(this);
	}

	public void setParam(final String key, final String value) {
		if (value != null && !value.equals(getConfig().getString(key))) {
			logger.debug("Setting param {}, value={}", key, value);

			if (localConfig != null) {
				localConfig.setProperty(key, value);
			} else {
				config.setProperty(key, value);
			}

		}
		parameters.get(key).setValue(value);
	}

	public AbstractConfiguration getConfig() {
		return config;
	}

	/**
	 * Add a particular file to the list of available configuration sources
	 * 
	 * @param configurationFilePath
	 * @return
	 */
	public PrettyXMLConfiguration addConfigurationSource(final String configurationFilePath) {
		try {
			final PrettyXMLConfiguration xmlConfiguration = new PrettyXMLConfiguration(configurationFilePath);
			final FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
			reloadingStrategy.setRefreshDelay(refreshDelay);
			xmlConfiguration.setReloadingStrategy(reloadingStrategy);

			config.addConfiguration(xmlConfiguration);

			logger.info("Added new config file: {}", configurationFilePath);

			initParamzCache();

			return xmlConfiguration;
		} catch (final ConfigurationException e) {
			logger.warn("Could not find configuration file @ {}", configurationFilePath);
			return null;
		}
	}

	public void initParamzCache() {
		final Iterator<String> keys = config.getKeys();

		if (keys != null) {
			while (keys.hasNext()) {
				final String key = keys.next();
				parameters.put(key, new Parameter(key, config.getString(key)));
			}
		}
	}

	public List<Parameter> getAll() {
		final ArrayList<Parameter> values = new ArrayList<Parameter>(parameters.values());
		Collections.sort(values);

		return values;
	}

	public void saveToFile() {
		if (localConfig != null) {
			try {
				logger.debug("Saving local config to file : {}", localConfigurationFile);
				localConfig.save(localConfigurationFile);

				for (final Parameter param : parameters.values()) {
					param.setDirty(false);
				}

			} catch (final ConfigurationException e) {
				logger.error("Could not save combined configuration...", e);
			}
		} else {
			logger.error("Cannot save local config, there is no such file");
		}
	}

	public void setRefreshDelay(final int refreshDelay) {
		this.refreshDelay = refreshDelay;
	}

	Parameter getParam(final String key) {
		return parameters.get(key);
	}

	public void setDefaultConfigurationFile(final String defaultConfigurationFile) {
		addConfigurationSource(defaultConfigurationFile);
	}

	public void setLocalConfigurationFile(final String localConfigurationFile) {
		this.localConfigurationFile = localConfigurationFile;

		localConfig = addConfigurationSource(localConfigurationFile);
	}

	@Override
	public void configurationChanged(final ConfigurationEvent event) {
		logger.trace("config changed");

		final Iterator<String> keys = config.getKeys();

		if (keys != null) {
			while (keys.hasNext()) {
				final String key = keys.next();
				final Parameter parameter = parameters.get(key);
				if (parameter != null) {
					parameter.setValue(config.getString(key));
				}
			}
		}
	}
}
