package com.orange.ccmd.paramz.utils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.AbstractConfiguration;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.orange.ccmd.paramz.C;
import com.orange.ccmd.paramz.M;
import com.orange.ccmd.paramz.Paramz;

public class Mocker {

	private static Paramz conf = Mockito.mock(Paramz.class);
	private static AbstractConfiguration confRepo = Mockito.mock(AbstractConfiguration.class);

	private static Paramz messages = Mockito.mock(Paramz.class);
	private static AbstractConfiguration messagesRepo = Mockito.mock(AbstractConfiguration.class);

	public static AbstractConfiguration mockConf() {
		when(conf.getConfig()).thenReturn(confRepo);
		when(confRepo.getString(anyString())).thenAnswer(theFirstParameter());
		when(confRepo.getBoolean(anyString())).thenReturn(false);

		C.getInstance().setParamz(conf);

		return confRepo;
	}

	public static AbstractConfiguration mockMessages() {
		when(messages.getConfig()).thenReturn(messagesRepo);
		when(messagesRepo.getString(anyString())).thenAnswer(
				theFirstParameter());

		M.getInstance().setParamz(messages);

		return confRepo;
	}


	public static Answer<String> theFirstParameter() {
		return new Answer<String>() {
			@Override
			public String answer(final InvocationOnMock invocation)
					throws Throwable {
				final Object[] args = invocation.getArguments();
				return (String) args[0];
			}
		};
	}

}
