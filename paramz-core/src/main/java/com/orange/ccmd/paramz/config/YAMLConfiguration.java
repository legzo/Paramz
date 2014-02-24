package com.orange.ccmd.paramz.config;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YAMLConfiguration extends SimpleConfiguration {
	

	public final static int DEFAULT_IDENT = 4;
	private DumperOptions yamlOptions = new DumperOptions();
	private  Yaml yaml = new Yaml(yamlOptions);

	public YAMLConfiguration() {
		super();
		initialize();
	}

	public YAMLConfiguration(HierarchicalConfiguration c) {
		super(c);
		initialize();
	}

	public YAMLConfiguration(String fileName) throws ConfigurationException {
		super(fileName);
		initialize();
	}

	public YAMLConfiguration(File file) throws ConfigurationException {
		super(file);
		initialize();
	}

	public YAMLConfiguration(URL url) throws ConfigurationException {
		super(url);
		initialize();
	}

	private void initialize() {
		if (yamlOptions == null) {
			yamlOptions = new DumperOptions();
		}
		if (yaml == null) {
			yaml = new Yaml(yamlOptions);
		}
		yamlOptions.setIndent(DEFAULT_IDENT);
		yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	}

	@Override
	public void load(Reader in) throws ConfigurationException {
		try {
			initialize();
			this.loadHierarchy(this.getRootNode(), yaml.load(in));
		} catch (Throwable e) {
			throw new ConfigurationException("Failed to load configuration: "
					+ e.getMessage(), e);
		}
	}

	@Override
	public void save(Writer out) throws ConfigurationException {
		try {
			yaml.dump(this.saveHierarchy(this.getRootNode()), out);
		} catch (Throwable e) {
			throw new ConfigurationException("Failed to save configuration: "
					+ e.getMessage(), e);
		}
	}
}
