package com.orange.ccmd.paramz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

	@Test
	public void shouldKeepHistoryOfPreviousValues() {
		final Paramz paramz = new Paramz();
		paramz.setDefaultConfigurationFile(BASE_PROPERTIES_PATH);

		assertEquals(0, paramz.getParam(KEY_URL).getPreviousValues().size());

		paramz.setParamNodeLevel(KEY_URL, "value1");
		paramz.setParamNodeLevel(KEY_URL, "value2");
		paramz.setParamNodeLevel(KEY_URL, "value1");
		paramz.setParamNodeLevel(KEY_URL, "value2");

		assertEquals(3, paramz.getParam(KEY_URL).getPreviousValues().size());
	}

}
