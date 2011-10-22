package org.elitefactory.paramz.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.elitefactory.paramz.model.Parameter;
import org.elitefactory.paramz.model.Paramz;

public class ParamzListPage extends WebPage {

	private static final long serialVersionUID = 1L;

	public ParamzListPage() {
		Paramz paramz = new Paramz();
		paramz.addConfigurationSource("props/default.properties");

		List<Parameter> params = paramz.getAll();

		PropertyListView<Parameter> paramsList = new PropertyListView<Parameter>(
				"paramsList", params) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Parameter> item) {
				item.add(new Label("name"));
				item.add(new Label("value"));
			}
		};

		this.add(paramsList);
	}

}
