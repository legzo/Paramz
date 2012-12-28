package com.orange.ccmd.paramz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameter implements Serializable {

	private String name;
	private String value;
	private List<String> previousValues = new ArrayList<String>();
	private boolean isDirty;

	private final static Logger logger = LoggerFactory.getLogger(Parameter.class);

	public Parameter() {
		logger.debug("Instantiated parameter");
	}

	public Parameter(final String name, final String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Parameter(final String name, final String value, final String previousValue) {
		super();
		this.name = name;
		this.value = value;
		if (previousValue != null) {
			this.previousValues.add(previousValue);
		}
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

	public void setValue(final String value) {
		if (!previousValues.contains(this.value)) {
			previousValues.add(this.value);
		}

		this.value = value;
		isDirty = true;
	}

	public List<String> getPreviousValues() {
		return previousValues;
	}

	public void setPreviousValues(final List<String> previousValues) {
		if (previousValues == null) {
			this.previousValues = new ArrayList<String>();
		} else {
			this.previousValues = previousValues;
		}
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(final boolean isDirty) {
		this.isDirty = isDirty;
	}
}
