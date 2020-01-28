package dk.jyskit.salescloud.application.pages.sales.masterdata;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class MasterDataPage extends ContentPage {
	public MasterDataPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), CorePageIds.SALES_MASTER_DATA));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		return new FormPanel(wicketId);
	}
}
