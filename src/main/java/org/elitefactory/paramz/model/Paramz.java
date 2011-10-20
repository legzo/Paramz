package org.elitefactory.paramz.model;

import java.util.HashMap;
import java.util.HashSet;
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

	public String getParam(String key) {
		return config.getString(key);
	}

	public void setParam(String key, String value) {
		config.setProperty(key, value);
	}

	public void addListener(String keyToListenTo, ParamerUpdateListener listener) {
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

	public void setConfigurationSources(List<String> configurationFilePaths) {
		if (configurationFilePaths != null) {
			for (String configurationFilePath : configurationFilePaths) {
				try {
					logger.debug("Adding new config file: {}",
							configurationFilePath);
					config.addConfiguration(new PropertiesConfiguration(
							configurationFilePath));
				} catch (ConfigurationException e) {
					logger.error(
							"Something went wrong while initializing configuration sources",
							e);
				}
			}
		}
	}

	public void configurationChanged(ConfigurationEvent event) {
		String keyThatTriggeredEvent = event.getPropertyName();
		if (keyThatTriggeredEvent != null
				&& event.getType() == AbstractConfiguration.EVENT_SET_PROPERTY
				&& event.isBeforeUpdate()) {
			logger.info("Configuration changed because of property {}",
					keyThatTriggeredEvent);

			Set<ParamerUpdateListener> listenersForThisKey = listeners
					.get(keyThatTriggeredEvent);

			if (listenersForThisKey != null) {
				for (ParamerUpdateListener listenerForThisKey : listenersForThisKey) {
					listenerForThisKey.onConfigChange();
				}
			}
		}
	}
}
