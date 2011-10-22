package org.elitefactory.paramz.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

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

		assertEquals("http://google.com/API/zoupete", paramz.getParam(KEY_URL));
		assertEquals("juanita", paramz.getParam(KEY_LOGIN));
		assertEquals("banana", paramz.getParam(KEY_PASSWORD));
	}

	@Test
	public void shouldOverride() {
		Paramz paramz = new Paramz();

		List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(OVERRIDE_PROPERTIES_PATH);
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		assertEquals("http://google.com/API/zoupete", paramz.getParam(KEY_URL));
		assertEquals("steven", paramz.getParam(KEY_LOGIN));
		assertEquals("banana", paramz.getParam(KEY_PASSWORD));
	}

	@Test
	public void shouldNotOverrideFileNotFound() {
		Paramz paramz = new Paramz();

		List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(BAD_OVERRIDE_PROPERTIES_PATH);
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		assertEquals("http://google.com/API/zoupete", paramz.getParam(KEY_URL));
		assertEquals("juanita", paramz.getParam(KEY_LOGIN));
		assertEquals("banana", paramz.getParam(KEY_PASSWORD));
	}

	@Test
	public void shouldTriggerListener() {
		Paramz paramz = new Paramz();

		List<String> configurationFilePaths = new ArrayList<String>();
		configurationFilePaths.add(OVERRIDE_PROPERTIES_PATH);
		configurationFilePaths.add(BASE_PROPERTIES_PATH);

		paramz.setConfigurationSources(configurationFilePaths);

		ParamerUpdateListener mockListenerForLogin = mock(ParamerUpdateListener.class);
		ParamerUpdateListener mockListenerForUrl = mock(ParamerUpdateListener.class);

		paramz.addListener(new String[] { KEY_LOGIN, KEY_URL },
				mockListenerForLogin);

		paramz.setParam(KEY_LOGIN, "newLoginValue");
		paramz.setParam(KEY_LOGIN, "newLoginValue");
		paramz.setParam(KEY_LOGIN, "thisIsReallyANewLoginValue");
		paramz.setParam(KEY_PASSWORD, "newPassValue");

		verify(mockListenerForLogin, times(2)).onConfigChange();

		reset(mockListenerForLogin);
		paramz.setParam(KEY_PASSWORD, "value2");
		verify(mockListenerForLogin, never()).onConfigChange();
		verify(mockListenerForUrl, never()).onConfigChange();
	}

}
