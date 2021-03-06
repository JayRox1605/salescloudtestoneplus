package dk.jyskit.salescloud.application.pages.partner;

import java.util.HashMap;

import org.apache.wicket.markup.html.form.FormComponent;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class ProductNameAndPriceRowPanel extends ComponentWithLabelAndValidationPanel<FormComponent<Object>> {

	public ProductNameAndPriceRowPanel(ComponentContainerPanel container, final String fieldName) {
		super(container, fieldName);
		init(new ProductNameAndPriceRowEditor("editor", propertyModel) {
			@Override
			public String getInputName() {
				return fieldName;  // To avoid a random name generated by Wicket
			}
		}, new HashMap<String, String>());
	}

}
