package com.orange.ccmd.paramz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
			final String configurationFilePath, final String alias) {
		try {
			final AbstractHierarchicalFileConfiguration config = new PrettyXMLConfiguration(
					configurationFilePath);
			final FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
			reloadingStrategy.setRefreshDelay(refreshDelay);
			config.setReloadingStrategy(reloadingStrategy);

			globalConfig.addConfiguration(config, alias);

			initCache();

			return config;
		} catch (final ConfigurationException e) {
			logger.debug("Could not find configuration file @ {}",
					configurationFilePath);
			return null;
		}
	}

	public void initCache() {
		final Iterator<String> keys = globalConfig.getKeys();

		if (keys != null) {
			while (keys.hasNext()) {
				final String key = keys.next();
				Parameter parameter = new Parameter(key, globalConfig.getString(key));
				parameter.setHierarchicalView(getHierarchicalView(key));
				parameters.put(key, parameter);
			}
		}
	}

	public List<Parameter> getAll() {
		final ArrayList<Parameter> values = new ArrayList<Parameter>(parameters.values());
		Collections.sort(values);
		return values;
	}

	public void setParamNodeLevel(final String key, final String value)
			throws ConfigurationException {
		setParam(key, value, nodeLevelConfig, nodeLevelConfigurationFile);
	}

	public void setParamClusterLevel(final String key, final String value)
			throws ConfigurationException {
		setParam(key, value, clusterLevelConfig, clusterLevelConfigurationFile);
	}

	public void setParam(final String key, final String value,
			AbstractHierarchicalFileConfiguration configToSaveTo,
			String configurationFile) throws ConfigurationException {
		if (value != null) {
			logger.debug("Setting param {}, value={}", key, value);

			if (configToSaveTo != null) {
				configToSaveTo.setProperty(key, value);
				saveToFile(configToSaveTo, configurationFile);
			} else {
				globalConfig.setProperty(key, value);
			}
			Parameter parameter = parameters.get(key);

			Map<String, String> hierarchicalView = getHierarchicalView(key);

			parameter.setValue(globalConfig.getString(key));
			parameter.setHierarchicalView(hierarchicalView);
		}
	}

	private Map<String, String> getHierarchicalView(final String key) {
		Map<String, String> hierarchicalView = new LinkedHashMap<String, String>();
		for (String configName : globalConfig.getConfigurationNameList()) {
			hierarchicalView.put(configName,
					globalConfig.getConfiguration(configName)
							.getString(key));
		}
		return hierarchicalView;
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
		nodeLevelConfig = addConfigurationSource(nodeLevelConfigurationFile,
				"node");
		clusterLevelConfig = addConfigurationSource(
				clusterLevelConfigurationFile, "cluster");
		defaultConfig = addConfigurationSource(defaultConfigurationFile,
				"default");

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
