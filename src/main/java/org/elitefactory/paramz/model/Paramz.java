package org.elitefactory.paramz.model;

import java.util.List;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paramz {

	private static final Logger logger = LoggerFactory.getLogger(Paramz.class);

	private CombinedConfiguration config = new CombinedConfiguration();

	public Paramz() {
		logger.debug("Initializing Paramz");
		config.setNodeCombiner(new OverrideCombiner());
	}

	public String getParam(String key) {
		return config.getString(key);
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

}
