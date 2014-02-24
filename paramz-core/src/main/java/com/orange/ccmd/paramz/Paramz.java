package com.orange.ccmd.paramz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.ccmd.paramz.config.PrettyXMLConfiguration;

public class Paramz implements ConfigurationListener {

	private static final Logger logger = LoggerFactory.getLogger(Paramz.class);

	private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();

	private int refreshDelay = 5000;

	private final CombinedConfiguration globalConfig = new CombinedConfiguration();

	private AbstractHierarchicalFileConfiguration defaultConfig;
	private String defaultConfigurationFile;

	private AbstractHierarchicalFileConfiguration nodeLevelConfig;
	private String nodeLevelConfigurationFile;

	private AbstractHierarchicalFileConfiguration clusterLevelConfig;
	private String clusterLevelConfigurationFile;

	public Paramz() {
		logger.debug("Initializing Paramz");
		globalConfig.setNodeCombiner(new OverrideCombiner());
		globalConfig.setForceReloadCheck(true);
		globalConfig.addConfigurationListener(this);
	}

	public AbstractConfiguration getConfig() {
		return globalConfig;
	}

	/**
	 * Add a particular file to the list of available configuration sources
	 * 
	 * @param configurationFilePath
	 * @return
	 */
	public AbstractHierarchicalFileConfiguration addConfigurationSource(
			final String configurationFilePath) {
		try {
			final AbstractHierarchicalFileConfiguration config = new PrettyXMLConfiguration(
					configurationFilePath);
			final FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
			reloadingStrategy.setRefreshDelay(refreshDelay);
			config.setReloadingStrategy(reloadingStrategy);

			globalConfig.addConfiguration(config);

			initParamzCache();

			return config;
		} catch (final ConfigurationException e) {
			logger.debug("Could not find configuration file @ {}",
					configurationFilePath);
			return null;
		}
	}

	public void initParamzCache() {
		final Iterator<String> keys = globalConfig.getKeys();

		if (keys != null) {
			while (keys.hasNext()) {
				final String key = keys.next();
				parameters.put(key, new Parameter(key, globalConfig.getString(key)));
			}
		}
	}

	public List<Parameter> getAll() {
		final ArrayList<Parameter> values = new ArrayList<Parameter>(parameters.values());
		Collections.sort(values);
		return values;
	}

	public void setParamNodeLevel(final String key, final String value) {
		setParam(key, value, nodeLevelConfig);
		parameters.get(key).setValueNodeLevel(value);
	}
	public void setParamClusterLevel(final String key, final String value) {
		setParam(key, value, clusterLevelConfig);
		parameters.get(key).setValueClusterLevel(value);
	}


	public void setParam(final String key, final String value,
			AbstractHierarchicalFileConfiguration configToSaveTo) {
		if (value != null && !value.equals(getConfig().getString(key))) {
			logger.debug("Setting param {}, value={}", key, value);

			if (configToSaveTo != null) {
				configToSaveTo.setProperty(key, value);
			} else {
				globalConfig.setProperty(key, value);
			}
		}
	}

	public void persistNodeLevel() throws ConfigurationException {
		saveToFile(nodeLevelConfig, nodeLevelConfigurationFile);
		undirtyParamsNodeLevel();
	}

	public void persistClusterLevel() throws ConfigurationException {
		saveToFile(clusterLevelConfig, clusterLevelConfigurationFile);
		undirtyParamsClusterLevel();
	}

	private void saveToFile(AbstractHierarchicalFileConfiguration configToDump,
			String fileToDumpItTo) throws ConfigurationException {
		if (configToDump != null) {
			try {
				logger.debug("Saving config to file : {}", fileToDumpItTo);
				configToDump.save(fileToDumpItTo);
			} catch (final ConfigurationException e) {
				logger.error("Could not save combined configuration...", e);
				throw e;
			}
		} else {
			String message = "Cannot save config, it does not exist";
			logger.error(message);
			throw new ConfigurationException(message);
		}
	}

	private void undirtyParamsNodeLevel() {
		for (final Parameter param : parameters.values()) {
			param.setDirtyNodeLevel(false);
		}
	}

	private void undirtyParamsClusterLevel() {
		for (final Parameter param : parameters.values()) {
			param.setDirtyClusterLevel(false);
		}
	}

	@Override
	public void configurationChanged(final ConfigurationEvent event) {
		logger.trace("config changed, updating paramz");
	}

	public void setRefreshDelay(final int refreshDelay) {
		this.refreshDelay = refreshDelay;
	}

	Parameter getParam(final String key) {
		return parameters.get(key);
	}

	public void setDefaultConfigurationFile(
			final String defaultConfigurationFile) {
		this.defaultConfigurationFile = defaultConfigurationFile;
	}

	public void setNodeLevelConfigurationFile(
			final String nodeLevelConfigurationFile) {
		this.nodeLevelConfigurationFile = nodeLevelConfigurationFile;
	}

	public void setClusterLevelConfigurationFile(
			final String clusterLevelConfigurationFile) {
		this.clusterLevelConfigurationFile = clusterLevelConfigurationFile;
	}

	@PostConstruct
	public void postConstruct() {
		nodeLevelConfig = addConfigurationSource(nodeLevelConfigurationFile);
		clusterLevelConfig = addConfigurationSource(clusterLevelConfigurationFile);
		defaultConfig = addConfigurationSource(defaultConfigurationFile);

		logConfig("     defaultConfig", defaultConfigurationFile, defaultConfig);
		logConfig("clusterLevelConfig", clusterLevelConfigurationFile,
				clusterLevelConfig);
		logConfig("   nodeLevelConfig", nodeLevelConfigurationFile,
				nodeLevelConfig);
	}

	private void logConfig(String configName, String configPath,
			AbstractHierarchicalFileConfiguration config) {
		logger.info("{} -> {}{}", new Object[] { configName,
				config != null ? "loaded @ " : "not loaded @ ", configPath });
	}
}
