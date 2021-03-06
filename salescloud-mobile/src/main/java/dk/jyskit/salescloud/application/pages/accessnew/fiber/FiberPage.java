package dk.jyskit.salescloud.application.pages.accessnew.fiber;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class FiberPage extends ContentPage {
	public FiberPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class).findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_FIBER_BUNDLES));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		if (CoreSession.get().getContract().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER) {
			return new FiberErhvervPlusPanel(wicketId, null);
		} else if (CoreSession.get().getContract().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			return new FiberErhvervPanel(wicketId, null);
		} else {
			throw new IllegalArgumentException();
		}
	}
}
