package dk.jyskit.salescloud.application.editors.productcountorall;

import java.util.HashMap;

import org.apache.wicket.markup.html.form.FormComponent;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class ProductCountOrAllEditorPanel extends ComponentWithLabelAndValidationPanel<FormComponent<Object>> {

	public ProductCountOrAllEditorPanel(ComponentContainerPanel container, final String fieldName) {
		super(container, fieldName);
		init(new ProductCountOrAllEditor("editor", propertyModel) {
			@Override
			public String getInputName() {
				return fieldName;  // To avoid a random name generated by Wicket
			}
		}, new HashMap<String, String>());
	}
}
