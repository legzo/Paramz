package com.orange.ccmd.paramz.web;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.ccmd.paramz.Parameter;
import com.orange.ccmd.paramz.Paramz;

public abstract class ParamzListPage extends WebPage {

	private static final Logger logger = LoggerFactory.getLogger(ParamzListPage.class);
	private final Form<List<Parameter>> form;

	public ParamzListPage() {
		setStatelessHint(true);

		form = new StatelessForm<List<Parameter>>("paramsForm");

		form.add(getListView());
		form.add(new AjaxButton("submitButton") {

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				logger.trace("Form submitted");
				target.add(form);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				logger.error("Error submiting form");
			}
		});
		form.add(new AjaxButton("persistButton") {

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				logger.trace("Save to file requested");
				getParamzService().saveToFile();

				target.add(form);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				logger.error("Error saving config");
			}
		});
		add(form);
	}

	private ListView<Parameter> getListView() {
		final ListView<Parameter> listView = new ListView<Parameter>("paramsList", new ParamzModel(
				getConfigProviderId())) {
			@Override
			protected void populateItem(final ListItem<Parameter> item) {
				final TextField<String> valueTextField = new TextField<String>("value",
						new LoadableDetachableModel<String>() {
							@Override
							protected String load() {
								return item.getModelObject().getValue();
							}

							@Override
							public void setObject(final String newValue) {
								getParamzService().setParam(item.getModelObject().getName(), newValue);
							}

						});
				valueTextField.setOutputMarkupId(true);
				final Model<String> dropdownModel = new Model<String>();
				final DropDownChoice<String> previousValuesChoice = new DropDownChoice<String>("previousValues",
						dropdownModel, new PropertyModel<List<String>>(item.getModel(), "previousValues"));
				previousValuesChoice.setOutputMarkupId(true);
				previousValuesChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
					@Override
					protected void onUpdate(final AjaxRequestTarget target) {
						valueTextField.getModel().setObject(dropdownModel.getObject());
						target.add(form);
					}
				});

				final String dirtyFlag = item.getModelObject().isDirty() ? "dirty" : "";

				final Label label = new Label("name", new Model<String>(item.getModelObject().getName()));
				label.add(new AttributeModifier("class", dirtyFlag));

				item.add(label);
				item.add(valueTextField);
				item.add(previousValuesChoice);
			}
		};

		listView.setOutputMarkupId(true);
		return listView;
	}

	private Paramz getParamzService() {
		return ParamzApplication.getConfigProvider(getConfigProviderId());
	}

	@Override
	public void renderHead(final IHeaderResponse response) {
		response.renderCSSReference(new PackageResourceReference(ParamzListPage.class,
				"bootstrap/css/bootstrap.min.css"));
		response.renderCSSReference(new PackageResourceReference(ParamzListPage.class, "base.css"));
	}

	protected String getConfigProviderId() {
		return getClass().getSimpleName().toLowerCase();
	}

}
