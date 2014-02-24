package com.orange.ccmd.paramz.config;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class PrettyXMLConfiguration extends XMLConfiguration {

	public PrettyXMLConfiguration(final AbstractHierarchicalFileConfiguration localConfig) {
		super(localConfig);
	}

	public PrettyXMLConfiguration(final String configurationFilePath) throws ConfigurationException {
		super(configurationFilePath);
	}

	@Override
	protected Transformer createTransformer() throws TransformerException {
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		return transformer;
	}
}
