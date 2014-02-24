package com.orange.ccmd.paramz;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.ccmd.paramz.config.YAMLConfiguration;

public class YAMLConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(YAMLConfig.class);

	private final CombinedConfiguration config = new CombinedConfiguration();

	@Test
	public void should() throws ConfigurationException {
		logger.debug("Initializing");
		config.setNodeCombiner(new OverrideCombiner());
		config.setForceReloadCheck(true);

		final YAMLConfiguration yamlConfig = new YAMLConfiguration(
				"test-base.yaml");
		final YAMLConfiguration overrideYamlConfig = new YAMLConfiguration(
				"test-override.yaml");

		config.addConfiguration(overrideYamlConfig);
		config.addConfiguration(yamlConfig);

		System.out.println(config.getString("awesome.service.password"));

		overrideYamlConfig.setProperty("whazza", "w'o'l{oo}");

		overrideYamlConfig.save("temp.yaml");
	}
}
