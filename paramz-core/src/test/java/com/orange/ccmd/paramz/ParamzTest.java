package com.orange.ccmd.paramz;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParamzTest {

	private static final String KEY_PASSWORD = "awesome.service.password";
	private static final String KEY_LOGIN = "awesome.service.login";
	private static final String KEY_URL = "awesome.service.url";

	private static final String OVERRIDE_PROPERTIES_PATH = "test-override.xml";
	private static final String BASE_PROPERTIES_PATH = "test-base.xml";
	private static final String BAD_OVERRIDE_PROPERTIES_PATH = "not-existing.xml";

	@Test
	public void shouldInitSuccessfully() {
		final Paramz paramz = new Paramz();
		paramz.setDefaultConfigurationFile(BASE_PROPERTIES_PATH);

		assertEquals("http://google.com/API/zoupete", paramz.getConfig().getString(KEY_URL));
		assertEquals("juanita", paramz.getConfig().getString(KEY_LOGIN));
		assertEquals("banana", paramz.getConfig().getString(KEY_PASSWORD));
	}

	@Test
	public void shouldOverride() {
		final Paramz paramz = new Paramz();

		paramz.setNodeLevelConfigurationFile(OVERRIDE_PROPERTIES_PATH);
		paramz.setDefaultConfigurationFile(BASE_PROPERTIES_PATH);

		assertEquals("http://google.com/API/zoupete", paramz.getConfig().getString(KEY_URL));
		assertEquals("steven", paramz.getConfig().getString(KEY_LOGIN));
		assertEquals("banana", paramz.getConfig().getString(KEY_PASSWORD));
	}

	@Test
	public void shouldNotOverrideFileNotFound() {
		final Paramz paramz = new Paramz();

		paramz.setDefaultConfigurationFile(BASE_PROPERTIES_PATH);
		paramz.setNodeLevelConfigurationFile(BAD_OVERRIDE_PROPERTIES_PATH);

		assertEquals("http://google.com/API/zoupete", paramz.getConfig().getString(KEY_URL));
		assertEquals("juanita", paramz.getConfig().getString(KEY_LOGIN));
		assertEquals("banana", paramz.getConfig().getString(KEY_PASSWORD));
	}


}
