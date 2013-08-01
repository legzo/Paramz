package com.orange.ccmd.paramz;

import org.springframework.beans.factory.annotation.Autowired;

public class M {

	private static M m = new M();

	@Autowired
	private Paramz messages;

	public static String get(final String key) {
		return m.messages.getConfig().getString(key);
	}

	public static M getInstance() {
		return m;
	}

	public void setParamz(final Paramz paramz) {
		messages = paramz;
	}

}
