package org.elitefactory.paramz.web;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.elitefactory.paramz.model.Parameter;
import org.elitefactory.paramz.model.Paramz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamzListPage extends WebPage {

	private static final Logger logger = LoggerFactory
			.getLogger(ParamzListPage.class);

	@SpringBean
	private Paramz paramz;

	public ParamzListPage() {

		Form<List<Parameter>> form = new Form<List<Parameter>>("paramsForm");
		form.add(getListView(paramz.getAll()));
		form.add(new AjaxButton("submitButton") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				logger.debug("Form submitted");
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				logger.error("Error submiting form");
			}
		});
		add(form);
	}

	private PropertyListView<Parameter> getListView(List<Parameter> list) {
		return new PropertyListView<Parameter>("paramsList", list) {
			@Override
			protected void populateItem(ListItem<Parameter> item) {
				item.add(new Label("name"));
				item.add(new TextField<String>("value"));
			}
		};
	}

	@Override
	public void renderHead(final IHeaderResponse response) {
		super.renderHead(response);
		response.renderCSSReference(new PackageResourceReference(getClass(),
				"base.css"));
	}
}
