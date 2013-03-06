package com.orange.ccmd.paramz.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.orange.ccmd.paramz.model.Paramz;

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
		final Paramz paramz = new Paramz(BASE_PROPERTIES_PATH);

		assertEquals("http://google.com/API/zoupete", paramz.getString(KEY_URL));
		assertEquals("juanita", paramz.getString(KEY_LOGIN));
		assertEquals("banana", paramz.getString(KEY_PASSWORD));
	}

	@Test
	public void shouldOverride() {
		final Paramz paramz = new Paramz(OVERRIDE_PROPERTIES_PATH, BASE_PROPERTIES_PATH);

		assertEquals("http://google.com/API/zoupete", paramz.getString(KEY_URL));
		assertEquals("steven", paramz.getString(KEY_LOGIN));
		assertEquals("banana", paramz.getString(KEY_PASSWORD));
	}

	@Test
	public void shouldNotOverrideFileNotFound() {
		final Paramz paramz = new Paramz();

		final List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(BAD_OVERRIDE_PROPERTIES_PATH);
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		assertEquals("http://google.com/API/zoupete", paramz.getString(KEY_URL));
		assertEquals("juanita", paramz.getString(KEY_LOGIN));
		assertEquals("banana", paramz.getString(KEY_PASSWORD));
	}

	@Test
	public void shouldKeepHistoryOfPreviousValues() {
		final Paramz paramz = new Paramz(BASE_PROPERTIES_PATH);

		assertEquals(0, paramz.getParam(KEY_URL).getPreviousValues().size());

		paramz.setParam(KEY_URL, "value1");
		paramz.setParam(KEY_URL, "value2");
		paramz.setParam(KEY_URL, "value1");
		paramz.setParam(KEY_URL, "value2");

		assertEquals(3, paramz.getParam(KEY_URL).getPreviousValues().size());
	}

}
