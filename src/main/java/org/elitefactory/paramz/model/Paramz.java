package org.elitefactory.paramz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paramz implements ConfigurationListener {

	private static final Logger logger = LoggerFactory.getLogger(Paramz.class);

	private Map<String, Set<ParamerUpdateListener>> listeners = new HashMap<String, Set<ParamerUpdateListener>>();

	private CombinedConfiguration config = new CombinedConfiguration();

	public Paramz() {
		logger.debug("Initializing Paramz");
		config.setNodeCombiner(new OverrideCombiner());
		config.addConfigurationListener(this);
	}

	public Paramz(String... configurationSources) {
		super();
		for (String configurationSource : configurationSources) {
			addConfigurationSource(configurationSource);
		}
	}

	public String getParam(String key) {
		return config.getString(key);
	}

	public void setParam(String key, String value) {
		if (!value.equals(getParam(key))) {
			logger.debug("Setting param {}, value={}", key, value);
			config.setProperty(key, value);
		}
	}

	public void addListener(String[] keysToListenTo,
			ParamerUpdateListener listener) {
		for (String keyToListenTo : keysToListenTo) {
			addListener(keyToListenTo, listener);
		}
	}

	private void addListener(String keyToListenTo,
			ParamerUpdateListener listener) {
		if (keyToListenTo != null) {
			Set<ParamerUpdateListener> existingListenersForKey = listeners
					.get(keyToListenTo);
			if (existingListenersForKey == null) {
				existingListenersForKey = new HashSet<ParamerUpdateListener>();
				listeners.put(keyToListenTo, existingListenersForKey);
			}
			existingListenersForKey.add(listener);
		}
	}

	/**
	 * Can be used for Spring injection. Please note that the order is important
	 * : the first file will override the following and so on.
	 * 
	 * @param configurationFilePaths
	 *            list of configuration paths (can be absolute or classpath
	 *            relative)
	 */
	public void setConfigurationSources(List<String> configurationFilePaths) {
		if (configurationFilePaths != null) {
			for (String configurationFilePath : configurationFilePaths) {
				addConfigurationSource(configurationFilePath);
			}
		}
	}

	/**
	 * Add a particular file to the list of available configuration sources
	 * 
	 * @param configurationFilePath
	 */
	public void addConfigurationSource(String configurationFilePath) {
		try {
			logger.info("Adding new config file: {}", configurationFilePath);
			config.addConfiguration(new PropertiesConfiguration(
					configurationFilePath));
		} catch (ConfigurationException e) {
			logger.error(
					"Something went wrong while initializing configuration sources",
					e);
		}
	}

	public void configurationChanged(ConfigurationEvent event) {
		String keyThatTriggeredEvent = event.getPropertyName();
		if (keyThatTriggeredEvent != null
				&& event.getType() == AbstractConfiguration.EVENT_SET_PROPERTY
				&& event.isBeforeUpdate()) {
			logger.trace(
					"Configuration changed because of update on property {}",
					keyThatTriggeredEvent);

			Set<ParamerUpdateListener> listenersForThisKey = listeners
					.get(keyThatTriggeredEvent);

			if (listenersForThisKey != null) {
				for (ParamerUpdateListener listenerForThisKey : listenersForThisKey) {
					logger.debug("Triggering listener {}", listenerForThisKey
							.getClass().getSimpleName());
					listenerForThisKey.onConfigChange();
				}
			}
		}
	}

	public List<Parameter> getAll() {
		@SuppressWarnings("unchecked")
		Iterator<String> keys = config.getKeys();

		List<Parameter> paramz = new ArrayList<Parameter>();

		if (keys != null) {
			while (keys.hasNext()) {
				String key = keys.next();
				paramz.add(new Parameter(key, config.getString(key)));
			}
		}
		return paramz;
	}

}
