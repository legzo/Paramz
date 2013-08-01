package com.orange.ccmd.paramz;

import org.springframework.beans.factory.annotation.Autowired;

public class C {

	private static C c = new C();

	@Autowired
	private Paramz config;

	public static String get(final String key) {
		return c.config.getConfig().getString(key);
	}

	public static String[] getArray(final String key) {
		return c.config.getConfig().getStringArray(key);
	}

	public static boolean getBoolean(final String key) {
		return c.config.getConfig().getBoolean(key);
	}

	public static long getLong(final String key) {
		return c.config.getConfig().getLong(key);
	}

	public static C getInstance() {
		return c;
	}

	public void setParamz(final Paramz paramz) {
		config = paramz;
	}

}
