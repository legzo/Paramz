package org.elitefactory.paramz.web;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.elitefactory.paramz.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamzListPage extends WebPage {

	private static final Logger logger = LoggerFactory
			.getLogger(ParamzListPage.class);
	Form<List<Parameter>> form;

	public ParamzListPage() {

		form = new Form<List<Parameter>>("paramsForm");

		form.add(getListView());
		form.add(new AjaxButton("submitButton") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				logger.trace("Form submitted");
				target.add(form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				logger.error("Error submiting form");
			}
		});
		add(form);
	}

	private ListView<Parameter> getListView() {
		ListView<Parameter> listView = new ListView<Parameter>("paramsList",
				new ParamzModel()) {
			@Override
			protected void populateItem(final ListItem<Parameter> item) {
				final TextField<String> valueTextField = new TextField<String>(
						"value", new LoadableDetachableModel<String>() {
							@Override
							protected String load() {
								return item.getModelObject().getValue();
							}

							@Override
							public void setObject(String newValue) {
								ParamzApplication.getParamzService().setParam(
										item.getModelObject().getName(),
										newValue);
							}

						});
				valueTextField.setOutputMarkupId(true);
				final Model<String> dropdownModel = new Model<String>();
				final DropDownChoice<String> previousValuesChoice = new DropDownChoice<String>(
						"previousValues", dropdownModel,
						new PropertyModel<List<String>>(item.getModel(),
								"previousValues"));
				previousValuesChoice.setOutputMarkupId(true);
				previousValuesChoice.add(new AjaxFormComponentUpdatingBehavior(
						"onChange") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						valueTextField.getModel().setObject(
								dropdownModel.getObject());
						target.add(form);
					}
				});

				item.add(new Label("name", new Model<String>(item
						.getModelObject().getName())));
				item.add(valueTextField);
				item.add(previousValuesChoice);
			}

		};

		listView.setOutputMarkupId(true);
		return listView;
	}

	@Override
	/**
	 * TODO Extract to utility method... 
	 */
	public void renderHead(final IHeaderResponse response) {
		super.renderHead(response);

		// adding specific CSS
		response.renderCSSReference(new PackageResourceReference(getClass(),
				"base.css"));

		// adding bootstrap LESS file
		PackageResourceReference reference = new PackageResourceReference(
				getClass(), "bootstrap/bootstrap.less");

		IRequestHandler handler = new ResourceReferenceRequestHandler(
				reference, null);
		CharSequence urlChars = RequestCycle.get().urlFor(handler);
		String url = urlChars.toString();

		if (Strings.isEmpty(url)) {
			throw new IllegalArgumentException("url cannot be empty or null");
		}
		String urlWoSessionId = Strings.stripJSessionId(url);
		List<String> token = Arrays.asList("css", urlWoSessionId, null);
		if (response.wasRendered(token) == false) {
			getResponse().write(
					"<link rel=\"stylesheet/less\" type=\"text/css\" href=\"");
			getResponse().write(urlWoSessionId);
			getResponse().write("\"");
			getResponse().write(" />");
			getResponse().write("\n");
			response.markRendered(token);
		}

	}
}
