package org.elitefactory.paramz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameter implements Serializable {

	private String name;

	private List<String> previousValues = new ArrayList<String>();

	private String value;

	private final static Logger logger = LoggerFactory
			.getLogger(Parameter.class);

	public Parameter() {
		logger.debug("Instantiated parameter");
	}

	public Parameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
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
		if (!previousValues.contains(this.value)) {
			previousValues.add(this.value);
		}

		this.value = value;
	}

	public List<String> getPreviousValues() {
		return previousValues;
	}

	public void setPreviousValues(List<String> previousValues) {
		this.previousValues = previousValues;
	}
}
