package com.orange.ccmd.paramz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameter implements Serializable, Comparable<Parameter> {

	private String name;
	private String value;
	private List<String> previousValues = new ArrayList<String>();
	private boolean isDirtyNodeLevel;
	private boolean isDirtyClusterLevel;

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
			previousValues.add(previousValue);
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

	public void setValueNodeLevel(final String newValue) {
		if (!previousValues.contains(value)) {
			previousValues.add(value);
		}
		// check if value has changed
		if (!value.equals(newValue)) {
			isDirtyNodeLevel = true;
		}
		// set new value
		value = newValue;
	}

	public void setValueClusterLevel(final String newValue) {
		if (!previousValues.contains(value)) {
			previousValues.add(value);
		}

		if (!previousValues.contains(newValue)) {
			previousValues.add(newValue);
		}

		// check if value has changed
		if (!value.equals(newValue)) {
			isDirtyClusterLevel = true;
		}
		// set new value
		value = newValue;
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


	@Override
	public int compareTo(final Parameter o) {
		return getName().compareTo(o.getName());
	}

	public boolean isDirtyNodeLevel() {
		return isDirtyNodeLevel;
	}

	public void setDirtyNodeLevel(boolean isDirtyNodeLevel) {
		this.isDirtyNodeLevel = isDirtyNodeLevel;
	}

	public boolean isDirtyClusterLevel() {
		return isDirtyClusterLevel;
	}

	public void setDirtyClusterLevel(boolean isDirtyClusterLevel) {
		this.isDirtyClusterLevel = isDirtyClusterLevel;
	}

}
