package org.elitefactory.paramz.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
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

	private List<Component> componentsToRedraw = new ArrayList<Component>();

	public ParamzListPage() {

		final Form<List<Parameter>> form = new Form<List<Parameter>>(
				"paramsForm");
		final PropertyListView<Parameter> listView = getListView();

		form.add(listView);
		form.add(new AjaxButton("submitButton") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				logger.trace("Form submitted");

				List<Parameter> updatedParameters = listView.getModelObject();
				for (Parameter updatedParameter : updatedParameters) {
					ParamzApplication.getParamzService().setParam(
							updatedParameter.getName(),
							updatedParameter.getValue());
				}

				for (Component component : componentsToRedraw) {
					target.add(component);
				}

			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				logger.error("Error submiting form");
			}
		});
		add(form);
	}

	private PropertyListView<Parameter> getListView() {
		return new PropertyListView<Parameter>("paramsList", new ParamzModel()) {
			@Override
			protected void populateItem(ListItem<Parameter> item) {
				item.add(new Label("name"));
				item.add(new TextField<String>("value"));

				DropDownChoice<String> previousValuesChoice = new DropDownChoice<String>(
						"previousValues", new Model<String>(),
						new PropertyModel<List<String>>(item.getModel(),
								"previousValues"));
				previousValuesChoice.setOutputMarkupId(true);

				item.add(previousValuesChoice);
				componentsToRedraw.add(previousValuesChoice);
			}
		};
	}

	@Override
	/**
	 * TODO Extract to utility method... 
	 */
	public void renderHead(final IHeaderResponse response) {
		super.renderHead(response);
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
