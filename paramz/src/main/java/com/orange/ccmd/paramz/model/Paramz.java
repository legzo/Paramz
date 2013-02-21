package com.orange.ccmd.paramz.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.wicket.util.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paramz {

	private static final Logger logger = LoggerFactory.getLogger(Paramz.class);
	private static final String BACKUP_EXTENSION = ".bak";

	private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();

	private final CombinedConfiguration config = new CombinedConfiguration();
	private final CombinedConfiguration backupConfig = new CombinedConfiguration();

	private String localConfigurationFile;

	public Paramz() {
		logger.debug("Initializing Paramz");
		config.setNodeCombiner(new OverrideCombiner());
		backupConfig.setNodeCombiner(new OverrideCombiner());
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
	
	public String[] getStringArray(final String key) {
		return config.getStringArray(key);
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

	/**
	 * Can be used for Spring injection. Please note that the order is important
	 * : the first file will override the following and so on.
	 * 
	 * @param configurationFilePaths
	 *            list of configuration paths (can be absolute or classpath
	 *            relative)
	 */
	public void setConfigurationSources(final List<String> configurationFilePaths) {

		if (configurationFilePaths != null) {
			localConfigurationFile = configurationFilePaths.get(0);
			addBackupConfigurationSource(localConfigurationFile + BACKUP_EXTENSION);

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

	/**
	 * Add a particular file to the list of available configuration sources
	 * 
	 * @param configurationFilePath
	 */
	private void addBackupConfigurationSource(final String configurationFilePath) {
		try {
			backupConfig.addConfiguration(new XMLConfiguration(configurationFilePath));
			logger.info("Added new backup config file: {}", configurationFilePath);
		} catch (final ConfigurationException e) {
			logger.warn("Could not find configuration file @ {}", configurationFilePath);
		}
	}

	public void initCache() {
		final Iterator<String> keys = config.getKeys();

		if (keys != null) {
			while (keys.hasNext()) {
				final String key = keys.next();
				parameters.put(key, new Parameter(key, config.getString(key), backupConfig.getString(key)));
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
			backupFile();
			result.save(localConfigurationFile);

			for (final Parameter param : parameters.values()) {
				param.setDirty(false);
			}

		} catch (final ConfigurationException e) {
			logger.error("Could not save combined configuration...", e);
		}
	}

	private void backupFile() {
		try {
			Files.copy(new File(localConfigurationFile), new File(localConfigurationFile + BACKUP_EXTENSION));
		} catch (IOException e) {
			logger.error("Error while doing configuration backup", e);
		}
	}

}
