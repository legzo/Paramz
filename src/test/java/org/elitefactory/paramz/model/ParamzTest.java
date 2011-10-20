package org.elitefactory.paramz.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class ParamzTest {

	private static final String KEY_PASSWORD = "awesome.service.password";
	private static final String KEY_LOGIN = "awesome.service.login";
	private static final String KEY_URL = "awesome.service.url";
	private static final String OVERRIDE_PROPERTIES_PATH = "test-override.properties";
	private static final String BASE_PROPERTIES_PATH = "test-base.properties";
	private static final String BAD_OVERRIDE_PROPERTIES_PATH = "not-existing.properties";

	@Test
	public void shouldInitSuccessfully() {
		Paramz paramz = new Paramz();

		List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		Assert.assertEquals("http://google.com/API/zoupete",
				paramz.getParam(KEY_URL));
		Assert.assertEquals("juanita", paramz.getParam(KEY_LOGIN));
		Assert.assertEquals("banana", paramz.getParam(KEY_PASSWORD));
	}

	@Test
	public void shouldOverride() {
		Paramz paramz = new Paramz();

		List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(OVERRIDE_PROPERTIES_PATH);
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		Assert.assertEquals("http://google.com/API/zoupete",
				paramz.getParam(KEY_URL));
		Assert.assertEquals("steven", paramz.getParam(KEY_LOGIN));
		Assert.assertEquals("banana", paramz.getParam(KEY_PASSWORD));
	}

	@Test
	public void shouldNotOverrideFileNotFound() {
		Paramz paramz = new Paramz();

		List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(BAD_OVERRIDE_PROPERTIES_PATH);
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		Assert.assertEquals("http://google.com/API/zoupete",
				paramz.getParam(KEY_URL));
		Assert.assertEquals("juanita", paramz.getParam(KEY_LOGIN));
		Assert.assertEquals("banana", paramz.getParam(KEY_PASSWORD));
	}

}
