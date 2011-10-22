package org.elitefactory.paramz.web;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.elitefactory.paramz.model.Parameter;
import org.elitefactory.paramz.model.Paramz;

public class ParamzListPage extends WebPage {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private Paramz paramz;

	public ParamzListPage() {

		ParamzModel paramzModel = new ParamzModel(paramz);

		PropertyListView<Parameter> paramsList = new PropertyListView<Parameter>(
				"paramsList", paramzModel) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Parameter> item) {
				item.add(new Label("name"));
				item.add(new TextField<String>("value"));
			}
		};

		this.add(paramsList);
	}

	@Override
	public void renderHead(final IHeaderResponse response) {
		super.renderHead(response);

		response.renderCSSReference(new PackageResourceReference(getClass(),
				  "base.css"));
	}
}
