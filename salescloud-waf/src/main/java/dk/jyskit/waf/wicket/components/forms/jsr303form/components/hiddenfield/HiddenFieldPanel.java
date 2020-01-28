package dk.jyskit.waf.wicket.components.forms.jsr303form.components.hiddenfield;

import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class HiddenFieldPanel extends Panel {
	public HiddenFieldPanel(ComponentContainerPanel container, final String fieldName) {
		super("panel");
		HiddenField hiddenField = new HiddenField("hidden", new PropertyModel(container.getBeanModel(), fieldName)) {
			@Override
			public String getInputName() {
				return fieldName;  // To avoid a random name generated by Wicket
			}
		};
		hiddenField.setOutputMarkupId(true);
		hiddenField.setMarkupId(fieldName);
		add(hiddenField);
	}
}
