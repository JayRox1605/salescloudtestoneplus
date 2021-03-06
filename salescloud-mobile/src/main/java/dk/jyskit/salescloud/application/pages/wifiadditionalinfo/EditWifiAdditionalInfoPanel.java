package dk.jyskit.salescloud.application.pages.wifiadditionalinfo;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.InvoicingTypeEnum;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.WiFiBundleIds;
import dk.jyskit.salescloud.application.pages.partner.PartnerProvisionSpreadsheet;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import dk.jyskit.waf.wicket.components.spreadsheets.SpreadsheetLink;

public class EditWifiAdditionalInfoPanel extends Panel {
	private static final String KEY_ORDER_HANDLING_REMARKS 		= "orderHandlingRemarks";
	private static final String KEY_TECHNICAL_SOLUTION 			= "technicalSolution";
	private static final String KEY_TECHNICAL_CONTACT_EMAIL 	= "technicalContactEmail";
	private static final String KEY_TECHNICAL_CONTACT_PHONE 	= "technicalContactPhone";
	private static final String KEY_TECHNICAL_CONTACT_NAME 		= "technicalContactName";
	private static final String KEY_BUILDING_PLAN 				= "buildingPlan";
	private static final String KEY_NEW_ACCOUNT 				= "newAccount";
	private static final String KEY_INVOICING_TYPE 				= "invoicingType";
	private static final String KEY_INVOICING_INFO 				= "invoicingInfo";
	private static final String KEY_ACCOUNT_NO 					= "accountNo";
	private static final String KEY_ORDER_CONFIRMATION			= "orderConfirmation";
	private static final String KEY_TM_NUMBER					= "tmNumber";
	private static final String KEY_SHOW_PROVISION				= "showProvision";
	private static final String KEY_PROVISION_TOTAL				= "provisionTotal";
	private static final String KEY_PROVISION_SPREADSHEET		= "provisionSpreadsheet";
	
	@Inject
	private PageNavigator navigator;
	@Inject
	private ContractDao contractDao;
	@Inject
	private ContractSaver contractSaver;
	
	private DropDownChoice invoicingTypeField;
	private TextField accountNoField;
	private TextField invoicingInfoField;
	
	private Boolean showProvision = false;
	private Label provisionTotal = null;
	private SpreadsheetLink spreadsheetLink = null;
	
	public EditWifiAdditionalInfoPanel(String id) {
		super(id);
		
		final MobileContract contract = (MobileContract) CoreSession.get().getContract();
		
		final WiFiAdditionalInfo values = new WiFiAdditionalInfo();

		Jsr303Form<WiFiAdditionalInfo> form = new Jsr303Form<>("form", values);
		add(form);
		
		Map<String, String> labelMap = new HashMap<>();
		MapLabelStrategy labelStrategy = new MapLabelStrategy(labelMap, new DefaultLabelStrategy(form.getNameSpace()));
		form.setLabelStrategy(labelStrategy);		
		
		FormGroup<WiFiAdditionalInfo> group = form.createGroup(Model.of("Generelt"));
		
		{
			String key = KEY_TECHNICAL_CONTACT_NAME;
			labelMap.put(key, "Teknisk kontaktperson - Navn");
			values.put(key, contract.getTechnicalContactName());
			form.addTextField(key);
		}
		
		{
			String key = KEY_TECHNICAL_CONTACT_PHONE;
			labelMap.put(key, "Teknisk kontaktperson - Telefon");
			values.put(key, contract.getTechnicalContactPhone());
			form.addTextField(key);
		}
		
		{
			String key = KEY_TECHNICAL_CONTACT_EMAIL;
			labelMap.put(key, "Teknisk kontaktperson - Email");
			values.put(key, contract.getTechnicalContactEmail());
			form.addTextField(key);
		}
		
		{
			String key = KEY_ORDER_HANDLING_REMARKS;
			labelMap.put(key, "Bemærkning til ordrehåndtering");
			values.put(key, contract.getOrderHandlingRemarks());
			form.addTextField(key);
		}
		
		{
			String key = KEY_TECHNICAL_SOLUTION;
			labelMap.put(key, "Løsningsbeskrivelse til tekniker");
			values.put(key, contract.getTechnicalSolution());
			TextArea textArea = form.addTextArea(key);
			textArea.add(StringValidator.lengthBetween(20, 500));
		}

		{
			String key = KEY_BUILDING_PLAN;
			labelMap.put(key, "Bygningstegning foreligger");
			values.put(key, contract.isBuildingPlanAvailable());
			form.addCheckBox(key);
		}

		{
			final String key = KEY_NEW_ACCOUNT;
			labelMap.put(key, "Fakturering - Ny konto");
			values.put(key, contract.isNewAccount());
			form.addCheckBox(key).add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if (values.getAsBoolean(key)) {
						invoicingTypeField.getParent().getParent().setVisible(true);
						invoicingInfoField.getParent().getParent().setVisible(true);
						accountNoField.getParent().getParent().setVisible(false);
					} else {
						invoicingTypeField.getParent().getParent().setVisible(false);
						invoicingInfoField.getParent().getParent().setVisible(false);
						accountNoField.getParent().getParent().setVisible(true);
					}
					target.add(invoicingTypeField.getParent().getParent());
					target.add(invoicingInfoField.getParent().getParent());
					target.add(accountNoField.getParent().getParent());
				}
			});
		}
		
		{
			String key = KEY_INVOICING_TYPE;
			labelMap.put(key, "Fakturering - Faktureringsmetode");
			values.put(key, contract.getInvoicingType());
			invoicingTypeField = form.addDropDownChoice(key, InvoicingTypeEnum.valuesAsList());
//			invoicingTypeField = form.addSelect2Choice(key, InvoicingTypeEnum.getChoiceProvider());
			invoicingTypeField.getParent().getParent().setOutputMarkupId(true);
			invoicingTypeField.getParent().getParent().setOutputMarkupPlaceholderTag(true);
			invoicingTypeField.getParent().getParent().setVisible(contract.isNewAccount());
		}
		
		{
			String key = KEY_INVOICING_INFO;
			labelMap.put(key, "Fakturering - Nødvendig information (Email, etc.)");
			values.put(key, contract.getInvoicingInfo());
			invoicingInfoField = form.addTextField(key);
			invoicingInfoField.getParent().getParent().setOutputMarkupId(true);
			invoicingInfoField.getParent().getParent().setOutputMarkupPlaceholderTag(true);
			invoicingInfoField.getParent().getParent().setVisible(contract.isNewAccount());
		}
		
		{
			String key = KEY_ACCOUNT_NO;
			labelMap.put(key, "Fakturering - Eksisterende konto - Kontonummer");
			values.put(key, contract.getAccountNo());
			accountNoField = form.addTextField(key);
			accountNoField.getParent().getParent().setOutputMarkupId(true);
			accountNoField.getParent().getParent().setOutputMarkupPlaceholderTag(true);
			accountNoField.getParent().getParent().setVisible(!contract.isNewAccount());
		}
		
		{
			if (StringUtils.isEmpty(contract.getOrderConfirmationEmailAdresses())) {
				if (!StringUtils.isEmpty(contract.getCustomer().getEmail())) {
					contract.setOrderConfirmationEmailAdresses(contract.getCustomer().getEmail());
				}
			}
			final String key = KEY_ORDER_CONFIRMATION;
			labelMap.put(key, "Ordrebekræftelse pr. email (kommasepareret liste, max. 3 adresser)");
			values.put(key, contract.getOrderConfirmationEmailAdresses());
			form.addTextField(key);
		}
		
		if ((MobileSession.get().getSalespersonRole() != null) && (MobileSession.get().getSalespersonRole().isPartner_ec())) {
			final String key = KEY_TM_NUMBER;
			labelMap.put(key, "TM Nummer");
			values.put(key, contract.getTmNumber());
			form.addTextField(key);
		}
		
		for (int i = 0; i < contract.getWiFiBundles().size(); i++) {
			WiFiBundleIds location = contract.getWiFiBundles().get(i);
			if (location.isValid()) {
				group = form.createGroup(Model.of("Lokation " + (i+1) + " - " + location.getAddress()));
				
				String key = "location" + i + "lidid";
				labelMap.put(key, "Access (LID ID)");
				values.put(key, location.getLidId());
				if (location.getNewAccess()) {
					group.addReadonly(key);
				} else {
					group.addTextField(key);
				}
				
				key = "location" + i + "name";
				labelMap.put(key, "Kontaktperson på installationsadressen - Navn");
				values.put(key, location.getContactName());
				group.addTextField(key);
				
				key = "location" + i + "phone";
				labelMap.put(key, "Kontaktperson på installationsadressen - Telefon");
				values.put(key, location.getContactPhone());
				group.addTextField(key);
			}
		}
		
		if ((MobileSession.get().getSalespersonRole() != null) && (MobileSession.get().getSalespersonRole().isPartner_ec())) {
			group = form.createGroup(Model.of("Provision"));
			{
				final String key = KEY_SHOW_PROVISION;
				labelMap.put(key, "Vis provision");
				values.put(key, showProvision );
				form.addCheckBox(key, new AjaxEventListener() {
					@Override
					public void onAjaxEvent(AjaxRequestTarget target) {
						provisionTotal.getParent().getParent().setVisible(values.getBoolean(key));
						target.add(provisionTotal.getParent().getParent());
						spreadsheetLink.getParent().getParent().setVisible(values.getBoolean(key));
						target.add(spreadsheetLink.getParent().getParent());
					}
				});
			}
			
			{
			    MutableLong stykProvisions	= new MutableLong();
			    MutableLong satsProvisions	= new MutableLong();
			    MutableLong totalProvisions	= new MutableLong();

			    contract.calculatePartnerProvision(stykProvisions, satsProvisions, totalProvisions);
			    float factor = contract.calculatePartnerProvisionFactor();

				final String key = KEY_PROVISION_TOTAL;
				labelMap.put(key, "Samlet provision");
			    values.put(key, NumberFormat.getNumberInstance(new Locale("DA", "dk")).format(totalProvisions.longValue() * factor));
				provisionTotal = form.addReadonly(key);
				provisionTotal.getParent().getParent().setOutputMarkupId(true);
				provisionTotal.getParent().getParent().setOutputMarkupPlaceholderTag(true);
				provisionTotal.getParent().getParent().setVisible(contract.isShowProvision());
			}
			
			{
				final String key = KEY_PROVISION_SPREADSHEET;
				labelMap.put(key, "Opgørelse over provision");
				spreadsheetLink = form.addSpreadsheetLink(key, "partnerprovision.xls", Model.of("Hent som regneark"), new PartnerProvisionSpreadsheet());
				
				spreadsheetLink.getParent().getParent().setOutputMarkupId(true);
				spreadsheetLink.getParent().getParent().setOutputMarkupPlaceholderTag(true);
				spreadsheetLink.getParent().getParent().setVisible(contract.isShowProvision());
			}
		}
		
//	Ordrebekræftelse sendes pr. email til	Kunde - samme som på stamdata
//		Teknisk kontaktperson
//		Kontaktperson på installationsadressen
//		Andre, mulighed for flere email adresser sepereret af komma eller andet
		
//	TM - nummer	Validering på antal cifre + start 9000
//	Visning af provision	Flueben for visning		
		
		
		labelMap.put("action.prev", "Tilbage");
		labelMap.put("action.next", "Videre");
		
		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(values, navigator.prev(getWebPage()));
			}
		});
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(values, navigator.next(getWebPage()));
			}
		});
	}
	
	private void saveAndNavigate(final WiFiAdditionalInfo values, Class<? extends WebPage> page) {
		// Transfer values to contract
		try {
			final MobileContract contract = (MobileContract) CoreSession.get().getContract();
			
			contract.setTechnicalContactName(values.getString(KEY_TECHNICAL_CONTACT_NAME));
			contract.setTechnicalContactPhone(values.getString(KEY_TECHNICAL_CONTACT_PHONE));
			contract.setTechnicalContactEmail(values.getString(KEY_TECHNICAL_CONTACT_EMAIL));
			contract.setOrderHandlingRemarks(values.getString(KEY_ORDER_HANDLING_REMARKS));
			contract.setTechnicalSolution(values.getString(KEY_TECHNICAL_SOLUTION));
			contract.setBuildingPlanAvailable(values.getBoolean(KEY_BUILDING_PLAN));
			contract.setNewAccount(values.getBoolean(KEY_NEW_ACCOUNT));
			contract.setInvoicingType((InvoicingTypeEnum) values.get(KEY_INVOICING_TYPE));
			contract.setInvoicingInfo(values.getString(KEY_INVOICING_INFO));
			contract.setAccountNo(values.getString(KEY_ACCOUNT_NO));
			contract.setOrderConfirmationEmailAdresses(values.getString(KEY_ORDER_CONFIRMATION));
			contract.setTmNumber(values.getString(KEY_TM_NUMBER));
			
			List<WiFiBundleIds> wifiBundles = contract.getWiFiBundles();
			for (int i = 0; i < wifiBundles.size(); i++) {
				WiFiBundleIds bundle = wifiBundles.get(i);
				bundle.setLidId(values.getString("location" + i + "lidid"));
				bundle.setContactName(values.getString("location" + i + "name"));
				bundle.setContactPhone(values.getString("location" + i + "phone"));
			}
			contract.setWiFiBundles(wifiBundles);
			
//			contractDao.save(contract);
			contractSaver.save(contract);
			setResponsePage(page);
		} catch (Exception e) {
			System.out.println("Shit happens");
		}
	}
}
