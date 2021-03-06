package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.markup.html.form.FormComponent;

import java.util.HashMap;

public class SelectProductCountEditorPanel extends ComponentWithLabelAndValidationPanel<FormComponent<Object>> implements IEventSink {

	public SelectProductCountEditorPanel(ComponentContainerPanel container, final String fieldName, boolean pullRight) {
		super(container, fieldName);
		init(new SelectProductCountEditor("editor", propertyModel, pullRight) {
			@Override
			public String getInputName() {
				return fieldName;  // To avoid a random name generated by Wicket
			}
		}, new HashMap<String, String>());
	}

	public SelectProductCountEditorPanel(ComponentContainerPanel container, final String fieldName) {
		this(container, fieldName, true);
	}
}
