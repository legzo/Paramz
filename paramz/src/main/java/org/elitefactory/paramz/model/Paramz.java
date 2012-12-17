package org.elitefactory.paramz.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paramz implements ConfigurationListener {

	private static final Logger logger = LoggerFactory.getLogger(Paramz.class);

	private final Map<String, Set<ParamerUpdateListener>> listeners = new HashMap<String, Set<ParamerUpdateListener>>();

	private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();

	private final CombinedConfiguration config = new CombinedConfiguration();

	private String localConfigurationFile;

	public Paramz() {
		logger.debug("Initializing Paramz");
		config.setNodeCombiner(new OverrideCombiner());
		config.addConfigurationListener(this);
	}

	public Paramz(final String... configurationSources) {
		this();
		if (configurationSources.length <= 0) {
			throw new IllegalArgumentException("You must provide at least one configuration source !");
		}

		setConfigurationSources(Arrays.asList(configurationSources));
	}

	public Parameter getParam(final String key) {
		return parameters.get(key);
	}

	public String getString(final String key) {
		return config.getString(key);
	}

	public boolean getBoolean(final String key) {
		return config.getBoolean(key);
	}

	public void setParam(final String key, final String value) {
		if (value != null && !value.equals(getString(key))) {
			logger.debug("Setting param {}, value={}", key, value);
			config.setProperty(key, value);
		}
		parameters.get(key).setValue(value);

	}

	public void addListener(final String[] keysToListenTo, final ParamerUpdateListener listener) {
		for (final String keyToListenTo : keysToListenTo) {
			addListener(keyToListenTo, listener);
		}
	}

	private void addListener(final String keyToListenTo, final ParamerUpdateListener listener) {
		if (keyToListenTo != null) {
			Set<ParamerUpdateListener> existingListenersForKey = listeners.get(keyToListenTo);
			if (existingListenersForKey == null) {
				existingListenersForKey = new HashSet<ParamerUpdateListener>();
				listeners.put(keyToListenTo, existingListenersForKey);
			}
			existingListenersForKey.add(listener);
		}
	}

	/**
	 * Can be used for Spring injection. Please note that the order is important : the first file will override the
	 * following and so on.
	 * 
	 * @param configurationFilePaths
	 *            list of configuration paths (can be absolute or classpath relative)
	 */
	public void setConfigurationSources(final List<String> configurationFilePaths) {

		if (configurationFilePaths != null) {
			localConfigurationFile = configurationFilePaths.get(0);

			for (final String configurationFilePath : configurationFilePaths) {
				addConfigurationSource(configurationFilePath);
			}
		}

		initCache();
	}

	/**
	 * Add a particular file to the list of available configuration sources
	 * 
	 * @param configurationFilePath
	 */
	public void addConfigurationSource(final String configurationFilePath) {
		try {
			config.addConfiguration(new XMLConfiguration(configurationFilePath));
			logger.info("Added new config file: {}", configurationFilePath);
		} catch (final ConfigurationException e) {
			logger.warn("Could not find configuration file @ {}", configurationFilePath);
		}
	}

	public void configurationChanged(final ConfigurationEvent event) {
		final String keyThatTriggeredEvent = event.getPropertyName();
		if (keyThatTriggeredEvent != null && event.getType() == AbstractConfiguration.EVENT_SET_PROPERTY
				&& event.isBeforeUpdate()) {
			logger.trace("Configuration changed because of update on property {}", keyThatTriggeredEvent);

			final Set<ParamerUpdateListener> listenersForThisKey = listeners.get(keyThatTriggeredEvent);

			if (listenersForThisKey != null) {
				for (final ParamerUpdateListener listenerForThisKey : listenersForThisKey) {
					logger.debug("Triggering listener {}", listenerForThisKey.getClass().getSimpleName());
					listenerForThisKey.onConfigChange();
				}
			}
		}
	}

	public void initCache() {
		final Iterator<String> keys = config.getKeys();

		if (keys != null) {
			while (keys.hasNext()) {
				final String key = keys.next();
				parameters.put(key, new Parameter(key, config.getString(key)));
			}
		}
	}

	public List<Parameter> getAll() {
		return new ArrayList<Parameter>(parameters.values());
	}

	public void saveToFile() {
		final XMLConfiguration result = new XMLConfiguration(config);
		try {
			logger.debug("Saving combined config to file : {}", localConfigurationFile);
			result.save(localConfigurationFile);

			for (final Parameter param : parameters.values()) {
				param.setDirty(false);
			}

		} catch (final ConfigurationException e) {
			logger.error("Could not save combined configuration...", e);
		}
	}

}
