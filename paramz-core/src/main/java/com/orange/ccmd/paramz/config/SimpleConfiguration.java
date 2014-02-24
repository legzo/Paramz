package com.orange.ccmd.paramz.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

public abstract class SimpleConfiguration extends
		AbstractHierarchicalFileConfiguration {

	protected boolean xmlCompatibility = true;

	public SimpleConfiguration() {
		super();
	}

	public SimpleConfiguration(URL url) throws ConfigurationException {
		super(url);
	}

	public SimpleConfiguration(File file) throws ConfigurationException {
		super(file);
	}

	public SimpleConfiguration(String fileName) throws ConfigurationException {
		super(fileName);
	}

	public SimpleConfiguration(HierarchicalConfiguration c) {
		super(c);
	}

	public void setXMLCompatibility(boolean mode) {
		this.xmlCompatibility = mode;
	}

	public boolean isXMLCompatibility() {
		return this.xmlCompatibility;
	}

	@SuppressWarnings("unchecked")
	protected void loadHierarchy(ConfigurationNode parentNode, Object obj) {
		String parentName = parentNode.getName();
		if (obj instanceof Map<?, ?>) {
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) obj)
					.entrySet()) {
				Node childNode = new Node(entry.getKey());

				if (this.isXMLCompatibility() && parentName != null
						&& parentName.endsWith("s")) { // if parent node is look
														// like "tableS",
														// "userS" or "groupS"
				// this is done to have
				// "users.user[@name='smith'] instead of "users.smith"
					childNode.setName(parentName.substring(0,
							parentName.length() - 1));
					childNode.addAttribute(new Node("name", entry.getKey()));
				}

				childNode.setReference(entry);
				loadHierarchy(childNode, entry.getValue());
				parentNode.addChild(childNode);
			}
		} else if (obj instanceof Collection) {
			for (Object child : (Collection) obj) {
				Node childNode = new Node("item");
				childNode.setReference(child);
				loadHierarchy(childNode, child);
				parentNode.addChild(childNode);
			}
		}

		parentNode.setValue(obj);
	}

	protected Object saveHierarchy(ConfigurationNode parentNode) {
		if (parentNode.getChildrenCount() == 0) {
			return parentNode.getValue();
		}

		if (parentNode.getChildrenCount("item") == parentNode
				.getChildrenCount()) { // This is List object
			List<Object> list = new ArrayList<Object>();

			for (ConfigurationNode childNode : parentNode.getChildren()) {
				list.add(saveHierarchy(childNode));
			}

			return list;
		} else { // Map object
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (ConfigurationNode childNode : parentNode.getChildren()) {
				String nodeName = childNode.getName();

				if (this.isXMLCompatibility()
						&& childNode.getAttributes("name").size() > 0) {
					nodeName = String.valueOf(childNode.getAttributes("name")
							.get(0).getValue());
}

				map.put(nodeName, saveHierarchy(childNode));
			}

			return map;
		}
	}
}