package org.elitefactory.paramz.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameter {

	private String name;

	private String value;

	private final static Logger logger = LoggerFactory
			.getLogger(Parameter.class);

	public Parameter() {
		logger.debug("Instantiated parameter");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
