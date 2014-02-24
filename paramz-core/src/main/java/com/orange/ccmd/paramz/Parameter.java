package com.orange.ccmd.paramz;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameter implements Serializable, Comparable<Parameter> {

	private String name;
	private String value;
	private Map<String, String> hierarchicalView = new LinkedHashMap<String, String>();

	private final static Logger logger = LoggerFactory.getLogger(Parameter.class);

	public Parameter() {
		logger.debug("Instantiated parameter");
	}

	public Parameter(final String name, final String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String newValue) {
		value = newValue;
	}

	@Override
	public int compareTo(final Parameter o) {
		return getName().compareTo(o.getName());
	}

	public Map<String, String> getHierarchicalView() {
		return hierarchicalView;
	}

	public void setHierarchicalView(Map<String, String> hierarchicalView) {
		this.hierarchicalView = hierarchicalView;
	}

}
